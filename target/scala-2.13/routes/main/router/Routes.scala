// @GENERATOR:play-routes-compiler
// @SOURCE:conf/routes

package router

import play.core.routing._
import play.core.routing.HandlerInvokerFactory._

import play.api.mvc._

import _root_.controllers.Assets.Asset

class Routes(
  override val errorHandler: play.api.http.HttpErrorHandler, 
  // @LINE:1
  SealController_0: controllers.SealController,
  val prefix: String
) extends GeneratedRouter {

  @javax.inject.Inject()
  def this(errorHandler: play.api.http.HttpErrorHandler,
    // @LINE:1
    SealController_0: controllers.SealController
  ) = this(errorHandler, SealController_0, "/")

  def withPrefix(addPrefix: String): Routes = {
    val prefix = play.api.routing.Router.concatPrefix(addPrefix, this.prefix)
    router.RoutesPrefix.setPrefix(prefix)
    new Routes(errorHandler, SealController_0, prefix)
  }

  private val defaultPrefix: String = {
    if (this.prefix.endsWith("/")) "" else "/"
  }

  def documentation = List(
    ("""GET""", this.prefix, """controllers.SealController.index"""),
    ("""POST""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """analyze""", """controllers.SealController.analyze"""),
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """report/""" + "$" + """id<[^/]+>""", """controllers.SealController.report(id:String)"""),
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """api/report/""" + "$" + """id<[^/]+>""", """controllers.SealController.reportJson(id:String)"""),
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """file/""" + "$" + """id<[^/]+>/""" + "$" + """name<[^/]+>""", """controllers.SealController.file(id:String, name:String)"""),
    ("""POST""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """register""", """controllers.SealController.register"""),
    ("""POST""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """register-sample""", """controllers.SealController.registerSample"""),
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """sample/""" + "$" + """kind<[^/]+>""", """controllers.SealController.sample(kind:String)"""),
    Nil
  ).foldLeft(Seq.empty[(String, String, String)]) { (s,e) => e.asInstanceOf[Any] match {
    case r @ (_,_,_) => s :+ r.asInstanceOf[(String, String, String)]
    case l => s ++ l.asInstanceOf[List[(String, String, String)]]
  }}


  // @LINE:1
  private lazy val controllers_SealController_index0_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix)))
  )
  private lazy val controllers_SealController_index0_invoker = createInvoker(
    SealController_0.index,
    play.api.routing.HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.SealController",
      "index",
      Nil,
      "GET",
      this.prefix + """""",
      """""",
      Seq()
    )
  )

  // @LINE:2
  private lazy val controllers_SealController_analyze1_route = Route("POST",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("analyze")))
  )
  private lazy val controllers_SealController_analyze1_invoker = createInvoker(
    SealController_0.analyze,
    play.api.routing.HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.SealController",
      "analyze",
      Nil,
      "POST",
      this.prefix + """analyze""",
      """""",
      Seq()
    )
  )

  // @LINE:3
  private lazy val controllers_SealController_report2_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("report/"), DynamicPart("id", """[^/]+""", encodeable=true)))
  )
  private lazy val controllers_SealController_report2_invoker = createInvoker(
    SealController_0.report(fakeValue[String]),
    play.api.routing.HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.SealController",
      "report",
      Seq(classOf[String]),
      "GET",
      this.prefix + """report/""" + "$" + """id<[^/]+>""",
      """""",
      Seq()
    )
  )

  // @LINE:4
  private lazy val controllers_SealController_reportJson3_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("api/report/"), DynamicPart("id", """[^/]+""", encodeable=true)))
  )
  private lazy val controllers_SealController_reportJson3_invoker = createInvoker(
    SealController_0.reportJson(fakeValue[String]),
    play.api.routing.HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.SealController",
      "reportJson",
      Seq(classOf[String]),
      "GET",
      this.prefix + """api/report/""" + "$" + """id<[^/]+>""",
      """""",
      Seq()
    )
  )

  // @LINE:5
  private lazy val controllers_SealController_file4_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("file/"), DynamicPart("id", """[^/]+""", encodeable=true), StaticPart("/"), DynamicPart("name", """[^/]+""", encodeable=true)))
  )
  private lazy val controllers_SealController_file4_invoker = createInvoker(
    SealController_0.file(fakeValue[String], fakeValue[String]),
    play.api.routing.HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.SealController",
      "file",
      Seq(classOf[String], classOf[String]),
      "GET",
      this.prefix + """file/""" + "$" + """id<[^/]+>/""" + "$" + """name<[^/]+>""",
      """""",
      Seq()
    )
  )

  // @LINE:6
  private lazy val controllers_SealController_register5_route = Route("POST",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("register")))
  )
  private lazy val controllers_SealController_register5_invoker = createInvoker(
    SealController_0.register,
    play.api.routing.HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.SealController",
      "register",
      Nil,
      "POST",
      this.prefix + """register""",
      """""",
      Seq()
    )
  )

  // @LINE:7
  private lazy val controllers_SealController_registerSample6_route = Route("POST",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("register-sample")))
  )
  private lazy val controllers_SealController_registerSample6_invoker = createInvoker(
    SealController_0.registerSample,
    play.api.routing.HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.SealController",
      "registerSample",
      Nil,
      "POST",
      this.prefix + """register-sample""",
      """""",
      Seq()
    )
  )

  // @LINE:8
  private lazy val controllers_SealController_sample7_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("sample/"), DynamicPart("kind", """[^/]+""", encodeable=true)))
  )
  private lazy val controllers_SealController_sample7_invoker = createInvoker(
    SealController_0.sample(fakeValue[String]),
    play.api.routing.HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.SealController",
      "sample",
      Seq(classOf[String]),
      "GET",
      this.prefix + """sample/""" + "$" + """kind<[^/]+>""",
      """""",
      Seq()
    )
  )


  def routes: PartialFunction[RequestHeader, Handler] = {
  
    // @LINE:1
    case controllers_SealController_index0_route(params@_) =>
      call { 
        controllers_SealController_index0_invoker.call(SealController_0.index)
      }
  
    // @LINE:2
    case controllers_SealController_analyze1_route(params@_) =>
      call { 
        controllers_SealController_analyze1_invoker.call(SealController_0.analyze)
      }
  
    // @LINE:3
    case controllers_SealController_report2_route(params@_) =>
      call(params.fromPath[String]("id", None)) { (id) =>
        controllers_SealController_report2_invoker.call(SealController_0.report(id))
      }
  
    // @LINE:4
    case controllers_SealController_reportJson3_route(params@_) =>
      call(params.fromPath[String]("id", None)) { (id) =>
        controllers_SealController_reportJson3_invoker.call(SealController_0.reportJson(id))
      }
  
    // @LINE:5
    case controllers_SealController_file4_route(params@_) =>
      call(params.fromPath[String]("id", None), params.fromPath[String]("name", None)) { (id, name) =>
        controllers_SealController_file4_invoker.call(SealController_0.file(id, name))
      }
  
    // @LINE:6
    case controllers_SealController_register5_route(params@_) =>
      call { 
        controllers_SealController_register5_invoker.call(SealController_0.register)
      }
  
    // @LINE:7
    case controllers_SealController_registerSample6_route(params@_) =>
      call { 
        controllers_SealController_registerSample6_invoker.call(SealController_0.registerSample)
      }
  
    // @LINE:8
    case controllers_SealController_sample7_route(params@_) =>
      call(params.fromPath[String]("kind", None)) { (kind) =>
        controllers_SealController_sample7_invoker.call(SealController_0.sample(kind))
      }
  }
}
