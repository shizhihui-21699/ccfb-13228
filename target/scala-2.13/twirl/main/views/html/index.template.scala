
package views.html

import _root_.play.twirl.api.TwirlFeatureImports._
import _root_.play.twirl.api.TwirlHelperImports._
import _root_.play.twirl.api.Html
import _root_.play.twirl.api.JavaScript
import _root_.play.twirl.api.Txt
import _root_.play.twirl.api.Xml
import models._
import controllers._
import play.api.i18n._
import views.html._
import play.api.templates.PlayMagic._
import play.api.mvc._
import play.api.data._

object index extends _root_.play.twirl.api.BaseScalaTemplate[play.twirl.api.HtmlFormat.Appendable,_root_.play.twirl.api.Format[play.twirl.api.HtmlFormat.Appendable]](play.twirl.api.HtmlFormat) with _root_.play.twirl.api.Template1[List[String],play.twirl.api.HtmlFormat.Appendable] {

  /**/
  def apply/*1.2*/(seals: List[String]):play.twirl.api.HtmlFormat.Appendable = {
    _display_ {
      {


Seq[Any](format.raw/*2.1*/("""<!DOCTYPE html>
<html lang="zh-CN">
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>印章图像鉴别服务</title>
<style>
  body """),format.raw/*9.8*/("""{"""),format.raw/*9.9*/(""" """),format.raw/*9.10*/("""font-family: -apple-system, "PingFang SC", "Microsoft YaHei", sans-serif;
         max-width: 860px; margin: 24px auto; padding: 0 16px; color: #222; """),format.raw/*10.77*/("""}"""),format.raw/*10.78*/("""
  """),format.raw/*11.3*/("""h1 """),format.raw/*11.6*/("""{"""),format.raw/*11.7*/(""" """),format.raw/*11.8*/("""font-size: 24px; """),format.raw/*11.25*/("""}"""),format.raw/*11.26*/(""" """),format.raw/*11.27*/("""h2 """),format.raw/*11.30*/("""{"""),format.raw/*11.31*/(""" """),format.raw/*11.32*/("""font-size: 17px; margin-top: 0; """),format.raw/*11.64*/("""}"""),format.raw/*11.65*/("""
  """),format.raw/*12.3*/(""".card """),format.raw/*12.9*/("""{"""),format.raw/*12.10*/(""" """),format.raw/*12.11*/("""border: 1px solid #ddd; border-radius: 10px; padding: 16px 20px; margin: 16px 0; """),format.raw/*12.92*/("""}"""),format.raw/*12.93*/("""
  """),format.raw/*13.3*/(""".btn """),format.raw/*13.8*/("""{"""),format.raw/*13.9*/(""" """),format.raw/*13.10*/("""display: inline-block; padding: 8px 16px; margin: 4px 8px 4px 0; border-radius: 6px;
         background: #c8102e; color: #fff; text-decoration: none; """),format.raw/*14.67*/("""}"""),format.raw/*14.68*/("""
  """),format.raw/*15.3*/(""".btn.gray """),format.raw/*15.13*/("""{"""),format.raw/*15.14*/(""" """),format.raw/*15.15*/("""background: #555; """),format.raw/*15.33*/("""}"""),format.raw/*15.34*/("""
  """),format.raw/*16.3*/("""button """),format.raw/*16.10*/("""{"""),format.raw/*16.11*/(""" """),format.raw/*16.12*/("""padding: 8px 18px; border: 0; border-radius: 6px; background: #c8102e; color: #fff; cursor: pointer; """),format.raw/*16.113*/("""}"""),format.raw/*16.114*/("""
  """),format.raw/*17.3*/(""".seal-tag """),format.raw/*17.13*/("""{"""),format.raw/*17.14*/(""" """),format.raw/*17.15*/("""display: inline-block; background: #f3f3f3; border-radius: 4px; padding: 3px 10px; margin: 2px 4px 2px 0; """),format.raw/*17.121*/("""}"""),format.raw/*17.122*/("""
  """),format.raw/*18.3*/(""".muted """),format.raw/*18.10*/("""{"""),format.raw/*18.11*/(""" """),format.raw/*18.12*/("""color: #777; font-size: 13px; """),format.raw/*18.42*/("""}"""),format.raw/*18.43*/("""
"""),format.raw/*19.1*/("""</style>
</head>
<body>
<h1>印章图像鉴别服务</h1>
<p class="muted">上传合同扫描件或照片，自动定位印章区域，检测复制粘贴、局部重采样、边缘融合、光照不一致等伪造迹象，并与登记印章比对形状与文字细节。</p>

<div class="card">
  <h2>上传检测</h2>
  <form action="/analyze" method="post" enctype="multipart/form-data">
    <input type="file" name="image" accept="image/*" required>
    <button type="submit">开始分析</button>
  </form>
</div>

<div class="card">
  <h2>样例演示（程序合成）</h2>
  <a class="btn" href="/sample/genuine">真盖章样例 → 分析</a>
  <a class="btn" href="/sample/forged">伪造（抠图粘贴）样例 → 分析</a>
</div>

<div class="card">
  <h2>登记印章库（"""),_display_(/*40.14*/seals/*40.19*/.size),format.raw/*40.24*/(""" """),format.raw/*40.25*/("""枚）</h2>
  """),_display_(if(seals.isEmpty)/*41.21*/ {_display_(Seq[Any](format.raw/*41.23*/(""" """),format.raw/*41.24*/("""<p class="muted">尚未登记任何印章。</p> """)))} else {null} ),format.raw/*41.56*/("""
  """),_display_(/*42.4*/for(s <- seals) yield /*42.19*/ {_display_(Seq[Any](format.raw/*42.21*/(""" """),format.raw/*42.22*/("""<span class="seal-tag">"""),_display_(/*42.46*/s),format.raw/*42.47*/("""</span> """)))}),format.raw/*42.56*/("""
  """),format.raw/*43.3*/("""<form action="/register" method="post" enctype="multipart/form-data" style="margin-top:12px">
    <input type="text" name="name" placeholder="印章名称，如：云启科技合同章" required>
    <input type="file" name="image" accept="image/*" required>
    <button type="submit">登记</button>
  </form>
  <form action="/register-sample" method="post" style="margin-top:8px">
    <button type="submit" class="btn gray" style="border:0">一键登记样例真章</button>
  </form>
</div>
</body>
</html>
"""))
      }
    }
  }

  def render(seals:List[String]): play.twirl.api.HtmlFormat.Appendable = apply(seals)

  def f:((List[String]) => play.twirl.api.HtmlFormat.Appendable) = (seals) => apply(seals)

  def ref: this.type = this

}


              /*
                  -- GENERATED --
                  SOURCE: app/views/index.scala.html
                  HASH: c153f24596f472962ae7cd912ab7411f6b873f88
                  MATRIX: 735->1|850->23|1050->197|1077->198|1105->199|1283->349|1312->350|1342->353|1372->356|1400->357|1428->358|1473->375|1502->376|1531->377|1562->380|1591->381|1620->382|1680->414|1709->415|1739->418|1772->424|1801->425|1830->426|1939->507|1968->508|1998->511|2030->516|2058->517|2087->518|2266->669|2295->670|2325->673|2363->683|2392->684|2421->685|2467->703|2496->704|2526->707|2561->714|2590->715|2619->716|2749->817|2779->818|2809->821|2847->831|2876->832|2905->833|3040->939|3070->940|3100->943|3135->950|3164->951|3193->952|3251->982|3280->983|3308->984|3887->1536|3901->1541|3927->1546|3956->1547|4011->1575|4051->1577|4080->1578|4156->1610|4186->1614|4217->1629|4257->1631|4286->1632|4337->1656|4359->1657|4399->1666|4429->1669
                  LINES: 21->1|26->2|33->9|33->9|33->9|34->10|34->10|35->11|35->11|35->11|35->11|35->11|35->11|35->11|35->11|35->11|35->11|35->11|35->11|36->12|36->12|36->12|36->12|36->12|36->12|37->13|37->13|37->13|37->13|38->14|38->14|39->15|39->15|39->15|39->15|39->15|39->15|40->16|40->16|40->16|40->16|40->16|40->16|41->17|41->17|41->17|41->17|41->17|41->17|42->18|42->18|42->18|42->18|42->18|42->18|43->19|64->40|64->40|64->40|64->40|65->41|65->41|65->41|65->41|66->42|66->42|66->42|66->42|66->42|66->42|66->42|67->43
                  -- GENERATED --
              */
          