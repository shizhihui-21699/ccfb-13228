package services

import models.{MatchResult, SealRegion}
import org.opencv.core._
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc
import java.io.File
import scala.jdk.CollectionConverters._

/** 与登记印章的比对：归一化墨迹掩码 + 旋转搜索对齐 + IoU / Hu 矩形状距离 + 差异图 */
object SealMatcher {
  val N = 256

  /** 把红色墨迹掩码按印章外接框裁剪、补齐成正方形并缩放到 n×n */
  def normalizeCrop(redMask: Mat, r: SealRegion, n: Int = N): Mat = {
    val side = Math.max(8, (Math.max(r.w, r.h) * 1.15).toInt)
    val cx = r.x + r.w / 2; val cy = r.y + r.h / 2
    val x0 = Math.max(0, cx - side / 2); val y0 = Math.max(0, cy - side / 2)
    val x1 = Math.min(redMask.cols(), cx + side / 2); val y1 = Math.min(redMask.rows(), cy + side / 2)
    val crop = new Mat(redMask, new Rect(x0, y0, Math.max(1, x1 - x0), Math.max(1, y1 - y0)))
    val top = (side - crop.rows()) / 2; val left = (side - crop.cols()) / 2
    val bottom = side - crop.rows() - top; val right = side - crop.cols() - left
    val sq = new Mat
    Core.copyMakeBorder(crop, sq, Math.max(0, top), Math.max(0, bottom),
      Math.max(0, left), Math.max(0, right), Core.BORDER_CONSTANT, new Scalar(0))
    val out = new Mat
    Imgproc.resize(sq, out, new Size(n, n))
    Imgproc.threshold(out, out, 127, 255, Imgproc.THRESH_BINARY)
    out
  }

  private def rotate(m: Mat, angle: Double): Mat = {
    val mm = Imgproc.getRotationMatrix2D(new Point(m.cols() / 2.0, m.rows() / 2.0), angle, 1.0)
    val out = new Mat
    Imgproc.warpAffine(m, out, mm, m.size(), Imgproc.INTER_NEAREST, Core.BORDER_CONSTANT, new Scalar(0))
    out
  }

  def iou(a: Mat, b: Mat): Double = {
    val inter = new Mat; Core.bitwise_and(a, b, inter)
    val uni = new Mat; Core.bitwise_or(a, b, uni)
    Core.countNonZero(inter).toDouble / Math.max(1, Core.countNonZero(uni))
  }

  /** 旋转搜索最佳对齐（盖章本身常带旋转） */
  def bestAlign(q: Mat, ref: Mat, range: Int = 24, step: Int = 2): (Double, Int) = {
    var bestV = -1.0; var bestA = 0
    var a = -range
    while (a <= range) {
      val v = iou(rotate(q, a), ref)
      if (v > bestV) { bestV = v; bestA = a }
      a += step
    }
    (bestV, bestA)
  }

  private def largestContour(mask: Mat): Option[MatOfPoint] = {
    val cs = new java.util.ArrayList[MatOfPoint]()
    Imgproc.findContours(mask.clone(), cs, new Mat, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE)
    cs.asScala.toList.sortBy(c => -Imgproc.contourArea(c)).headOption
  }

  /** Hu 矩形状距离（外轮廓） */
  def shapeDist(a: Mat, b: Mat): Double =
    (largestContour(a), largestContour(b)) match {
      case (Some(ca), Some(cb)) => Imgproc.matchShapes(ca, cb, Imgproc.CONTOURS_MATCH_I1, 0)
      case _ => 999.0
    }

  /** 差异图：灰=两者一致，红=登记章有而检材缺，蓝=检材多出 */
  def diffImage(q: Mat, ref: Mat, angle: Int): Mat = {
    val qr = rotate(q, angle)
    val n = q.rows()
    val qb = new Array[Byte](n * n); qr.get(0, 0, qb)
    val rb = new Array[Byte](n * n); ref.get(0, 0, rb)
    val px = new Array[Byte](n * n * 3)
    var i = 0
    while (i < n * n) {
      val hasQ = qb(i) != 0; val hasR = rb(i) != 0
      val (b, g, rr) =
        if (hasQ && hasR) (200, 200, 200)
        else if (hasR) (60, 60, 240)
        else if (hasQ) (250, 170, 60)
        else (255, 255, 255)
      px(i * 3) = b.toByte; px(i * 3 + 1) = g.toByte; px(i * 3 + 2) = rr.toByte
      i += 1
    }
    val out = new Mat(n, n, CvType.CV_8UC3)
    out.put(0, 0, px)
    out
  }

  def matchAll(redMask: Mat, r: SealRegion, sealIdx: Int, outDir: File): List[MatchResult] = {
    val q = normalizeCrop(redMask, r)
    SealRegistry.loadAll().map { case (name, ref) =>
      val (v, ang) = bestAlign(q, ref)
      val sd = shapeDist(q, ref)
      val diffName = s"diff_${sealIdx}_$name.png"
      Imgcodecs.imwrite(new File(outDir, diffName).getAbsolutePath, diffImage(q, ref, ang))
      val verdict =
        if (v >= 0.9) "与登记印章高度一致（注意：图像级完全一致本身也可能是原章图片被复制粘贴）"
        else if (v >= 0.75) "与登记印章基本一致，局部文字/边框细节有差异"
        else "与登记印章差异明显，可能不是同一枚章"
      MatchResult(name, (sd * 10000).round / 10000.0, (v * 1000).round / 1000.0, ang, diffName, verdict)
    }.sortBy(m => -m.iou)
  }
}
