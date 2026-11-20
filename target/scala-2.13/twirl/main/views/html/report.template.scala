
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

object report extends _root_.play.twirl.api.BaseScalaTemplate[play.twirl.api.HtmlFormat.Appendable,_root_.play.twirl.api.Format[play.twirl.api.HtmlFormat.Appendable]](play.twirl.api.HtmlFormat) with _root_.play.twirl.api.Template1[models.Report,play.twirl.api.HtmlFormat.Appendable] {

  /**/
  def apply/*1.2*/(r: models.Report):play.twirl.api.HtmlFormat.Appendable = {
    _display_ {
      {


Seq[Any](format.raw/*2.1*/("""<!DOCTYPE html>
<html lang="zh-CN">
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>分析报告 #"""),_display_(/*7.15*/r/*7.16*/.id),format.raw/*7.19*/("""</title>
<style>
  body """),format.raw/*9.8*/("""{"""),format.raw/*9.9*/(""" """),format.raw/*9.10*/("""font-family: -apple-system, "PingFang SC", "Microsoft YaHei", sans-serif;
         max-width: 960px; margin: 24px auto; padding: 0 16px; color: #222; """),format.raw/*10.77*/("""}"""),format.raw/*10.78*/("""
  """),format.raw/*11.3*/(""".banner """),format.raw/*11.11*/("""{"""),format.raw/*11.12*/(""" """),format.raw/*11.13*/("""border-radius: 10px; padding: 14px 18px; color: #fff; margin: 14px 0; """),format.raw/*11.83*/("""}"""),format.raw/*11.84*/("""
  """),format.raw/*12.3*/(""".high """),format.raw/*12.9*/("""{"""),format.raw/*12.10*/(""" """),format.raw/*12.11*/("""background: #c8102e; """),format.raw/*12.32*/("""}"""),format.raw/*12.33*/(""" """),format.raw/*12.34*/(""".mid """),format.raw/*12.39*/("""{"""),format.raw/*12.40*/(""" """),format.raw/*12.41*/("""background: #d98c00; """),format.raw/*12.62*/("""}"""),format.raw/*12.63*/(""" """),format.raw/*12.64*/(""".low """),format.raw/*12.69*/("""{"""),format.raw/*12.70*/(""" """),format.raw/*12.71*/("""background: #2e8b57; """),format.raw/*12.92*/("""}"""),format.raw/*12.93*/("""
  """),format.raw/*13.3*/(""".card """),format.raw/*13.9*/("""{"""),format.raw/*13.10*/(""" """),format.raw/*13.11*/("""border: 1px solid #ddd; border-radius: 10px; padding: 16px 20px; margin: 16px 0; """),format.raw/*13.92*/("""}"""),format.raw/*13.93*/("""
  """),format.raw/*14.3*/("""img.main """),format.raw/*14.12*/("""{"""),format.raw/*14.13*/(""" """),format.raw/*14.14*/("""max-width: 100%; border: 1px solid #ccc; border-radius: 6px; """),format.raw/*14.75*/("""}"""),format.raw/*14.76*/("""
  """),format.raw/*15.3*/("""img.diff """),format.raw/*15.12*/("""{"""),format.raw/*15.13*/(""" """),format.raw/*15.14*/("""width: 220px; border: 1px solid #ccc; border-radius: 6px; margin: 6px 12px 6px 0; """),format.raw/*15.96*/("""}"""),format.raw/*15.97*/("""
  """),format.raw/*16.3*/(""".bar-wrap """),format.raw/*16.13*/("""{"""),format.raw/*16.14*/(""" """),format.raw/*16.15*/("""background: #eee; border-radius: 4px; height: 14px; flex: 1; margin: 0 10px; """),format.raw/*16.92*/("""}"""),format.raw/*16.93*/("""
  """),format.raw/*17.3*/(""".bar """),format.raw/*17.8*/("""{"""),format.raw/*17.9*/(""" """),format.raw/*17.10*/("""height: 14px; border-radius: 4px; background: #c8102e; """),format.raw/*17.65*/("""}"""),format.raw/*17.66*/("""
  """),format.raw/*18.3*/(""".ind-row """),format.raw/*18.12*/("""{"""),format.raw/*18.13*/(""" """),format.raw/*18.14*/("""display: flex; align-items: center; margin-top: 10px; """),format.raw/*18.68*/("""}"""),format.raw/*18.69*/("""
  """),format.raw/*19.3*/(""".ind-name """),format.raw/*19.13*/("""{"""),format.raw/*19.14*/(""" """),format.raw/*19.15*/("""width: 130px; font-weight: 600; """),format.raw/*19.47*/("""}"""),format.raw/*19.48*/("""
  """),format.raw/*20.3*/(""".ind-score """),format.raw/*20.14*/("""{"""),format.raw/*20.15*/(""" """),format.raw/*20.16*/("""width: 56px; text-align: right; font-weight: 700; """),format.raw/*20.66*/("""}"""),format.raw/*20.67*/("""
  """),format.raw/*21.3*/(""".detail """),format.raw/*21.11*/("""{"""),format.raw/*21.12*/(""" """),format.raw/*21.13*/("""color: #666; font-size: 13px; margin: 4px 0 0; """),format.raw/*21.60*/("""}"""),format.raw/*21.61*/("""
  """),format.raw/*22.3*/("""table """),format.raw/*22.9*/("""{"""),format.raw/*22.10*/(""" """),format.raw/*22.11*/("""border-collapse: collapse; """),format.raw/*22.38*/("""}"""),format.raw/*22.39*/(""" """),format.raw/*22.40*/("""td, th """),format.raw/*22.47*/("""{"""),format.raw/*22.48*/(""" """),format.raw/*22.49*/("""padding: 4px 12px 4px 0; text-align: left; """),format.raw/*22.92*/("""}"""),format.raw/*22.93*/("""
  """),format.raw/*23.3*/("""a """),format.raw/*23.5*/("""{"""),format.raw/*23.6*/(""" """),format.raw/*23.7*/("""color: #c8102e; """),format.raw/*23.23*/("""}"""),format.raw/*23.24*/("""
"""),format.raw/*24.1*/("""</style>
</head>
<body>
<h1>分析报告 <small style="color:#999">#"""),_display_(/*27.38*/r/*27.39*/.id),format.raw/*27.42*/("""</small></h1>
<div class="banner """),_display_(/*28.21*/(if(r.overall >= 65) "high" else if(r.overall >= 35) "mid" else "low")),format.raw/*28.91*/("""">
  综合风险："""),_display_(/*29.9*/("%.0f".format(r.overall))),format.raw/*29.35*/("""% — """),_display_(/*29.40*/r/*29.41*/.summary),format.raw/*29.49*/("""
"""),format.raw/*30.1*/("""</div>

"""),_display_(if(r.annotatedFile.nonEmpty)/*32.30*/ {_display_(Seq[Any](format.raw/*32.32*/("""
"""),format.raw/*33.1*/("""<div class="card">
  <h3>疑点区域标注</h3>
  <img class="main" src="/file/"""),_display_(/*35.33*/r/*35.34*/.id),format.raw/*35.37*/("""/"""),_display_(/*35.39*/r/*35.40*/.annotatedFile),format.raw/*35.54*/("""" alt="标注图">
</div>
""")))} else {null} ),format.raw/*37.2*/("""

"""),_display_(/*39.2*/for(s <- r.seals) yield /*39.19*/ {_display_(Seq[Any](format.raw/*39.21*/("""
"""),format.raw/*40.1*/("""<div class="card">
  <h2>印章 #"""),_display_(/*41.12*/{s.index + 1}),format.raw/*41.25*/(""" """),format.raw/*41.26*/("""— 风险 """),_display_(/*41.32*/("%.0f".format(s.suspicion))),format.raw/*41.60*/("""%（"""),_display_(/*41.63*/s/*41.64*/.level),format.raw/*41.70*/("""）</h2>
  <p class="detail">位置：("""),_display_(/*42.26*/s/*42.27*/.x),format.raw/*42.29*/(""", """),_display_(/*42.32*/s/*42.33*/.y),format.raw/*42.35*/(""")，尺寸："""),_display_(/*42.41*/s/*42.42*/.w),format.raw/*42.44*/(""" """),format.raw/*42.45*/("""× """),_display_(/*42.48*/s/*42.49*/.h),format.raw/*42.51*/(""" """),format.raw/*42.52*/("""px</p>
  """),_display_(/*43.4*/for(ind <- s.indicators) yield /*43.28*/ {_display_(Seq[Any](format.raw/*43.30*/("""
    """),format.raw/*44.5*/("""<div class="ind-row">
      <span class="ind-name">"""),_display_(/*45.31*/ind/*45.34*/.label),format.raw/*45.40*/("""</span>
      <div class="bar-wrap"><div class="bar" style="width:"""),_display_(/*46.60*/ind/*46.63*/.score),format.raw/*46.69*/("""%"></div></div>
      <span class="ind-score">"""),_display_(/*47.32*/("%.0f".format(ind.score))),format.raw/*47.58*/("""</span>
    </div>
    <p class="detail">"""),_display_(/*49.24*/ind/*49.27*/.detail),format.raw/*49.34*/("""</p>
  """)))}),format.raw/*50.4*/("""
  """),_display_(if(s.matches.nonEmpty)/*51.26*/ {_display_(Seq[Any](format.raw/*51.28*/("""
    """),format.raw/*52.5*/("""<h3>与登记印章比对</h3>
    """),_display_(/*53.6*/for(m <- s.matches) yield /*53.25*/ {_display_(Seq[Any](format.raw/*53.27*/("""
      """),format.raw/*54.7*/("""<table>
        <tr><th>登记章</th><td>"""),_display_(/*55.30*/m/*55.31*/.name),format.raw/*55.36*/("""</td></tr>
        <tr><th>墨迹重合度 IoU</th><td>"""),_display_(/*56.36*/m/*56.37*/.iou),format.raw/*56.41*/("""（对齐旋转 """),_display_(/*56.48*/m/*56.49*/.angle),format.raw/*56.55*/("""°）</td></tr>
        <tr><th>形状距离（Hu 矩）</th><td>"""),_display_(/*57.37*/m/*57.38*/.shapeDist),format.raw/*57.48*/("""</td></tr>
        <tr><th>结论</th><td>"""),_display_(/*58.29*/m/*58.30*/.verdict),format.raw/*58.38*/("""</td></tr>
      </table>
      <img class="diff" src="/file/"""),_display_(/*60.37*/r/*60.38*/.id),format.raw/*60.41*/("""/"""),_display_(/*60.43*/m/*60.44*/.diffImage),format.raw/*60.54*/("""" alt="差异图">
      <p class="detail">差异图：灰=一致，红=登记章有而检材缺，蓝=检材多出</p>
    """)))}),format.raw/*62.6*/("""
  """)))} else {null} ),format.raw/*63.4*/("""
"""),format.raw/*64.1*/("""</div>
""")))}),format.raw/*65.2*/("""

"""),format.raw/*67.1*/("""<p><a href="/">← 返回首页</a> ｜ <a href="/api/report/"""),_display_(/*67.51*/r/*67.52*/.id),format.raw/*67.55*/("""">JSON 格式报告</a></p>
</body>
</html>
"""))
      }
    }
  }

  def render(r:models.Report): play.twirl.api.HtmlFormat.Appendable = apply(r)

  def f:((models.Report) => play.twirl.api.HtmlFormat.Appendable) = (r) => apply(r)

  def ref: this.type = this

}


              /*
                  -- GENERATED --
                  SOURCE: app/views/report.scala.html
                  HASH: ca27949574dbb5e59102d5f458b6267f75c2ed98
                  MATRIX: 737->1|849->20|1024->169|1033->170|1056->173|1106->197|1133->198|1161->199|1339->349|1368->350|1398->353|1434->361|1463->362|1492->363|1590->433|1619->434|1649->437|1682->443|1711->444|1740->445|1789->466|1818->467|1847->468|1880->473|1909->474|1938->475|1987->496|2016->497|2045->498|2078->503|2107->504|2136->505|2185->526|2214->527|2244->530|2277->536|2306->537|2335->538|2444->619|2473->620|2503->623|2540->632|2569->633|2598->634|2687->695|2716->696|2746->699|2783->708|2812->709|2841->710|2951->792|2980->793|3010->796|3048->806|3077->807|3106->808|3211->885|3240->886|3270->889|3302->894|3330->895|3359->896|3442->951|3471->952|3501->955|3538->964|3567->965|3596->966|3678->1020|3707->1021|3737->1024|3775->1034|3804->1035|3833->1036|3893->1068|3922->1069|3952->1072|3991->1083|4020->1084|4049->1085|4127->1135|4156->1136|4186->1139|4222->1147|4251->1148|4280->1149|4355->1196|4384->1197|4414->1200|4447->1206|4476->1207|4505->1208|4560->1235|4589->1236|4618->1237|4653->1244|4682->1245|4711->1246|4782->1289|4811->1290|4841->1293|4870->1295|4898->1296|4926->1297|4970->1313|4999->1314|5027->1315|5115->1376|5125->1377|5149->1380|5210->1414|5301->1484|5338->1495|5385->1521|5417->1526|5427->1527|5456->1535|5484->1536|5548->1573|5588->1575|5616->1576|5712->1645|5722->1646|5746->1649|5775->1651|5785->1652|5820->1666|5884->1687|5913->1690|5946->1707|5986->1709|6014->1710|6071->1740|6105->1753|6134->1754|6167->1760|6216->1788|6246->1791|6256->1792|6283->1798|6342->1830|6352->1831|6375->1833|6405->1836|6415->1837|6438->1839|6471->1845|6481->1846|6504->1848|6533->1849|6563->1852|6573->1853|6596->1855|6625->1856|6661->1866|6701->1890|6741->1892|6773->1897|6852->1949|6864->1952|6891->1958|6985->2025|6997->2028|7024->2034|7098->2081|7145->2107|7214->2149|7226->2152|7254->2159|7292->2167|7345->2193|7385->2195|7417->2200|7465->2222|7500->2241|7540->2243|7574->2250|7638->2287|7648->2288|7674->2293|7747->2339|7757->2340|7782->2344|7816->2351|7826->2352|7853->2358|7929->2407|7939->2408|7970->2418|8036->2457|8046->2458|8075->2466|8164->2528|8174->2529|8198->2532|8227->2534|8237->2535|8268->2545|8371->2618|8418->2622|8446->2623|8484->2631|8513->2633|8590->2683|8600->2684|8624->2687
                  LINES: 21->1|26->2|31->7|31->7|31->7|33->9|33->9|33->9|34->10|34->10|35->11|35->11|35->11|35->11|35->11|35->11|36->12|36->12|36->12|36->12|36->12|36->12|36->12|36->12|36->12|36->12|36->12|36->12|36->12|36->12|36->12|36->12|36->12|36->12|37->13|37->13|37->13|37->13|37->13|37->13|38->14|38->14|38->14|38->14|38->14|38->14|39->15|39->15|39->15|39->15|39->15|39->15|40->16|40->16|40->16|40->16|40->16|40->16|41->17|41->17|41->17|41->17|41->17|41->17|42->18|42->18|42->18|42->18|42->18|42->18|43->19|43->19|43->19|43->19|43->19|43->19|44->20|44->20|44->20|44->20|44->20|44->20|45->21|45->21|45->21|45->21|45->21|45->21|46->22|46->22|46->22|46->22|46->22|46->22|46->22|46->22|46->22|46->22|46->22|46->22|47->23|47->23|47->23|47->23|47->23|47->23|48->24|51->27|51->27|51->27|52->28|52->28|53->29|53->29|53->29|53->29|53->29|54->30|56->32|56->32|57->33|59->35|59->35|59->35|59->35|59->35|59->35|61->37|63->39|63->39|63->39|64->40|65->41|65->41|65->41|65->41|65->41|65->41|65->41|65->41|66->42|66->42|66->42|66->42|66->42|66->42|66->42|66->42|66->42|66->42|66->42|66->42|66->42|66->42|67->43|67->43|67->43|68->44|69->45|69->45|69->45|70->46|70->46|70->46|71->47|71->47|73->49|73->49|73->49|74->50|75->51|75->51|76->52|77->53|77->53|77->53|78->54|79->55|79->55|79->55|80->56|80->56|80->56|80->56|80->56|80->56|81->57|81->57|81->57|82->58|82->58|82->58|84->60|84->60|84->60|84->60|84->60|84->60|86->62|87->63|88->64|89->65|91->67|91->67|91->67|91->67
                  -- GENERATED --
              */
          