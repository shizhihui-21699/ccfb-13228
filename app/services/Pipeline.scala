package services

import models._
import org.opencv.core.{Mat, Size}
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc
import util.Imaging
import java.awt.{BasicStroke, Color}
import java.io.File
import java.nio.file.{Files, StandardCopyOption}
import javax.imageio.ImageIO

/** 分析流水线：定位 → 单项指标 → 克隆检测 → 登记章比对 → 标注出图 */
object Pipeline {

  private val Weights = Map("edge" -> 0.15, "halo" -> 0.25, "light" -> 0.20, "noise" -> 0.20, "block" -> 0.20)

  def analyze(srcFile: File, id: String): Report = {
    Imaging.ensureCv()
    val outDir = new File(s"data/results/$id"); outDir.mkdirs()
    val ext = srcFile.getName.split("\\.").lastOption.map(_.toLowerCase).getOrElse("png")
    val uploadName = if (ext.length <= 5 && ext.matches("[a-z0-9]+")) s"upload.$ext" else "upload.png"
    val uploadCopy = new File(outDir, uploadName)
    if (srcFile.getAbsolutePath != uploadCopy.getAbsolutePath)
      Files.copy(srcFile.toPath, uploadCopy.toPath, StandardCopyOption.REPLACE_EXISTING)

    val loaded = Imgcodecs.imread(uploadCopy.getAbsolutePath)
    if (loaded.empty())
      return Report(id, Nil, 0.0, "无法读取图像文件", uploadName, "")

    val (img, scale) = resizeToMax(loaded, 1800)
    val ctx = new ImageCtx(img)
    val (regions, redMask) = SealDetector.detect(img)

    if (regions.isEmpty)
      return Report(id, Nil, 0.0, "未检测到印章区域（请确认图像中有红色印章且清晰度足够）", uploadName, "")

    val cloneInds = detectClones(redMask, regions)

    val seals = regions.zipWithIndex.map { case (r, idx) =>
      val inds = ForgeryAnalyzer.analyze(ctx, r) ++ cloneInds.get(idx).toList
      val matches = SealMatcher.matchAll(redMask, r, idx, outDir)
      val susp = suspicion(inds)
      SealResult(idx,
        (r.x / scale).round.toInt, (r.y / scale).round.toInt,
        (r.w / scale).round.toInt, (r.h / scale).round.toInt,
        inds, susp, level(susp), matches)
    }

    val annotatedName = "annotated.png"
    saveAnnotated(img, regions, seals, new File(outDir, annotatedName))

    val overall = seals.map(_.suspicion).max
    val nHigh = seals.count(_.suspicion >= 65)
    val summary =
      if (nHigh > 0) s"检测到 ${seals.length} 枚印章，其中 $nHigh 枚存在明显伪造迹象，建议人工复核"
      else if (seals.exists(_.suspicion >= 35)) s"检测到 ${seals.length} 枚印章，部分指标存疑，建议结合登记章比对结果复核"
      else s"检测到 ${seals.length} 枚印章，各项指标未见明显异常"
    Report(id, seals, overall, summary, uploadName, annotatedName)
  }

  private def suspicion(inds: List[IndicatorResult]): Double = {
    val plain = inds.filter(_.key != "clone")
    val weighted = plain.map(i => i.score * Weights.getOrElse(i.key, 0.0)).sum
    // 强证据增强：一两项指标特别突出时不应被其他无关指标稀释
    val top2 = plain.map(_.score).sortBy(-_).take(2)
    val boosted = if (top2.nonEmpty) 0.55 * weighted + 0.45 * (top2.sum / top2.length) else 0.0
    val cloneBoost = inds.find(_.key == "clone").map(_.score).getOrElse(0.0)
    (Seq(weighted, boosted, cloneBoost).max * 10).round / 10.0
  }

  private def level(s: Double): String =
    if (s >= 65) "高风险" else if (s >= 35) "存疑" else "未见明显异常"

  /** 同一文档内出现两枚图像级完全一致的印章 → 复制粘贴 */
  private def detectClones(redMask: Mat, regions: List[SealRegion]): Map[Int, IndicatorResult] = {
    if (regions.length < 2) return Map.empty
    val norms = regions.map(r => SealMatcher.normalizeCrop(redMask, r, 128))
    val pairs = for {
      i <- norms.indices; j <- norms.indices if i < j
      (v, _) = SealMatcher.bestAlign(norms(i), norms(j), range = 12, step = 2) if v > 0.92
    } yield (i, j, v)
    pairs.flatMap { case (i, j, v) =>
      val ind = IndicatorResult("clone", "重复印章", 95,
        f"与本文档另一枚印章图像完全一致（IoU $v%.2f），真实盖章不可能两次完全相同，疑似复制粘贴")
      List(i -> ind, j -> ind)
    }.toMap
  }

  private def resizeToMax(m: Mat, maxDim: Int): (Mat, Double) = {
    val d = Math.max(m.cols(), m.rows())
    if (d <= maxDim) (m, 1.0)
    else {
      val s = maxDim.toDouble / d
      val out = new Mat
      Imgproc.resize(m, out, new Size(m.cols() * s, m.rows() * s))
      (out, s)
    }
  }

  /** 标注图：风险等级配色画框 + 中文标签 */
  private def saveAnnotated(img: Mat, regions: List[SealRegion], seals: List[SealResult], out: File): Unit = {
    val bi = Imaging.toBufferedImage(img)
    val g = bi.createGraphics()
    g.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON)
    val font = SampleGenerator.cjk(26, java.awt.Font.BOLD)
    g.setFont(font)
    seals.zip(regions).foreach { case (s, r) =>
      val color =
        if (s.suspicion >= 65) new Color(220, 40, 40)
        else if (s.suspicion >= 35) new Color(230, 150, 20)
        else new Color(40, 160, 60)
      g.setColor(color)
      g.setStroke(new BasicStroke(5f))
      g.drawRect(r.x - 8, r.y - 8, r.w + 16, r.h + 16)
      val label = s"印章#${s.index + 1} 风险 ${s.suspicion.toInt}%"
      val tw = g.getFontMetrics.stringWidth(label)
      val ly = Math.max(30, r.y - 16)
      g.fillRect(r.x - 8, ly - 28, tw + 16, 34)
      g.setColor(Color.WHITE)
      g.drawString(label, r.x, ly - 4)
    }
    g.dispose()
    ImageIO.write(bi, "png", out)
  }
}
