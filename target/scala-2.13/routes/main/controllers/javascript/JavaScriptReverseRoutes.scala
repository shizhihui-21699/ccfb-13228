// @GENERATOR:play-routes-compiler
// @SOURCE:conf/routes

import play.api.routing.JavaScriptReverseRoute


import _root_.controllers.Assets.Asset

// @LINE:1
package controllers.javascript {

  // @LINE:1
  class ReverseSealController(_prefix: => String) {

    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:7
    def registerSample: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.SealController.registerSample",
      """
        function() {
          return _wA({method:"POST", url:"""" + _prefix + { _defaultPrefix } + """" + "register-sample"})
        }
      """
    )
  
    // @LINE:5
    def file: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.SealController.file",
      """
        function(id0,name1) {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "file/" + encodeURIComponent((""" + implicitly[play.api.mvc.PathBindable[String]].javascriptUnbind + """)("id", id0)) + "/" + encodeURIComponent((""" + implicitly[play.api.mvc.PathBindable[String]].javascriptUnbind + """)("name", name1))})
        }
      """
    )
  
    // @LINE:4
    def reportJson: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.SealController.reportJson",
      """
        function(id0) {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "api/report/" + encodeURIComponent((""" + implicitly[play.api.mvc.PathBindable[String]].javascriptUnbind + """)("id", id0))})
        }
      """
    )
  
    // @LINE:6
    def register: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.SealController.register",
      """
        function() {
          return _wA({method:"POST", url:"""" + _prefix + { _defaultPrefix } + """" + "register"})
        }
      """
    )
  
    // @LINE:2
    def analyze: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.SealController.analyze",
      """
        function() {
          return _wA({method:"POST", url:"""" + _prefix + { _defaultPrefix } + """" + "analyze"})
        }
      """
    )
  
    // @LINE:8
    def sample: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.SealController.sample",
      """
        function(kind0) {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "sample/" + encodeURIComponent((""" + implicitly[play.api.mvc.PathBindable[String]].javascriptUnbind + """)("kind", kind0))})
        }
      """
    )
  
    // @LINE:1
    def index: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.SealController.index",
      """
        function() {
          return _wA({method:"GET", url:"""" + _prefix + """"})
        }
      """
    )
  
    // @LINE:3
    def report: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.SealController.report",
      """
        function(id0) {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "report/" + encodeURIComponent((""" + implicitly[play.api.mvc.PathBindable[String]].javascriptUnbind + """)("id", id0))})
        }
      """
    )
  
  }


}
