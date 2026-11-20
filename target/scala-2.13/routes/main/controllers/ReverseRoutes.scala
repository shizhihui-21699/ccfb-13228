// @GENERATOR:play-routes-compiler
// @SOURCE:conf/routes

import play.api.mvc.Call


import _root_.controllers.Assets.Asset

// @LINE:1
package controllers {

  // @LINE:1
  class ReverseSealController(_prefix: => String) {
    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:7
    def registerSample: Call = {
      
      Call("POST", _prefix + { _defaultPrefix } + "register-sample")
    }
  
    // @LINE:5
    def file(id:String, name:String): Call = {
      
      Call("GET", _prefix + { _defaultPrefix } + "file/" + play.core.routing.dynamicString(implicitly[play.api.mvc.PathBindable[String]].unbind("id", id)) + "/" + play.core.routing.dynamicString(implicitly[play.api.mvc.PathBindable[String]].unbind("name", name)))
    }
  
    // @LINE:4
    def reportJson(id:String): Call = {
      
      Call("GET", _prefix + { _defaultPrefix } + "api/report/" + play.core.routing.dynamicString(implicitly[play.api.mvc.PathBindable[String]].unbind("id", id)))
    }
  
    // @LINE:6
    def register: Call = {
      
      Call("POST", _prefix + { _defaultPrefix } + "register")
    }
  
    // @LINE:2
    def analyze: Call = {
      
      Call("POST", _prefix + { _defaultPrefix } + "analyze")
    }
  
    // @LINE:8
    def sample(kind:String): Call = {
      
      Call("GET", _prefix + { _defaultPrefix } + "sample/" + play.core.routing.dynamicString(implicitly[play.api.mvc.PathBindable[String]].unbind("kind", kind)))
    }
  
    // @LINE:1
    def index: Call = {
      
      Call("GET", _prefix)
    }
  
    // @LINE:3
    def report(id:String): Call = {
      
      Call("GET", _prefix + { _defaultPrefix } + "report/" + play.core.routing.dynamicString(implicitly[play.api.mvc.PathBindable[String]].unbind("id", id)))
    }
  
  }


}
