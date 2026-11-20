package services

import models.SealRegion
import org.opencv.core._
import org.opencv.imgproc.Imgproc
import scala.jdk.CollectionConverters._

/** 印章区域定位：HSV 红色分割 + 形态学清理 + 轮廓筛选 */
object SealDetector {

  /** @return (检测到的印章区域列表, 红色墨迹掩码 CV_8UC1 0/255) */
  def detect(img: Mat): (List[SealRegion], Mat) = {
    val hsv = new Mat
    Imgproc.cvtColor(img, hsv, Imgproc.COLOR_BGR2HSV)
    // 红色在 HSV 色相环两端
    val m1 = new Mat; Core.inRange(hsv, new Scalar(0, 50, 60), new Scalar(12, 255, 255), m1)
    val m2 = new Mat; Core.inRange(hsv, new Scalar(150, 50, 60), new Scalar(180, 255, 255), m2)
    val red = new Mat; Core.add(m1, m2, red)

    val mask = red.clone()
    val kOpen = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(3, 3))
    val kClose = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(11, 11))
    Imgproc.morphologyEx(mask, mask, Imgproc.MORPH_OPEN, kOpen)
    Imgproc.morphologyEx(mask, mask, Imgproc.MORPH_CLOSE, kClose)

    val contours = new java.util.ArrayList[MatOfPoint]()
    Imgproc.findContours(mask, contours, new Mat, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE)
    val imgArea = img.rows() * img.cols().toDouble

    val regions = contours.asScala.toList.flatMap { c =>
      val area = Imgproc.contourArea(c)
      val rect = Imgproc.boundingRect(c)
      val peri = Imgproc.arcLength(new MatOfPoint2f(c.toArray: _*), true)
      val circ = if (peri > 0) 4 * Math.PI * area / (peri * peri) else 0.0
      val aspect = rect.width.toDouble / Math.max(1, rect.height)
      if (area > imgArea * 0.0003 && area < imgArea * 0.35 &&
          aspect > 0.35 && aspect < 2.8 && circ > 0.2)
        Some(SealRegion(rect.x, rect.y, rect.width, rect.height, circ, area, c.toArray))
      else None
    }.sortBy(r => -r.area).take(8)

    (regions, red)
  }
}
