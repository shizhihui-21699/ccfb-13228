package services

import org.opencv.core.Mat
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc
import java.io.File
import java.nio.file.{Files, StandardCopyOption}

/** 登记印章库：data/seals/<名称>.mask.png 存归一化墨迹掩码，<名称>.src.png 留原始上传件 */
object SealRegistry {
  private val dir = new File("data/seals")
  dir.mkdirs()

  private def safe(name: String): String =
    name.replaceAll("[^0-9A-Za-z\\u4e00-\\u9fa5_-]", "_")

  def list(): List[String] =
    Option(dir.listFiles()).toList.flatten.map(_.getName)
      .filter(_.endsWith(".mask.png")).map(_.stripSuffix(".mask.png")).sorted

  def register(name: String, imgFile: File): Either[String, Unit] = {
    val img = Imgcodecs.imread(imgFile.getAbsolutePath)
    if (img.empty()) Left("无法读取图像文件")
    else {
      val (regions, red) = SealDetector.detect(img)
      regions.headOption match {
        case None => Left("未在图像中检测到印章区域")
        case Some(r) =>
          val norm = SealMatcher.normalizeCrop(red, r)
          val n = safe(name)
          Imgcodecs.imwrite(new File(dir, s"$n.mask.png").getAbsolutePath, norm)
          Files.copy(imgFile.toPath, new File(dir, s"$n.src.png").toPath, StandardCopyOption.REPLACE_EXISTING)
          Right(())
      }
    }
  }

  def loadAll(): List[(String, Mat)] =
    list().map { n =>
      val m = Imgcodecs.imread(new File(dir, s"$n.mask.png").getAbsolutePath, Imgcodecs.IMREAD_GRAYSCALE)
      Imgproc.threshold(m, m, 127, 255, Imgproc.THRESH_BINARY)
      (n, m)
    }
}
