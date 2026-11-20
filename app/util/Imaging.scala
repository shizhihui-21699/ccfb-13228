package util

import org.opencv.core.{CvType, Mat}
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

object Imaging {
  @volatile private var cvLoaded = false

  /** 加载 OpenCV 原生库（幂等）。注意：dev 模式改代码热重载后需重启 run，
    * 否则原生库会在新 classloader 里重复加载报错。 */
  def ensureCv(): Unit = synchronized {
    if (!cvLoaded) {
      nu.pattern.OpenCV.loadLocally()
      cvLoaded = true
    }
  }

  /** BufferedImage -> BGR Mat */
  def toMat(src: BufferedImage): Mat = {
    val w = src.getWidth; val h = src.getHeight
    val img =
      if (src.getType == BufferedImage.TYPE_INT_RGB) src
      else {
        val c = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB)
        val g = c.createGraphics(); g.drawImage(src, 0, 0, null); g.dispose(); c
      }
    val px = new Array[Int](w * h)
    img.getRGB(0, 0, w, h, px, 0, w)
    val bytes = new Array[Byte](w * h * 3)
    var i = 0
    while (i < w * h) {
      val p = px(i)
      bytes(i * 3) = (p & 0xff).toByte
      bytes(i * 3 + 1) = ((p >> 8) & 0xff).toByte
      bytes(i * 3 + 2) = ((p >> 16) & 0xff).toByte
      i += 1
    }
    val m = new Mat(h, w, CvType.CV_8UC3)
    m.put(0, 0, bytes)
    m
  }

  /** BGR/灰度 Mat -> BufferedImage */
  def toBufferedImage(m: Mat): BufferedImage = {
    val w = m.cols(); val h = m.rows(); val ch = m.channels()
    val bytes = new Array[Byte](w * h * ch)
    m.get(0, 0, bytes)
    if (ch == 1) {
      val img = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY)
      img.getRaster.setDataElements(0, 0, w, h, bytes)
      img
    } else {
      val img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB)
      val px = new Array[Int](w * h)
      var i = 0
      while (i < w * h) {
        val b = bytes(i * 3) & 0xff; val g = bytes(i * 3 + 1) & 0xff; val r = bytes(i * 3 + 2) & 0xff
        px(i) = (r << 16) | (g << 8) | b
        i += 1
      }
      img.setRGB(0, 0, w, h, px, 0, w)
      img
    }
  }

  def savePng(m: Mat, f: File): Unit = ImageIO.write(toBufferedImage(m), "png", f)
}
