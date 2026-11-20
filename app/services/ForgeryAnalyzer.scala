package services

import models.{IndicatorResult, SealRegion}
import org.opencv.core._
import org.opencv.imgproc.Imgproc
import scala.collection.mutable.ArrayBuffer

/** 图像采样上下文：一次性取出灰度/饱和度/红色度，避免反复 JNI 调用 */
class ImageCtx(val img: Mat) {
  val w: Int = img.cols()
  val h: Int = img.rows()
  val gray = new Mat; Imgproc.cvtColor(img, gray, Imgproc.COLOR_BGR2GRAY)
  val hsv = new Mat;  Imgproc.cvtColor(img, hsv, Imgproc.COLOR_BGR2HSV)
  val grayB = new Array[Byte](w * h);     gray.get(0, 0, grayB)
  val hsvB  = new Array[Byte](w * h * 3); hsv.get(0, 0, hsvB)
  val bgrB  = new Array[Byte](w * h * 3); img.get(0, 0, bgrB)

  @inline def gAt(i: Int): Int = grayB(i) & 0xff
  @inline def sAt(i: Int): Int = hsvB(i * 3 + 1) & 0xff
  /** 红色度：R 超出 BG 均值的部分，墨迹处大、纸面处接近 0 */
  @inline def redAt(i: Int): Int =
    (bgrB(i * 3 + 2) & 0xff) - ((bgrB(i * 3) & 0xff) + (bgrB(i * 3 + 1) & 0xff)) / 2
  /** 纸面像素：低饱和、不泛红、够亮（排除墨迹、红晕和深色文字） */
  @inline def isPaper(i: Int): Boolean = sAt(i) < 80 && redAt(i) < 30 && gAt(i) > 150
}

object ForgeryAnalyzer {

  @inline private def clamp01(v: Double): Double = Math.max(0.0, Math.min(1.0, v))

  def regionMask(w: Int, h: Int, r: SealRegion): Mat = {
    val m = Mat.zeros(h, w, CvType.CV_8UC1)
    Imgproc.fillPoly(m, java.util.List.of(new MatOfPoint(r.contour: _*)), new Scalar(255))
    m
  }

  private def maskBytes(m: Mat): Array[Byte] = {
    val b = new Array[Byte](m.rows() * m.cols()); m.get(0, 0, b); b
  }

  private def erodeM(mask: Mat, it: Int): Mat = {
    val k = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(3, 3))
    val d = new Mat; Imgproc.erode(mask, d, k, new Point(-1, -1), it); d
  }

  private def dilateM(mask: Mat, it: Int): Mat = {
    val k = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(3, 3))
    val d = new Mat; Imgproc.dilate(mask, d, k, new Point(-1, -1), it); d
  }

  def analyze(ctx: ImageCtx, r: SealRegion): List[IndicatorResult] = {
    val mask = regionMask(ctx.w, ctx.h, r)
    List(edgeBlend(ctx, r), halo(ctx, mask), lighting(ctx, mask), resampleNoise(ctx, mask), blockiness(ctx, r, mask))
  }

  /** 1. 墨迹边缘：沿质心射线测量墨迹→纸面过渡带宽度。
    * 真章油墨边缘锐利且不均匀；抠图后整体羽化/模糊会让过渡带宽而整齐。 */
  def edgeBlend(ctx: ImageCtx, r: SealRegion): IndicatorResult = {
    val mom = Imgproc.moments(new MatOfPoint(r.contour: _*))
    if (mom.m00 == 0) return IndicatorResult("edge", "墨迹边缘", 0, "无法计算轮廓质心")
    val cx = mom.m10 / mom.m00; val cy = mom.m01 / mom.m00
    val maxR = (Math.max(r.w, r.h) * 0.8 + 30).toInt
    val widths = ArrayBuffer.empty[Double]
    var ang = 0.0
    while (ang < 360) {
      val dx = Math.cos(Math.toRadians(ang)); val dy = Math.sin(Math.toRadians(ang))
      val samples = new Array[Int](maxR)
      var i = 0
      while (i < maxR) {
        val x = (cx + dx * i).round.toInt; val y = (cy + dy * i).round.toInt
        samples(i) = if (x >= 0 && y >= 0 && x < ctx.w && y < ctx.h) ctx.redAt(y * ctx.w + x) else 0
        i += 1
      }
      var peak = 0; var pi = 0
      i = 0; while (i < maxR) { if (samples(i) > peak) { peak = samples(i); pi = i }; i += 1 }
      if (peak > 60) {
        val hi = 0.75 * peak; val lo = 0.25 * peak
        var a = pi; while (a < maxR - 1 && samples(a) > hi) a += 1
        var b = a; while (b < maxR - 1 && samples(b) > lo) b += 1
        if (b < maxR - 1 && b > a) widths += (b - a).toDouble
      }
      ang += 3
    }
    if (widths.length < 20)
      IndicatorResult("edge", "墨迹边缘", 0, s"有效边缘采样过少（${widths.length} 条射线），无法评估")
    else {
      val mean = widths.sum / widths.length
      val sd = Math.sqrt(widths.map(v => (v - mean) * (v - mean)).sum / widths.length)
      val cv = sd / mean
      // 真章油墨边缘宽度不一（cv 高）；数字抠图边缘整齐划一（cv 低）或被整体羽化（极宽）
      val sUniform = clamp01((0.6 - cv) / 0.4)
      val sWide = clamp01((mean - 9.0) / 4.0)
      val score = (sUniform * 0.5 + sWide * 0.5) * 100
      IndicatorResult("edge", "墨迹边缘", (score * 10).round / 10.0,
        f"墨迹边缘过渡带平均宽 $mean%.1f px，变异系数 $cv%.2f（真章边缘宽窄不一 cv 较高；过低说明边缘整齐得反常，过宽说明被整体羽化）")
    }
  }

  /** 2. 拼接融合痕迹（光晕）：印章外侧径向亮度剖面。
    * 粘贴图块的羽化边界会在印章外形成一圈缓变的亮度过渡带；真章外侧纸面亮度平坦。 */
  def halo(ctx: ImageCtx, mask: Mat): IndicatorResult = {
    val radii = (5 to 65 by 10).toList
    val disks = radii.map(k => maskBytes(dilateM(mask, k)))
    val bandMeans = radii.indices.map { i =>
      var s = 0.0; var n = 0L
      var p = 0
      while (p < ctx.w * ctx.h) {
        if (disks(i)(p) != 0 && (i == 0 || disks(i - 1)(p) == 0) && ctx.isPaper(p)) { s += ctx.gAt(p); n += 1 }
        p += 1
      }
      if (n < 100) Double.NaN else s / n
    }
    val diffs = bandMeans.sliding(2).collect {
      case Seq(a, b) if !a.isNaN && !b.isNaN => Math.abs(b - a)
    }.toList
    if (diffs.isEmpty) IndicatorResult("halo", "拼接融合痕迹", 0, "印章外侧纸面采样不足，无法评估")
    else {
      val maxDiff = diffs.max
      val score = clamp01((maxDiff - 2.0) / 4.0) * 100
      val profile = bandMeans.map(m => if (m.isNaN) "  -- " else f"$m%6.1f").mkString(" → ")
      IndicatorResult("halo", "拼接融合痕迹", (score * 10).round / 10.0,
        f"印章外 5~65px 纸面亮度剖面：$profile（相邻环带最大跳变 $maxDiff%.1f 灰度级；粘贴块羽化边界会形成缓变台阶）")
    }
  }

  /** 3. 光照一致性：印面透出的纸底亮度 vs 远处纸面亮度 */
  def lighting(ctx: ImageCtx, mask: Mat): IndicatorResult = {
    val inner = maskBytes(erodeM(mask, 9))
    val ringA = maskBytes(dilateM(mask, 110))
    val ringB = maskBytes(dilateM(mask, 60))
    var inSum = 0.0; var inN = 0L; var ringSum = 0.0; var ringN = 0L
    var i = 0
    while (i < ctx.w * ctx.h) {
      if (ctx.isPaper(i)) {
        if (inner(i) != 0) { inSum += ctx.gAt(i); inN += 1 }
        else if (ringA(i) != 0 && ringB(i) == 0) { ringSum += ctx.gAt(i); ringN += 1 }
      }
      i += 1
    }
    if (inN < 200 || ringN < 200)
      IndicatorResult("light", "光照一致性", 0, "纸面采样点不足，无法评估")
    else {
      val inM = inSum / inN; val ringM = ringSum / ringN
      val diff = (inM - ringM) / ringM
      val score = clamp01((Math.abs(diff) - 0.015) / 0.05) * 100
      IndicatorResult("light", "光照一致性", (score * 10).round / 10.0,
        f"印面纸底亮度 $inM%.1f，周边纸面（60~110px 环带）$ringM%.1f，偏差 ${diff * 100}%.1f%%（|偏差|超过约 2%% 可疑，粘贴块常带来原图底色）")
    }
  }

  /** 4. 局部重采样/二次压缩：印面纸底噪声强度 vs 远处纸面噪声强度 */
  def resampleNoise(ctx: ImageCtx, mask: Mat): IndicatorResult = {
    val med = new Mat; Imgproc.medianBlur(ctx.gray, med, 5)
    val nz = new Mat; Core.absdiff(ctx.gray, med, nz)
    val nb = new Array[Byte](ctx.w * ctx.h); nz.get(0, 0, nb)
    val inner = maskBytes(erodeM(mask, 9))
    val ringA = maskBytes(dilateM(mask, 110)); val ringB = maskBytes(dilateM(mask, 60))

    // 用 MAD（中位数绝对偏差）代替标准差：印面纸底常夹杂墨迹邻近像素，
    // MAD 对这类少量污染鲁棒，而粘贴块的压缩噪声遍布全区域、无法被掩盖
    def mad(sel: Int => Boolean): Double = {
      val vs = ArrayBuffer.empty[Int]
      var i = 0
      while (i < ctx.w * ctx.h) {
        if (ctx.isPaper(i) && sel(i)) vs += (nb(i) & 0xff)
        i += 1
      }
      if (vs.length < 200) return -1
      val sorted = vs.sorted
      val med = sorted(sorted.length / 2)
      val devs = sorted.map(v => Math.abs(v - med)).sorted
      devs(devs.length / 2).toDouble
    }
    val inS = mad(i => inner(i) != 0)
    val outS = mad(i => ringA(i) != 0 && ringB(i) == 0)
    if (inS < 0 || outS <= 0) IndicatorResult("noise", "局部重采样", 0, "采样点不足，无法评估")
    else {
      val ratio = (inS + 0.5) / (outS + 0.5)
      val dev = Math.max(ratio, 1 / ratio)
      val score = clamp01((dev - 1.2) / 0.8) * 100
      IndicatorResult("noise", "局部重采样", (score * 10).round / 10.0,
        f"印面噪声强度（MAD）$inS%.2f vs 周边 $outS%.2f（比值 $ratio%.2f；明显偏离 1 说明该区域经历过缩放、模糊或二次压缩）")
    }
  }

  /** 5. JPEG 块效应网格：对 8x8 网格的所有偏移取边界强度，网格显著性 = 最大偏移/中位偏移。
    * 与网格绝对对齐无关，区域内外的显著性对比可发现局部粘贴的 JPEG 图块。 */
  def blockiness(ctx: ImageCtx, r: SealRegion, mask: Mat): IndicatorResult = {
    val ink = maskBytes(dilateM(mask, 2))

    /** @return 网格显著性（≈1 表示无网格） */
    def gridStrength(x0: Int, y0: Int, x1: Int, y1: Int, exclude: Int => Boolean): Double = {
      val sums = new Array[Double](16); val ns = new Array[Long](16)
      var y = y0
      while (y < y1) {
        var x = x0
        while (x < x1) {
          val i = y * ctx.w + x
          if (ctx.isPaper(i) && ink(i) == 0 && !exclude(i)) {
            if (x > 0) { val o = x & 7; sums(o) += Math.abs(ctx.gAt(i) - ctx.gAt(i - 1)); ns(o) += 1 }
            if (y > 0) { val o = 8 + (y & 7); sums(o) += Math.abs(ctx.gAt(i) - ctx.gAt(i - ctx.w)); ns(o) += 1 }
          }
          x += 1
        }
        y += 1
      }
      val means = sums.indices.filter(ns(_) > 50).map(o => sums(o) / ns(o)).sorted
      if (means.length < 8) 1.0
      else {
        val med = (means(means.length / 2 - 1) + means(means.length / 2)) / 2
        if (med <= 0) 1.0 else means.last / med
      }
    }

    val pad = 6
    val x0 = Math.max(1, r.x - pad); val y0 = Math.max(1, r.y - pad)
    val x1 = Math.min(ctx.w - 1, r.x + r.w + pad); val y1 = Math.min(ctx.h - 1, r.y + r.h + pad)
    val inS = gridStrength(x0, y0, x1, y1, _ => false)
    val m = 80
    val ox0 = Math.max(1, x0 - m); val oy0 = Math.max(1, y0 - m)
    val ox1 = Math.min(ctx.w - 1, x1 + m); val oy1 = Math.min(ctx.h - 1, y1 + m)
    val outS = gridStrength(ox0, oy0, ox1, oy1, i => {
      val yy = i / ctx.w; val xx = i % ctx.w
      xx >= x0 && xx < x1 && yy >= y0 && yy < y1
    })
    val ratio = inS / Math.max(1.0, outS)
    val score = clamp01((ratio - 1.08) / 0.35) * 100
    IndicatorResult("block", "JPEG 块效应异常", (score * 10).round / 10.0,
      f"区域内 8x8 网格显著性 $inS%.2f vs 区域外 $outS%.2f（整图统一压缩时两者接近；局部粘贴的 JPEG 图块会出现独立网格）")
  }
}
