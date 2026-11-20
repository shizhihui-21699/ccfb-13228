package controllers

import models._
import play.api.libs.json._
import play.api.mvc._
import services._
import _root_.util.Imaging
import java.io.File
import java.nio.file.{Files, StandardCopyOption}
import java.util.UUID
import javax.inject._
import scala.collection.concurrent.TrieMap

object ReportStore {
  private val store = TrieMap.empty[String, Report]
  def put(r: Report): Unit = store.put(r.id, r)
  def get(id: String): Option[Report] = store.get(id)
}

@Singleton
class SealController @Inject()(cc: ControllerComponents)(implicit ec: scala.concurrent.ExecutionContext) extends AbstractController(cc) {
  Imaging.ensureCv()

  private implicit val indW: OWrites[IndicatorResult] = Json.writes[IndicatorResult]
  private implicit val matchW: OWrites[MatchResult] = Json.writes[MatchResult]
  private implicit val sealW: OWrites[SealResult] = Json.writes[SealResult]
  private implicit val repW: OWrites[Report] = Json.writes[Report]

  private def newId(): String = UUID.randomUUID().toString.take(8)

  def index: Action[AnyContent] = Action {
    Ok(views.html.index(SealRegistry.list()))
  }

  def analyze: Action[MultipartFormData[play.api.libs.Files.TemporaryFile]] =
    Action(parse.multipartFormData) { req =>
      req.body.file("image") match {
        case Some(f) =>
          val id = newId()
          val dir = new File(s"data/results/$id"); dir.mkdirs()
          val rawName = Option(f.filename).getOrElse("upload.png")
          val ext = rawName.split("\\.").lastOption.map(_.toLowerCase).getOrElse("png")
          val dest = new File(dir, s"upload.${if (ext.matches("[a-z0-9]{1,5}")) ext else "png"}")
          Files.copy(f.ref.path, dest.toPath, StandardCopyOption.REPLACE_EXISTING)
          val report = Pipeline.analyze(dest, id)
          ReportStore.put(report)
          Redirect(routes.SealController.report(id))
        case None => BadRequest("未收到上传文件（字段名 image）")
      }
    }

  def report(id: String): Action[AnyContent] = Action {
    ReportStore.get(id).map(r => Ok(views.html.report(r)))
      .getOrElse(NotFound("报告不存在或服务已重启（报告保存在内存中）"))
  }

  def reportJson(id: String): Action[AnyContent] = Action {
    ReportStore.get(id).map(r => Ok(Json.toJson(r)))
      .getOrElse(NotFound(Json.obj("error" -> "not found")))
  }

  def file(id: String, name: String): Action[AnyContent] = Action {
    if (id.exists(c => !c.isLetterOrDigit) || name.contains("..") || name.contains("/")) BadRequest("非法路径")
    else {
      val f = new File(s"data/results/$id/$name")
      if (f.exists()) Ok.sendFile(f) else NotFound("文件不存在")
    }
  }

  def register: Action[MultipartFormData[play.api.libs.Files.TemporaryFile]] =
    Action(parse.multipartFormData) { req =>
      val name = req.body.dataParts.get("name").flatMap(_.headOption).getOrElse("").trim
      (req.body.file("image"), name.nonEmpty) match {
        case (Some(f), true) =>
          val tmp = File.createTempFile("seal-reg", ".png")
          Files.copy(f.ref.path, tmp.toPath, StandardCopyOption.REPLACE_EXISTING)
          SealRegistry.register(name, tmp) match {
            case Right(_) => Redirect(routes.SealController.index)
            case Left(err) => BadRequest(s"登记失败：$err")
          }
        case _ => BadRequest("需要同时提供印章名称（name）和图像文件（image）")
      }
    }

  /** 一键登记：用合成的真章样例作为登记章 */
  def registerSample: Action[AnyContent] = Action {
    val f = SampleGenerator.genuine(new File("data/samples/genuine-for-register.jpg"))
    SealRegistry.register("样例真章-云启科技", f) match {
      case Right(_) => Redirect(routes.SealController.index)
      case Left(err) => BadRequest(s"登记失败：$err")
    }
  }

  /** 生成样例并立即分析 */
  def sample(kind: String): Action[AnyContent] = Action {
    val f = kind match {
      case "genuine" => SampleGenerator.genuine(new File("data/samples/genuine.jpg"))
      case "forged" => SampleGenerator.forged(new File("data/samples/forged.png"))
      case _ => null
    }
    if (f == null) NotFound("未知样例类型")
    else {
      val id = newId()
      val report = Pipeline.analyze(f, id)
      ReportStore.put(report)
      Redirect(routes.SealController.report(id))
    }
  }
}
