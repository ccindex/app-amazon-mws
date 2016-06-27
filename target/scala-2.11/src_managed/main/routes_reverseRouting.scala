// @SOURCE:F:/workspace_eclipse-jse/app-amazon-mws/conf/routes
// @HASH:8c7cad7ca02bcfb13dca86ec19d5dcb6ed78d9c1
// @DATE:Mon Jun 27 17:08:54 CST 2016

import Routes.{prefix => _prefix, defaultPrefix => _defaultPrefix}
import play.core._
import play.core.Router._
import play.core.Router.HandlerInvokerFactory._
import play.core.j._

import play.api.mvc._
import _root_.controllers.Assets.Asset
import _root_.play.libs.F

import Router.queryString


// @LINE:22
// @LINE:17
// @LINE:16
// @LINE:15
// @LINE:14
// @LINE:13
// @LINE:12
// @LINE:11
// @LINE:7
// @LINE:6
package controllers {

// @LINE:17
// @LINE:16
// @LINE:15
// @LINE:14
// @LINE:13
// @LINE:12
// @LINE:11
class ReverseAPIToERPController {


// @LINE:13
def getS3FileIdList(): Call = {
   import ReverseRouteContext.empty
   Call("GET", _prefix + { _defaultPrefix } + "ap/getS3FileIdList")
}
                        

// @LINE:17
def download(): Call = {
   import ReverseRouteContext.empty
   Call("GET", _prefix + { _defaultPrefix } + "ap/download")
}
                        

// @LINE:11
def getCronOrderList(): Call = {
   import ReverseRouteContext.empty
   Call("GET", _prefix + { _defaultPrefix } + "ap/getCronOrderList")
}
                        

// @LINE:15
def updateCronOrder(): Call = {
   import ReverseRouteContext.empty
   Call("POST", _prefix + { _defaultPrefix } + "ap/updateCronOrder")
}
                        

// @LINE:12
def getCronOrderByAccount(): Call = {
   import ReverseRouteContext.empty
   Call("GET", _prefix + { _defaultPrefix } + "ap/getCronOrderByAccount")
}
                        

// @LINE:14
def postCronOrder(): Call = {
   import ReverseRouteContext.empty
   Call("POST", _prefix + { _defaultPrefix } + "ap/postCronOrder")
}
                        

// @LINE:16
def putTracking(): Call = {
   import ReverseRouteContext.empty
   Call("POST", _prefix + { _defaultPrefix } + "ap/putTracking")
}
                        

}
                          

// @LINE:22
class ReverseAssets {


// @LINE:22
def versioned(file:Asset): Call = {
   implicit val _rrc = new ReverseRouteContext(Map(("path", "/public")))
   Call("GET", _prefix + { _defaultPrefix } + "assets/" + implicitly[PathBindable[Asset]].unbind("file", file))
}
                        

}
                          

// @LINE:7
// @LINE:6
class ReverseApplication {


// @LINE:6
def index(): Call = {
   import ReverseRouteContext.empty
   Call("GET", _prefix)
}
                        

// @LINE:7
def test(): Call = {
   import ReverseRouteContext.empty
   Call("GET", _prefix + { _defaultPrefix } + "test/a")
}
                        

}
                          
}
                  


// @LINE:22
// @LINE:17
// @LINE:16
// @LINE:15
// @LINE:14
// @LINE:13
// @LINE:12
// @LINE:11
// @LINE:7
// @LINE:6
package controllers.javascript {
import ReverseRouteContext.empty

// @LINE:17
// @LINE:16
// @LINE:15
// @LINE:14
// @LINE:13
// @LINE:12
// @LINE:11
class ReverseAPIToERPController {


// @LINE:13
def getS3FileIdList : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.APIToERPController.getS3FileIdList",
   """
      function() {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "ap/getS3FileIdList"})
      }
   """
)
                        

// @LINE:17
def download : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.APIToERPController.download",
   """
      function() {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "ap/download"})
      }
   """
)
                        

// @LINE:11
def getCronOrderList : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.APIToERPController.getCronOrderList",
   """
      function() {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "ap/getCronOrderList"})
      }
   """
)
                        

// @LINE:15
def updateCronOrder : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.APIToERPController.updateCronOrder",
   """
      function() {
      return _wA({method:"POST", url:"""" + _prefix + { _defaultPrefix } + """" + "ap/updateCronOrder"})
      }
   """
)
                        

// @LINE:12
def getCronOrderByAccount : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.APIToERPController.getCronOrderByAccount",
   """
      function() {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "ap/getCronOrderByAccount"})
      }
   """
)
                        

// @LINE:14
def postCronOrder : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.APIToERPController.postCronOrder",
   """
      function() {
      return _wA({method:"POST", url:"""" + _prefix + { _defaultPrefix } + """" + "ap/postCronOrder"})
      }
   """
)
                        

// @LINE:16
def putTracking : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.APIToERPController.putTracking",
   """
      function() {
      return _wA({method:"POST", url:"""" + _prefix + { _defaultPrefix } + """" + "ap/putTracking"})
      }
   """
)
                        

}
              

// @LINE:22
class ReverseAssets {


// @LINE:22
def versioned : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.Assets.versioned",
   """
      function(file) {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "assets/" + (""" + implicitly[PathBindable[Asset]].javascriptUnbind + """)("file", file)})
      }
   """
)
                        

}
              

// @LINE:7
// @LINE:6
class ReverseApplication {


// @LINE:6
def index : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.Application.index",
   """
      function() {
      return _wA({method:"GET", url:"""" + _prefix + """"})
      }
   """
)
                        

// @LINE:7
def test : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.Application.test",
   """
      function() {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "test/a"})
      }
   """
)
                        

}
              
}
        


// @LINE:22
// @LINE:17
// @LINE:16
// @LINE:15
// @LINE:14
// @LINE:13
// @LINE:12
// @LINE:11
// @LINE:7
// @LINE:6
package controllers.ref {


// @LINE:17
// @LINE:16
// @LINE:15
// @LINE:14
// @LINE:13
// @LINE:12
// @LINE:11
class ReverseAPIToERPController {


// @LINE:13
def getS3FileIdList(): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.APIToERPController.getS3FileIdList(), HandlerDef(this.getClass.getClassLoader, "", "controllers.APIToERPController", "getS3FileIdList", Seq(), "GET", """""", _prefix + """ap/getS3FileIdList""")
)
                      

// @LINE:17
def download(): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.APIToERPController.download(), HandlerDef(this.getClass.getClassLoader, "", "controllers.APIToERPController", "download", Seq(), "GET", """""", _prefix + """ap/download""")
)
                      

// @LINE:11
def getCronOrderList(): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.APIToERPController.getCronOrderList(), HandlerDef(this.getClass.getClassLoader, "", "controllers.APIToERPController", "getCronOrderList", Seq(), "GET", """#ERP_TO_AP API""", _prefix + """ap/getCronOrderList""")
)
                      

// @LINE:15
def updateCronOrder(): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.APIToERPController.updateCronOrder(), HandlerDef(this.getClass.getClassLoader, "", "controllers.APIToERPController", "updateCronOrder", Seq(), "POST", """""", _prefix + """ap/updateCronOrder""")
)
                      

// @LINE:12
def getCronOrderByAccount(): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.APIToERPController.getCronOrderByAccount(), HandlerDef(this.getClass.getClassLoader, "", "controllers.APIToERPController", "getCronOrderByAccount", Seq(), "GET", """""", _prefix + """ap/getCronOrderByAccount""")
)
                      

// @LINE:14
def postCronOrder(): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.APIToERPController.postCronOrder(), HandlerDef(this.getClass.getClassLoader, "", "controllers.APIToERPController", "postCronOrder", Seq(), "POST", """""", _prefix + """ap/postCronOrder""")
)
                      

// @LINE:16
def putTracking(): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.APIToERPController.putTracking(), HandlerDef(this.getClass.getClassLoader, "", "controllers.APIToERPController", "putTracking", Seq(), "POST", """""", _prefix + """ap/putTracking""")
)
                      

}
                          

// @LINE:22
class ReverseAssets {


// @LINE:22
def versioned(path:String, file:Asset): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.Assets.versioned(path, file), HandlerDef(this.getClass.getClassLoader, "", "controllers.Assets", "versioned", Seq(classOf[String], classOf[Asset]), "GET", """ Map static resources from the /public folder to the /assets URL path""", _prefix + """assets/$file<.+>""")
)
                      

}
                          

// @LINE:7
// @LINE:6
class ReverseApplication {


// @LINE:6
def index(): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.Application.index(), HandlerDef(this.getClass.getClassLoader, "", "controllers.Application", "index", Seq(), "GET", """ Home page""", _prefix + """""")
)
                      

// @LINE:7
def test(): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.Application.test(), HandlerDef(this.getClass.getClassLoader, "", "controllers.Application", "test", Seq(), "GET", """""", _prefix + """test/a""")
)
                      

}
                          
}
        
    