// @GENERATOR:play-routes-compiler
// @SOURCE:conf/routes

package controllers;

import router.RoutesPrefix;

public class routes {
  
  public static final controllers.ReverseSealController SealController = new controllers.ReverseSealController(RoutesPrefix.byNamePrefix());

  public static class javascript {
    
    public static final controllers.javascript.ReverseSealController SealController = new controllers.javascript.ReverseSealController(RoutesPrefix.byNamePrefix());
  }

}
