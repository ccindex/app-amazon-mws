package controllers;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.PagingList;
import com.fasterxml.jackson.databind.JsonNode;
import misc.AmazonMWS;
import models.CronOrder;
import models.EbayOrderDetail;
import models.EbayOrderMaster;
import models.PlatformAccount;
import models.S3File;
import models.TrackingNumber;
import play.Play;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.util.*;

public class APIToERPController extends Controller {

    /**
     * 获取订单 id list
     * return  eg:[{"1":"ebay-order","fileName":"工作简报.xls","url":"/Users/kky979899/Play/_storage/s3/2015/8/工作简报.xls"}]
     */
    public static Result getS3FileIdList(){
        try {
            String account=request().getQueryString("account");   //账户
            String address=request().getQueryString("address");   //地址
            CronOrder cronOrder=CronOrder.find.where().eq("accountName", account).findUnique();

            if(cronOrder==null){
                return ok();
            }
            List<S3File> list=S3File.find.where().eq("cronOrder.id", cronOrder.id).eq("isHandle", false).findList();
            List<Map> returnList = new ArrayList();
            for(int i=0;i<list.size();i++){
                S3File temp=list.get(i);
                Map map = new HashMap();
                map.put("action", temp.action);
                String url="http://"+address+"/ap/download";
                map.put("url",url);
                map.put("fileName", temp.fileName);
                map.put("path", temp.path);
                map.put("id",temp.id);
                map.put("reportId",temp.reportId);
                map.put("type",temp.type);
                returnList.add(map);
            }
            return ok(Json.toJson(returnList));

        } catch (Exception e) {
            play.Logger.error(e.toString());
        }
        return ok();
    }
    /**
     * 新增cronoder用户
     */
    public static Result postCronOrder(){
        Map map=new HashMap<>();
        try {
            DynamicForm dynamicForm = Form.form().bindFromRequest();
            JsonNode node=Json.parse(dynamicForm.data().get("data"));
            PlatformAccount p=PlatformAccount.find.byId(Long.parseLong(node.get("account").get("id").asText()));

            CronOrder cronOrder=new CronOrder(p,node.get("config").asText(),Short.parseShort(node.get("minute").asText()));
            cronOrder.accountName=node.get("accountName").asText();
            cronOrder.apAddress=InetAddress.getLocalHost().getHostAddress();
            cronOrder.runnable=true;
            cronOrder.createdAt=new Date();

            //同一个账号只能关联一个AP 账号相同 ip相同
            //2015-10-13 老板要求  一个AP 可关联多个账号：同一渠道
            //现在只要账号不相同就OK
//            List<CronOrder> cron=CronOrder.find.where()
//            .or(Expr.eq("account.id", p.id),
//                    Expr.and(Expr.eq("apAddress", cronOrder.apAddress),
//                            Expr.eq("accountName", cronOrder.accountName))
//                    ).findList();
            List<CronOrder> cron=CronOrder.find.where().eq("accountName", cronOrder.accountName).eq("apAddress", cronOrder.apAddress).findList();
            if(null!=cron && cron.size()>0){
                map.put("msg","该AP上已经存在该用户绑定！");
                map.put("success",false);
            }else {
                cronOrder.save();
            }

        } catch (Exception e) {
            map.put("msg","数据错误！");
        }
        map.put("success",true);
        return ok(Json.toJson(map));
    }
    /**
     * 通过ip获取账号信息
     */
    public static Result getCronOrderList(){
        try {
           String ap=InetAddress.getLocalHost().getHostAddress();
            List<CronOrder> cronOrder=CronOrder.find.where().eq("apAddress",ap).findList();
            return ok(Json.toJson(cronOrder));
        } catch (Exception e) {
            play.Logger.error("通过ip获取账号信息:"+e.toString());
            return badRequest("数据错误！");
        }
    }
    /**
     * 通过acountName获取账号信息
     */
    public static Result getCronOrderByAccount(){
        try {
            String account=request().getQueryString("account");
            CronOrder cronOrder=CronOrder.find.where().eq("accountName",account).findUnique();
            if(null!=cronOrder){
                return ok(Json.toJson(cronOrder));
            }
            return ok();
        } catch (Exception e) {
            play.Logger.error("通过acountName获取账号信息:"+e.toString());
            return badRequest("数据错误！");
        }
    }
    /**
     * 修改cronOrder：按账号找?
     * 或者ID
     */
    public static Result updateCronOrder(){
        Map map=new HashMap<>();
        try {
            DynamicForm dynamicForm = Form.form().bindFromRequest();
            String account=dynamicForm.get("accountName");
            CronOrder cronOrder=CronOrder.find.where().eq("accountName", account).findUnique();
            if(null!=dynamicForm.get("runnable")){
                if(dynamicForm.get("runnable").equals("停止")){
                    cronOrder.status= CronOrder.Status.停止;
                }else if(dynamicForm.get("runnable").equals("运行")){
                    cronOrder.status= CronOrder.Status.运行;
                }
            }
            if(null!=dynamicForm.get("minute")) {
                cronOrder.minute=Short.parseShort(dynamicForm.get("minute"));
            }
            if(null!=dynamicForm.get("cronOrder")) {
                JsonNode node=Json.parse(dynamicForm.data().get("cronOrder"));
                cronOrder.accountName=node.get("accountName").asText();
                cronOrder.config=node.get("config").asText();
            }

            cronOrder.update();
            map.put("success",true);
        } catch (Exception e) {
            map.put("msg","数据错误！"+e.getMessage());
            map.put("success",false);
        }
        return ok(Json.toJson(map));
    }

    /**
     * putTracking
     */
    public static Result putTracking(){
        List<Map> list=new ArrayList<>();
        Map map=new HashMap<>();

        try {
            DynamicForm dynamicForm = Form.form().bindFromRequest();
            JsonNode node=Json.parse(dynamicForm.data().get("trackingNumber"));
            String platform=node.get("platform").asText();
            String data=node.get("data").asText();
            TrackingNumber trackingNumber=new TrackingNumber();
            trackingNumber.platform=node.get("platform").asText();
            trackingNumber.total=node.get("total").asInt();
            trackingNumber.config=node.get("config").asText();
            trackingNumber.accountName=node.get("accountName").asText();
            switch (platform){
                case "ebay":
                    for(int i=0;i<trackingNumber.total;i++){
                        Map returnMap=new HashMap<>();
                        if(i>=10){
                            Thread.sleep(60000);
                        }
                        Map temp =new HashMap<>();
                        temp.put("orderDetailId", node.get("data").get(i).get("orderDetailId").asText());
                        temp.put("orderId",node.get("data").get(i).get("orderId").asText());
                        temp.put("orderItemId",node.get("data").get(i).get("orderItemId").asText());
                        temp.put("quantityPurchased",node.get("data").get(i).get("quantityPurchased").asText());
                        temp.put("shipDate",node.get("data").get(i).get("shipDate").asText());
                        temp.put("carrierName",node.get("data").get(i).get("carrierName").asText());
                        temp.put("trackingNumber",node.get("data").get(i).get("trackingNumber").asText());
                        temp.put("shipMethod",node.get("data").get(i).get("shipMethod").asText());
                        temp.put("transactionId",node.get("data").get(i).get("transactionId").asText());
                        temp.put("carrierCode",node.get("data").get(i).get("carrierCode").asText());
//                        if(!temp.get("orderItemId").equals("") || !temp.get("transactionId").equals("")){
//                            EbaySDK ebaySDK=new EbaySDK();
//                            JsonNode json= Json.parse(trackingNumber.config);
//                            returnMap=ebaySDK.uploadTrackingNumber(json.get("token").asText(),temp);
//                        }else{
                            returnMap.put("success","false");
                            returnMap.put("msg","orderItemId跟transactionId不能为空");
                       // }
                        returnMap.put("orderDetailId",node.get("data").get(i).get("orderDetailId").asText());
                        list.add(returnMap);
                    }

                    break;
                case "amazon":
                    Map returnMap=new HashMap<>();
                    List<Map> tempList=new ArrayList<>();
                    for(int i=0;i<trackingNumber.total;i++){
                        Map temp =new HashMap<>();
                        temp.put("orderDetailId",node.get("data").get(i).get("orderDetailId").asText());
                        temp.put("orderId",node.get("data").get(i).get("orderId").asText());
                        temp.put("orderItemId",node.get("data").get(i).get("orderItemId").asText());
                        temp.put("quantityPurchased",node.get("data").get(i).get("quantityPurchased").asText());
                        temp.put("shipDate",node.get("data").get(i).get("shipDate").asText());
                        temp.put("carrierName",node.get("data").get(i).get("carrierName").asText());
                        temp.put("trackingNumber",node.get("data").get(i).get("trackingNumber").asText());
                        temp.put("shipMethod",node.get("data").get(i).get("shipMethod").asText());
                        temp.put("transactionId",node.get("data").get(i).get("transactionId").asText());
                        temp.put("shipDate", node.get("data").get(i).get("shipDate").asText());
                        temp.put("carrierCode", node.get("data").get(i).get("carrierCode").asText());
                        tempList.add(temp);
                        AmazonMWS amazonMWS = new AmazonMWS(Json.parse(trackingNumber.config));
                        returnMap = amazonMWS.uploadTrackingNumber(amazonMWS.createUploadXml(trackingNumber,temp),"_POST_ORDER_FULFILLMENT_DATA_");
                        returnMap.put("orderDetailId",node.get("data").get(i).get("orderDetailId").asText());
                        list.add(returnMap);
                    }
                    //trackingNumber.data=tempList;
                    break;
                default:
                    break;
            }
            return ok(Json.toJson(list));
        } catch (Exception e) {
            map.put("msg","数据错误！"+e.getMessage());
            map.put("success",false);
            list.add(map);
            return ok(Json.toJson(list));
        }
    }

    /**
     * 文件下载
     */
    public static Result download(){
        try {
            String fileId=request().getQueryString("fileId");
            S3File s3File=S3File.find.byId(Long.parseLong(fileId));
            String downName = "";
            try {
                downName = java.net.URLEncoder.encode(s3File.fileName, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            //将下载完的文件设置已经处理
            s3File.isHandle=true;
            s3File.updatedAt=new Date();
            s3File.update();

            response().setContentType("application/x-download");
            response().setHeader("Content-disposition", "attachment; filename=" + downName);
            return ok(new File(Play.application().configuration().getString("s3.file.path") + s3File.path));
            //return ok(new File(Play.application().configuration().getString("s3.file.path") + s3File.path+s3File.fileName));
        } catch (NumberFormatException e) {
            return badRequest("数据错误！");
        }
    }
    
}
