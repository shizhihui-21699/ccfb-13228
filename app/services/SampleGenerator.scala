package services

import java.awt._
import java.awt.geom.Path2D
import java.awt.image.{BufferedImage, ConvolveOp, Kernel}
import java.io.{ByteArrayInputStream, ByteArrayOutputStream, File}
import javax.imageio.stream.MemoryCacheImageOutputStream
import javax.imageio.{IIOImage, ImageIO, ImageWriteParam}
import scala.util.Random

/** 样例合成：程序生成"真盖章"与"抠图伪造"两类合同扫描件 */
object SampleGenerator {
  val W = 1240; val H = 1754 // A4 @150dpi

  private val Company = "杭州云启科技有限公司"
  private val SubTitle = "合同专用章"

  def cjk(size: Float, style: Int = Font.PLAIN): Font = {
    val candidates = Seq("PingFang SC", "Hiragino Sans GB", "Songti SC", "STHeiti", "SimSun", "Microsoft YaHei", Font.SANS_SERIF)
    val base = candidates.map(new Font(_, style, 12)).find(_.canDisplay('同'))
      .getOrElse(new Font(Font.SANS_SERIF, style, 12))
    base.deriveFont(style, size)
  }

  /** 带噪声、纤维斑点和缓变光照梯度的纸面 */
  def paper(w: Int, h: Int, seed: Int, base: Int, gx: Double, gy: Double): BufferedImage = {
    val rnd = new Random(seed)
    val px = new Array[Int](w * h)
    var y = 0
    while (y < h) {
      var x = 0
      while (x < w) {
        val ill = 1.0 + gx * (x - w / 2.0) / w + gy * (y - h / 2.0) / h
        val n = rnd.nextGaussian() * 2.2
        val fiber = if (rnd.nextDouble() < 0.002) -12 else 0
        val v = (base * ill + n + fiber).round.toInt.max(0).min(255)
        px(y * w + x) = (v << 16) | ((v * 0.995).toInt << 8) | (v * 0.985).toInt // 微暖色纸
        x += 1
      }
      y += 1
    }
    val img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB)
    img.setRGB(0, 0, w, h, px, 0, w)
    img
  }

  private def drawContract(img: BufferedImage, rnd: Random): Unit = {
    val g = img.createGraphics()
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
    g.setColor(new Color(58, 58, 64))
    g.setFont(cjk(46, Font.BOLD))
    val title = "采  购  合  同  书"
    g.drawString(title, (img.getWidth - g.getFontMetrics.stringWidth(title)) / 2, 150)
    g.setFont(cjk(21))
    val lines = Seq(
      "甲方（需方）：杭州云启科技有限公司",
      "乙方（供方）：苏州恒信精密制造有限公司",
      "",
      "第一条  合同标的：精密结构件一批，详见附件清单。",
      "第二条  合同金额：人民币壹佰贰拾万元整（¥1,200,000.00）。",
      "第三条  交付期限：自合同生效之日起四十五日内交付。",
      "第四条  验收标准：按国家相关标准及双方技术协议执行。",
      "第五条  违约责任：任一方违约，应按合同金额的 5% 支付违约金。",
      "第六条  争议解决：协商不成的，提交甲方所在地人民法院诉讼解决。",
      "",
      "本合同一式两份，甲乙双方各执一份，自双方盖章之日起生效。"
    )
    var y = 260
    lines.foreach { l => if (l.nonEmpty) g.drawString(l, 110, y); y += 46 }
    g.setFont(cjk(24))
    g.drawString("甲方盖章：", 110, 1330)
    g.drawString("乙方盖章：", 110, 1470)
    g.drawString("签订日期：2026 年 11 月 18 日", 110, 1610)
    g.dispose()
  }

  /** 标准圆形公章：外圈 + 五角星 + 弧形公司名称 + 横排副标题 */
  def renderSeal(d: Int, company: String, sub: String): BufferedImage = {
    val img = new BufferedImage(d, d, BufferedImage.TYPE_INT_ARGB)
    val g = img.createGraphics()
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
    g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
    val red = new Color(198, 28, 36)
    g.setColor(red)
    val cx = d / 2f; val cy = d / 2f
    // 外圈
    g.setStroke(new BasicStroke(d * 0.038f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER))
    val inset = d * 0.045f
    g.draw(new java.awt.geom.Ellipse2D.Float(inset, inset, d - 2 * inset, d - 2 * inset))
    // 五角星
    val starR = d * 0.15f
    val star = new Path2D.Float()
    var k = 0
    while (k < 10) {
      val rr = if (k % 2 == 0) starR else starR * 0.382f
      val a = -Math.PI / 2 + k * Math.PI / 5
      val px = cx + rr * Math.cos(a); val py = cy + rr * Math.sin(a)
      if (k == 0) star.moveTo(px, py) else star.lineTo(px, py)
      k += 1
    }
    star.closePath()
    g.fill(star)
    // 弧形公司名称
    val n = company.length
    val span = Math.min(300.0, n * 19.0)
    val radius = d * 0.355
    g.setFont(cjk(d * 0.098f, Font.BOLD))
    val fm = g.getFontMetrics
    var i = 0
    while (i < n) {
      val aDeg = 90.0 + span / 2 - span * (if (n > 1) i.toDouble / (n - 1) else 0.5)
      val a = Math.toRadians(aDeg)
      val px = cx + radius * Math.cos(a); val py = cy - radius * Math.sin(a)
      val g2 = g.create().asInstanceOf[Graphics2D]
      g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
      g2.setColor(red); g2.setFont(g.getFont)
      g2.translate(px, py)
      g2.rotate(Math.toRadians(90.0 - aDeg))
      val ch = company.charAt(i).toString
      g2.drawString(ch, -fm.stringWidth(ch) / 2f, fm.getAscent * 0.35f)
      g2.dispose()
      i += 1
    }
    // 横排副标题
    g.setFont(cjk(d * 0.082f, Font.BOLD))
    val sw = g.getFontMetrics.stringWidth(sub)
    g.drawString(sub, cx - sw / 2f, cy + d * 0.30f)
    g.dispose()
    img
  }

  /** 真章油墨质感：透明度随纸面颗粒起伏、随机缺墨、轻微洇散 */
  private def inkTexture(src: BufferedImage, rnd: Random): BufferedImage = {
    val w = src.getWidth; val h = src.getHeight
    val gw = w / 7 + 2; val gh = h / 7 + 2
    val grid = Array.fill(gw * gh)(rnd.nextDouble())
    def noiseAt(x: Double, y: Double): Double = {
      val xi = x.toInt.max(0).min(gw - 2); val yi = y.toInt.max(0).min(gh - 2)
      val fx = x - xi; val fy = y - yi
      val v00 = grid(yi * gw + xi); val v10 = grid(yi * gw + xi + 1)
      val v01 = grid((yi + 1) * gw + xi); val v11 = grid((yi + 1) * gw + xi + 1)
      (v00 * (1 - fx) + v10 * fx) * (1 - fy) + (v01 * (1 - fx) + v11 * fx) * fy
    }
    val out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB)
    var y = 0
    while (y < h) {
      var x = 0
      while (x < w) {
        val p = src.getRGB(x, y)
        val a0 = (p >>> 24) & 0xff
        if (a0 > 0) {
          var grain = 0.45 + 0.55 * noiseAt(x / 7.0, y / 7.0)
          if (rnd.nextDouble() < 0.05) grain *= 0.35 // 缺墨点
          val na0 = (a0 * grain).round.toInt.max(0).min(255)
          // 锐化：真章油墨边缘接近二值，只留 1~2px 洇散（由后续轻微模糊产生）
          val na = if (na0 < 70) 0 else if (na0 > 190) 255 else na0
          val rr = ((p >> 16) & 0xff) * (0.92 + 0.16 * rnd.nextDouble())
          out.setRGB(x, y, (na << 24) | (rr.round.toInt.min(255) << 16) | (((p >> 8) & 0xff) << 8) | (p & 0xff))
        }
        x += 1
      }
      y += 1
    }
    blur(out, 1)
  }

  private def gauss(radius: Int): Kernel = {
    val n = radius * 2 + 1
    val sigma = radius / 2.0
    val data = Array.tabulate(n * n) { i =>
      val dx = i % n - radius; val dy = i / n - radius
      Math.exp(-(dx * dx + dy * dy) / (2 * sigma * sigma))
    }
    val sum = data.sum
    new Kernel(n, n, data.map(v => (v / sum).toFloat))
  }

  private def blur(img: BufferedImage, radius: Int): BufferedImage =
    new ConvolveOp(gauss(radius), ConvolveOp.EDGE_NO_OP, null).filter(img, null)

  private def rotate(src: BufferedImage, deg: Double): BufferedImage = {
    val out = new BufferedImage(src.getWidth, src.getHeight, BufferedImage.TYPE_INT_ARGB)
    val g = out.createGraphics()
    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR)
    g.rotate(Math.toRadians(deg), src.getWidth / 2.0, src.getHeight / 2.0)
    g.drawImage(src, 0, 0, null); g.dispose()
    out
  }

  private def scaleTo(src: BufferedImage, w: Int, h: Int): BufferedImage = {
    val out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB)
    val g = out.createGraphics()
    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR)
    g.drawImage(src, 0, 0, w, h, null); g.dispose()
    out
  }

  private def jpegRoundTrip(img: BufferedImage, quality: Float): BufferedImage = {
    val rgb = new BufferedImage(img.getWidth, img.getHeight, BufferedImage.TYPE_INT_RGB)
    val g = rgb.createGraphics(); g.drawImage(img, 0, 0, null); g.dispose()
    val writer = ImageIO.getImageWritersByFormatName("jpeg").next()
    val param = writer.getDefaultWriteParam
    param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT)
    param.setCompressionQuality(quality)
    val bos = new ByteArrayOutputStream()
    writer.setOutput(new MemoryCacheImageOutputStream(bos))
    writer.write(null, new IIOImage(rgb, null, null), param)
    writer.dispose()
    ImageIO.read(new ByteArrayInputStream(bos.toByteArray))
  }

  private def saveJpeg(img: BufferedImage, out: File, quality: Float): Unit = {
    val writer = ImageIO.getImageWritersByFormatName("jpeg").next()
    val param = writer.getDefaultWriteParam
    param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT)
    param.setCompressionQuality(quality)
    val ios = ImageIO.createImageOutputStream(out)
    writer.setOutput(ios)
    writer.write(null, new IIOImage(img, null, null), param)
    writer.dispose(); ios.close()
  }

  /** 羽化椭圆 alpha 蒙版（模拟抠图时的边缘融合） */
  private def featheredEllipse(w: Int, h: Int, feather: Int): BufferedImage = {
    val m = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY)
    val g = m.createGraphics()
    g.setColor(Color.BLACK); g.fillRect(0, 0, w, h)
    g.setColor(Color.WHITE)
    g.fillOval(feather, feather, w - 2 * feather, h - 2 * feather)
    g.dispose()
    blur(m, feather / 2)
  }

  private def compositeWithAlpha(doc: BufferedImage, patch: BufferedImage, alpha: BufferedImage, x0: Int, y0: Int): Unit = {
    var y = 0
    while (y < patch.getHeight) {
      var x = 0
      while (x < patch.getWidth) {
        val a = (alpha.getRGB(x, y) & 0xff) / 255.0
        if (a > 0.001) {
          val dp = doc.getRGB(x0 + x, y0 + y); val pp = patch.getRGB(x, y)
          val r = (((pp >> 16) & 0xff) * a + ((dp >> 16) & 0xff) * (1 - a)).round.toInt
          val g = (((pp >> 8) & 0xff) * a + ((dp >> 8) & 0xff) * (1 - a)).round.toInt
          val b = ((pp & 0xff) * a + (dp & 0xff) * (1 - a)).round.toInt
          doc.setRGB(x0 + x, y0 + y, (r << 16) | (g << 8) | b)
        }
        x += 1
      }
      y += 1
    }
  }

  /** 真盖章样例：印章直接盖在本文档上，整图统一 JPEG 压缩 */
  def genuine(out: File): File = {
    out.getParentFile.mkdirs()
    val doc = paper(W, H, 7, 246, 0.02, -0.05)
    drawContract(doc, new Random(1001))
    val seal = rotate(renderSeal(360, Company, SubTitle), 6.5)
    val inked = inkTexture(seal, new Random(55))
    val g = doc.createGraphics()
    g.drawImage(inked, 660, 1150, null)
    g.dispose()
    saveJpeg(doc, out, 0.92f)
    out
  }

  /** 伪造样例：从"别的文件"抠来的印章块——不同底色、缩放重采样、
    * 局部 JPEG 重压缩、羽化边缘，最终文档存 PNG（无整图压缩） */
  def forged(out: File): File = {
    out.getParentFile.mkdirs()
    val doc = paper(W, H, 8, 240, -0.03, 0.04)
    drawContract(doc, new Random(2002))
    val pw = 460; val ph = 460
    val patch = paper(pw, ph, 9, 255, 0.0, 0.0) // 来源文件底色更亮 → 光照不一致
    val sealScaled = scaleTo(renderSeal(520, Company, SubTitle), 396, 396) // 重采样
    val gp = patch.createGraphics()
    gp.drawImage(sealScaled, (pw - 396) / 2, (ph - 396) / 2, null)
    gp.dispose()
    val compressed = jpegRoundTrip(patch, 0.5f) // 局部二次压缩痕迹
    val alpha = featheredEllipse(pw, ph, 26) // 边缘融合
    compositeWithAlpha(doc, compressed, alpha, 640, 1140)
    ImageIO.write(doc, "png", out)
    out
  }
}
