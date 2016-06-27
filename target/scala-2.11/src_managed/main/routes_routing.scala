// @SOURCE:F:/workspace_eclipse-jse/app-amazon-mws/conf/routes
// @HASH:8c7cad7ca02bcfb13dca86ec19d5dcb6ed78d9c1
// @DATE:Mon Jun 27 17:08:54 CST 2016


import play.core._
import play.core.Router._
import play.core.Router.HandlerInvokerFactory._
import play.core.j._

import play.api.mvc._
import _root_.controllers.Assets.Asset
import _root_.play.libs.F

import Router.queryString

object Routes extends Router.Routes {

import ReverseRouteContext.empty

private var _prefix = "/"

def setPrefix(prefix: String) {
  _prefix = prefix
  List[(String,Routes)]().foreach {
    case (p, router) => router.setPrefix(prefix + (if(prefix.endsWith("/")) "" else "/") + p)
  }
}

def prefix = _prefix

lazy val defaultPrefix = { if(Routes.prefix.endsWith("/")) "" else "/" }


// @LINE:6
private[this] lazy val controllers_Application_index0_route = Route("GET", PathPattern(List(StaticPart(Routes.prefix))))
private[this] lazy val controllers_Application_index0_invoker = createInvoker(
controllers.Application.index(),
HandlerDef(this.getClass.getClassLoader, "", "controllers.Application", "index", Nil,"GET", """ Home page""", Routes.prefix + """"""))
        

// @LINE:7
private[this] lazy val controllers_Application_test1_route = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("test/a"))))
private[this] lazy val controllers_Application_test1_invoker = createInvoker(
controllers.Application.test(),
HandlerDef(this.getClass.getClassLoader, "", "controllers.Application", "test", Nil,"GET", """""", Routes.prefix + """test/a"""))
        

// @LINE:11
private[this] lazy val controllers_APIToERPController_getCronOrderList2_route = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("ap/getCronOrderList"))))
private[this] lazy val controllers_APIToERPController_getCronOrderList2_invoker = createInvoker(
controllers.APIToERPController.getCronOrderList(),
HandlerDef(this.getClass.getClassLoader, "", "controllers.APIToERPController", "getCronOrderList", Nil,"GET", """#ERP_TO_AP API""", Routes.prefix + """ap/getCronOrderList"""))
        

// @LINE:12
private[this] lazy val controllers_APIToERPController_getCronOrderByAccount3_route = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("ap/getCronOrderByAccount"))))
private[this] lazy val controllers_APIToERPController_getCronOrderByAccount3_invoker = createInvoker(
controllers.APIToERPController.getCronOrderByAccount(),
HandlerDef(this.getClass.getClassLoader, "", "controllers.APIToERPController", "getCronOrderByAccount", Nil,"GET", """""", Routes.prefix + """ap/getCronOrderByAccount"""))
        

// @LINE:13
private[this] lazy val controllers_APIToERPController_getS3FileIdList4_route = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("ap/getS3FileIdList"))))
private[this] lazy val controllers_APIToERPController_getS3FileIdList4_invoker = createInvoker(
controllers.APIToERPController.getS3FileIdList(),
HandlerDef(this.getClass.getClassLoader, "", "controllers.APIToERPController", "getS3FileIdList", Nil,"GET", """""", Routes.prefix + """ap/getS3FileIdList"""))
        

// @LINE:14
private[this] lazy val controllers_APIToERPController_postCronOrder5_route = Route("POST", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("ap/postCronOrder"))))
private[this] lazy val controllers_APIToERPController_postCronOrder5_invoker = createInvoker(
controllers.APIToERPController.postCronOrder(),
HandlerDef(this.getClass.getClassLoader, "", "controllers.APIToERPController", "postCronOrder", Nil,"POST", """""", Routes.prefix + """ap/postCronOrder"""))
        

// @LINE:15
private[this] lazy val controllers_APIToERPController_updateCronOrder6_route = Route("POST", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("ap/updateCronOrder"))))
private[this] lazy val controllers_APIToERPController_updateCronOrder6_invoker = createInvoker(
controllers.APIToERPController.updateCronOrder(),
HandlerDef(this.getClass.getClassLoader, "", "controllers.APIToERPController", "updateCronOrder", Nil,"POST", """""", Routes.prefix + """ap/updateCronOrder"""))
        

// @LINE:16
private[this] lazy val controllers_APIToERPController_putTracking7_route = Route("POST", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("ap/putTracking"))))
private[this] lazy val controllers_APIToERPController_putTracking7_invoker = createInvoker(
controllers.APIToERPController.putTracking(),
HandlerDef(this.getClass.getClassLoader, "", "controllers.APIToERPController", "putTracking", Nil,"POST", """""", Routes.prefix + """ap/putTracking"""))
        

// @LINE:17
private[this] lazy val controllers_APIToERPController_download8_route = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("ap/download"))))
private[this] lazy val controllers_APIToERPController_download8_invoker = createInvoker(
controllers.APIToERPController.download(),
HandlerDef(this.getClass.getClassLoader, "", "controllers.APIToERPController", "download", Nil,"GET", """""", Routes.prefix + """ap/download"""))
        

// @LINE:22
private[this] lazy val controllers_Assets_versioned9_route = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("assets/"),DynamicPart("file", """.+""",false))))
private[this] lazy val controllers_Assets_versioned9_invoker = createInvoker(
controllers.Assets.versioned(fakeValue[String], fakeValue[Asset]),
HandlerDef(this.getClass.getClassLoader, "", "controllers.Assets", "versioned", Seq(classOf[String], classOf[Asset]),"GET", """ Map static resources from the /public folder to the /assets URL path""", Routes.prefix + """assets/$file<.+>"""))
        
def documentation = List(("""GET""", prefix,"""controllers.Application.index()"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """test/a""","""controllers.Application.test()"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """ap/getCronOrderList""","""controllers.APIToERPController.getCronOrderList()"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """ap/getCronOrderByAccount""","""controllers.APIToERPController.getCronOrderByAccount()"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """ap/getS3FileIdList""","""controllers.APIToERPController.getS3FileIdList()"""),("""POST""", prefix + (if(prefix.endsWith("/")) "" else "/") + """ap/postCronOrder""","""controllers.APIToERPController.postCronOrder()"""),("""POST""", prefix + (if(prefix.endsWith("/")) "" else "/") + """ap/updateCronOrder""","""controllers.APIToERPController.updateCronOrder()"""),("""POST""", prefix + (if(prefix.endsWith("/")) "" else "/") + """ap/putTracking""","""controllers.APIToERPController.putTracking()"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """ap/download""","""controllers.APIToERPController.download()"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """assets/$file<.+>""","""controllers.Assets.versioned(path:String = "/public", file:Asset)""")).foldLeft(List.empty[(String,String,String)]) { (s,e) => e.asInstanceOf[Any] match {
  case r @ (_,_,_) => s :+ r.asInstanceOf[(String,String,String)]
  case l => s ++ l.asInstanceOf[List[(String,String,String)]]
}}
      

def routes:PartialFunction[RequestHeader,Handler] = {

// @LINE:6
case controllers_Application_index0_route(params) => {
   call { 
        controllers_Application_index0_invoker.call(controllers.Application.index())
   }
}
        

// @LINE:7
case controllers_Application_test1_route(params) => {
   call { 
        controllers_Application_test1_invoker.call(controllers.Application.test())
   }
}
        

// @LINE:11
case controllers_APIToERPController_getCronOrderList2_route(params) => {
   call { 
        controllers_APIToERPController_getCronOrderList2_invoker.call(controllers.APIToERPController.getCronOrderList())
   }
}
        

// @LINE:12
case controllers_APIToERPController_getCronOrderByAccount3_route(params) => {
   call { 
        controllers_APIToERPController_getCronOrderByAccount3_invoker.call(controllers.APIToERPController.getCronOrderByAccount())
   }
}
        

// @LINE:13
case controllers_APIToERPController_getS3FileIdList4_route(params) => {
   call { 
        controllers_APIToERPController_getS3FileIdList4_invoker.call(controllers.APIToERPController.getS3FileIdList())
   }
}
        

// @LINE:14
case controllers_APIToERPController_postCronOrder5_route(params) => {
   call { 
        controllers_APIToERPController_postCronOrder5_invoker.call(controllers.APIToERPController.postCronOrder())
   }
}
        

// @LINE:15
case controllers_APIToERPController_updateCronOrder6_route(params) => {
   call { 
        controllers_APIToERPController_updateCronOrder6_invoker.call(controllers.APIToERPController.updateCronOrder())
   }
}
        

// @LINE:16
case controllers_APIToERPController_putTracking7_route(params) => {
   call { 
        controllers_APIToERPController_putTracking7_invoker.call(controllers.APIToERPController.putTracking())
   }
}
        

// @LINE:17
case controllers_APIToERPController_download8_route(params) => {
   call { 
        controllers_APIToERPController_download8_invoker.call(controllers.APIToERPController.download())
   }
}
        

// @LINE:22
case controllers_Assets_versioned9_route(params) => {
   call(Param[String]("path", Right("/public")), params.fromPath[Asset]("file", None)) { (path, file) =>
        controllers_Assets_versioned9_invoker.call(controllers.Assets.versioned(path, file))
   }
}
        
}

}
     