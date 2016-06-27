package misc;


import com.ebay.sdk.ApiCall;
import com.ebay.sdk.ApiContext;
import com.ebay.sdk.ApiCredential;
import com.ebay.sdk.call.GetOrdersCall;
import com.ebay.soap.eBLBaseComponents.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.paypal.core.Constants;
import models.*;
import play.Logger;
import play.Play;
import play.libs.Json;
import urn.ebay.api.PayPalAPI.GetTransactionDetailsReq;
import urn.ebay.api.PayPalAPI.GetTransactionDetailsRequestType;
import urn.ebay.api.PayPalAPI.GetTransactionDetailsResponseType;
import urn.ebay.api.PayPalAPI.PayPalAPIInterfaceServiceService;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class EbaySDK {
  SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  private String apiUrl = "https://api.ebay.com/wsapi";
  //private String apiUrl = "https://api.sandbox.ebay.com/wsapi";
  private String token = "AgAAAA**AQAAAA**aAAAAA**V743VQ**nY+sHZ2PrBmdj6wVnY+sEZ2PrA2dj6AFl4GkDZiGpgmdj6x9nY+seQ**a3QBAA**AAMAAA**11LPNEptF6YIOq0wBHJAMZxN++YZTLVIVq8gHgPPo/Wexng8BKwXsznqq32wJN0Y5vgMZPvuC4oRiMW1pn1DegeqNAatx/8oeyaxMeZmL2wJq0cvTkBysSbabeLrC9IaVQ1y2SE9UFxh14mBZjMLXkRyVem79ic79TFaXoRnFzQ0ncN8B8UllRvKxiBe106jNze7uC/sAPrtS61OOv32Q8CGGHj8XReuMKp2Bzg0Gihzlox2EQFFft30kd/NBduMTo4n5JiqTsElsoU64KH+H2btECdKgM6Y4x+oXMLrvAoa+fBoN2ElC5dYHmSchEBaSFKC7VwU4Zh8G8MVSgiyT6bapGhuLl7cCxvlS1g8yim94cJVShJ2zLN3JqX5bdZSvpNS5uH0hNCPtp7yRQxF68ARBOdugcIXuoMlJEZ2xmdhbjVoAeBqYF1+kFgA0YX0twwUsSW8rpwk5H1MtWUSQWo6Mj1eyvUFx/8mW5rBUkiGBSQbMXw87tZQ6W7E+h2Lg8NHWkGLEXNx/YhqK41ACbeIig8I4hItXXOL7X9hKemEt53xnFD482Gh+BaHYO0ytrToUT0/GAqlE6vVUZAPLplReH4CeR9KqbmFDqpLEYILrn3RBGzKVXscxlCbmcuAN67LhwlWD8LLdo1z5TR2vfMmFxt69aE7QGoAgp2rMq1lx03Sp9y+C6VYugXKJiMCVyvG0ydJrnh+FMeawQuG5hSNxqV40SQFmfPzlf29AStMs/ogOoihEKpcdsnGHjRq";
  private GetOrdersCall call;
    /**
     * token \apiUrl
     * 设置apicontent
     * @return
     */
  public ApiContext getApiContent() {
    ApiContext apiContext = new ApiContext();

    ApiCredential apiCredential = apiContext.getApiCredential();
    apiCredential.seteBayToken(this.token);
    apiContext.setApiServerUrl(this.apiUrl);

    return apiContext;
  }

  /**
   * 返回的response中，增加了OrderLineItemID字段，这个字段由itemID-TransactionID组成，是一个全局唯一的变量
   * @2015-11-27:修改获取时间为当前时间到上次执行时间往前推一个小时。减少进单量。修复原来没有按照page获取订单的bug
   * @return
   */
  public OrderType[] getOrderArrayBySeller(Date date) throws Exception{
    OrderType[] orders = {};
    ApiContext apiContext = this.getApiContent();
    this.call = new GetOrdersCall(apiContext);
    DetailLevelCodeType[] level = new DetailLevelCodeType[]{DetailLevelCodeType.RETURN_ALL};
    call.setDetailLevel(level);
    call.setOrderRole(TradingRoleCodeType.SELLER);

    Calendar begin=Calendar.getInstance();
    Calendar end=Calendar.getInstance();
    begin.setTime(date);
    //begin.add(Calendar.MINUTE, -240);
    call.setModTimeFrom(begin);
    call.setModTimeTo(end);
    //call.setNumberOfDays(1);

    try {
      orders = call.getOrders();
    } catch (TimeoutException e) {
      Thread.sleep(60000);
      orders = call.getOrders();
    }
    return orders;
  }

  public String generateFilePath(LocalDate now, String prePath,CronOrder cronOrder) {
    String path =  "" +cronOrder.accountName + "/"+ now.getYear() + "/" + now.getMonth().getValue() + "/"+now.getDayOfMonth()+"/";
    File dir = new File(prePath + path);

    if (!dir.exists()) {
      dir.mkdirs();
    }

    return path;
  }

  public String generateFileName(long uniqueKey, String format) {
    return uniqueKey + "." + format;
  }

  /**
   * 获取订单并保存为文件
   * @param preFilePath
   * @param filePath
   * @param cronOrder
   * @return
   * @throws Exception
   */
  public void save(String preFilePath, String filePath,CronOrder cronOrder) throws Exception{
    User me = User.find.where().eq("email", "cron@cron.com").findUnique();
    long startTime = System.currentTimeMillis();

    File file = null;
    OrderType[] orders = this.getOrderArrayBySeller(cronOrder.fireAt);
    int pageNumber=call.getReturnedPaginationResult().getTotalNumberOfPages();
    FileWriter fileWriter = null;
    BufferedWriter bufferedWriter = null;
    try {
      if(pageNumber>=0){
        for (int n = 1; n <= pageNumber; n++) {
          String _fileName = startTime + "";
          if (n != 1) {
            PaginationType pagination = new PaginationType();
            pagination.setPageNumber(n);
            pagination.setEntriesPerPage(100);
            call.setPagination(pagination);
            orders = call.getOrders();
            _fileName = _fileName + "_" + (n);
          }
          _fileName = _fileName + ".txt";
          file = new File(preFilePath + filePath + _fileName);

          fileWriter = new FileWriter(file);
          bufferedWriter = new BufferedWriter(fileWriter);

          bufferedWriter.write("order_id\torder_status\tadjustment_amount_currency\tadjustment_amount\tamount_paid_currency\tamount_paid\tamount_save_currency\tamount_save\tpayment_satus\tlast_modified_time\tpayment_method\tstatus\tsales_tax_percent\tsales_tax_state\tsales_tax_shipping_inclueded_in_tax\tsales_tax_currency\tsales_tax\tselling_manager_sales_record_number\tcreate_time\tpayment_method\tshipping_address_name\tshipping_address_street1\tshipping_address_street2\tshipping_address_city\tshipping_address_state\tshipping_address_country\tshipping_address_country_name\tshipping_address_phone\tshipping_address_postcode\tshipping_address_id\tshipping_address_owner\tshipping_address_address_id\tshipping_service\tshipping_service_cost_currency\tshipping_service_cost\tsub_total_currency\tsub_total\ttotal_currency\ttotal\ttransaction_buyer_email\ttransaction_shippingdetail_sellingmannager_sales_recordnumber\tcreate_date\tsku\tquantity_purchased\tstatus_payment_hold_status\ttransaction_id\ttransaction_id_currency\ttransaction_price_currency\ttransaction_price\ttransaction_platform\ttransaction_order_line_item_id\tbuyer_user_id\tpaid_time\tfirstName\tlastName\tpayer\tpaymentStatus\t" + "paymentTime\tpaypalFeeAmount\tActualShippingCost\tActualHandlingCost\tInsuranceFee\tShippingDiscountAmount\n");
          for (OrderType i : orders) {
            String orderStatus = i.getOrderStatus().value().toString();
            String paymentStatus = i.getCheckoutStatus().getStatus().value().toString();
            String paymentMethod = i.getCheckoutStatus().getPaymentMethod().value().toString();

            GetTransactionDetailsResponseType responseType = null;
            //cancelled 取消的订单   cancelpending 取消中的订单
            if( (orderStatus.toLowerCase().equals("shipped") || orderStatus.toLowerCase().equals("completed"))
                && (paymentMethod.toLowerCase().equals("paypal") || paymentMethod.toLowerCase().equals("paypalpredit"))
                && paymentStatus.toUpperCase().equals("COMPLETE") ){
              String transactionId = i.getExternalTransaction(0).getExternalTransactionID();
              Map map = setPaypalConfigMap(cronOrder.paypalId);
              try {
                responseType = getTransactionDetails(transactionId, map);
              } catch (Exception e) {
                try {
                  responseType = getTransactionDetails(transactionId, map);
                } catch (Exception ex) {
                  //responseType=getTransactionDetails(transactionId,map);
                  Logger.error(sdf.format(new Date()) + "||cronOrder：" + cronOrder.accountName + "||this is question ,later;");
                }
              }
            }
            String discountAmmount = "";
            if (i.getTransactionArray().getTransaction(0).getSellerDiscounts() != null) {
              if (null != i.getTransactionArray().getTransaction(0).getSellerDiscounts().getSellerDiscount(0)
                  && null != i.getTransactionArray().getTransaction(0).getSellerDiscounts()
                  && null != i.getTransactionArray().getTransaction(0).getSellerDiscounts().getSellerDiscount(0).getShippingDiscountAmount()) {
                discountAmmount = String.valueOf(i.getTransactionArray().getTransaction(0).getSellerDiscounts().getSellerDiscount(0).getShippingDiscountAmount().getValue());
              }
            }
            bufferedWriter.write(i.getOrderID() + "\t" +
                i.getOrderStatus() + "\t" +
                i.getAdjustmentAmount().getCurrencyID() + "\t" +
                i.getAdjustmentAmount().getValue() + "\t" +
                i.getAmountPaid().getCurrencyID() + "\t" +
                i.getAmountPaid().getValue() + "\t" +
                i.getAmountSaved().getCurrencyID() + "\t" +
                i.getAmountSaved().getValue() + "\t" +
                i.getCheckoutStatus().getEBayPaymentStatus() + "\t" +
                i.getCheckoutStatus().getLastModifiedTime().getTime() + "\t" +
                i.getCheckoutStatus().getPaymentMethod() + "\t" +
                i.getCheckoutStatus().getStatus() + "\t" +
                i.getShippingDetails().getSalesTax().getSalesTaxPercent() + "\t" +
                i.getShippingDetails().getSalesTax().getSalesTaxState() + "\t" +
                i.getShippingDetails().getSalesTax().isShippingIncludedInTax() + "\t" +
                i.getShippingDetails().getSalesTax().getSalesTaxAmount().getCurrencyID() + "\t" +
                i.getShippingDetails().getSalesTax().getSalesTaxAmount().getValue() + "\t" +
                i.getShippingDetails().getSellingManagerSalesRecordNumber() + "\t" +
                i.getCreatedTime().getTime() + "\t" +
                i.getPaymentMethods()[0] + "\t" +
                i.getShippingAddress().getName() + "\t" +
                i.getShippingAddress().getStreet1() + "\t" +
                i.getShippingAddress().getStreet2() + "\t" +
                i.getShippingAddress().getCityName() + "\t" +
                i.getShippingAddress().getStateOrProvince() + "\t" +
                i.getShippingAddress().getCountry() + "\t" +
                i.getShippingAddress().getCountryName() + "\t" +
                i.getShippingAddress().getPhone() + "\t" +
                i.getShippingAddress().getPostalCode() + "\t" +
                i.getShippingAddress().getAddressID() + "\t" +
                i.getShippingAddress().getAddressOwner() + "\t" +
                i.getShippingAddress().getExternalAddressID() + "\t" +
                i.getShippingServiceSelected().getShippingService() + "\t" +
                ((i.getShippingServiceSelected()==null || i.getShippingServiceSelected().getShippingServiceCost() == null) ? "" : i.getShippingServiceSelected().getShippingServiceCost().getCurrencyID()) + "\t" +
                ((i.getShippingServiceSelected()==null || i.getShippingServiceSelected().getShippingServiceCost() == null) ? "" : i.getShippingServiceSelected().getShippingServiceCost().getValue()) + "\t" +
                (i.getSubtotal() == null ? "" : i.getSubtotal().getCurrencyID()) + "\t" +
                (i.getSubtotal() == null ? "" : i.getSubtotal().getValue()) + "\t" +
                (i.getTotal() == null ? "" : i.getTotal().getCurrencyID()) + "\t" +
                (i.getTotal() == null ? "" : i.getTotal().getValue()) + "\t" +
                (i.getTransactionArray().getTransaction(0).getBuyer() == null ? "" : i.getTransactionArray().getTransaction(0).getBuyer().getEmail()) + "\t" +
                (i.getTransactionArray().getTransaction(0).getShippingDetails() == null ? "" : i.getTransactionArray().getTransaction(0).getShippingDetails().getSellingManagerSalesRecordNumber()) + "\t" +
                i.getTransactionArray().getTransaction(0).getCreatedDate().getTime() + "\t" +
                (i.getTransactionArray().getTransaction(0).getItem() == null ? "" : i.getTransactionArray().getTransaction(0).getItem().getSKU()) + "\t" +
                i.getTransactionArray().getTransaction(0).getQuantityPurchased() + "\t" +
                (i.getTransactionArray().getTransaction(0).getStatus() == null ? "" : i.getTransactionArray().getTransaction(0).getStatus().getPaymentHoldStatus()) + "\t" +
                i.getTransactionArray().getTransaction(0).getTransactionID() + "\t" +
                i.getTransactionArray().getTransaction(0).getTransactionPrice().getCurrencyID() + "\t" +
                (i.getTransactionArray().getTransaction(0).getTransactionPrice() == null ? "" : i.getTransactionArray().getTransaction(0).getTransactionPrice().getCurrencyID()) + "\t" +
                (i.getTransactionArray().getTransaction(0).getTransactionPrice() == null ? 0 : i.getTransactionArray().getTransaction(0).getTransactionPrice().getValue()) + "\t" +
                //i.getTransactionArray().getTransaction(0).getTransactionSiteID() + "\t" +
                i.getTransactionArray().getTransaction(0).getPlatform() + "\t" +
                i.getTransactionArray().getTransaction(0).getOrderLineItemID() + "\t" + i.getBuyerUserID() + "\t" + (i.getPaidTime() == null ? "" : i.getPaidTime().getTime()) + "\t" +
                (i.getTransactionArray().getTransaction(0).getBuyer() == null ? "" : i.getTransactionArray().getTransaction(0).getBuyer().getUserFirstName()) + "\t" +
                (i.getTransactionArray().getTransaction(0).getBuyer() == null ? "" : i.getTransactionArray().getTransaction(0).getBuyer().getUserLastName()) + "\t" +
                (responseType == null ? "" : responseType.getPaymentTransactionDetails().getPayerInfo().getPayer()) + "\t" +
                ((i.getMonetaryDetails() == null || i.getMonetaryDetails().getPayments() == null) ? "" : i.getMonetaryDetails().getPayments().getPayment(0).getPaymentStatus()) + "\t" +
                ((i.getMonetaryDetails() == null || i.getMonetaryDetails().getPayments() == null) ? "" : i.getMonetaryDetails().getPayments().getPayment(0).getPaymentTime().getTime()) + "\t" +
                ((responseType == null || responseType.getPaymentTransactionDetails()==null || responseType.getPaymentTransactionDetails().getPaymentInfo()==null ||  responseType.getPaymentTransactionDetails().getPaymentInfo().getFeeAmount()==null ) ? "" : responseType.getPaymentTransactionDetails().getPaymentInfo().getFeeAmount().getValue()) + "\t" +
                (i.getTransactionArray().getTransaction(0).getActualShippingCost() == null ? "" : i.getTransactionArray().getTransaction(0).getActualShippingCost().getValue()) + "\t" +
                (i.getTransactionArray().getTransaction(0).getActualHandlingCost() == null ? "" : i.getTransactionArray().getTransaction(0).getActualHandlingCost().getValue()) + "\t" +
                (i.getShippingDetails().getInsuranceFee() == null ? "" : i.getShippingDetails().getInsuranceFee().getValue()) + "\t" +
                discountAmmount + "\t" +
                "\n");

          }

          bufferedWriter.close();
          fileWriter.close();

          S3File s3File = new S3File(_fileName, "text/plain", me, file.length(), filePath + _fileName,null,_fileName);
          s3File.action="ebay-order";
          s3File.cronOrder=cronOrder;
          s3File.save();

          OrderLog orderLog = new OrderLog(file.length(), System.currentTimeMillis() - startTime, cronOrder);
          orderLog.save();
        }
      }

    } catch (Exception e) {
      bufferedWriter.close();
      fileWriter.close();
      Logger.error(sdf.format(new Date()) + "||cronOrder：" + cronOrder.accountName);
      throw new Exception(e);
    }
  }

  public void handle(CronOrder cronOrder) throws Exception{
    Logger.info(sdf.format(new Date())+"ebay getOrders;account:" +cronOrder.accountName);
    LocalDate now = LocalDate.now();
    String preFilePath = Play.application().configuration().getString("s3.file.path");      //获取本地路径
    String filePath = this.generateFilePath(now, preFilePath,cronOrder);                              //制造2015/11/20文件夹

    JsonNode config = Json.parse(cronOrder.config);
    this.token = config.get("token").asText();

    this.save(preFilePath, filePath,cronOrder);
  }

  /**
   * CompleteSaleCall
   * getorders:返回的response中，增加了OrderLineItemID字段，这个字段由itemID-TransactionID组成，是一个全局唯一的变量
   * 上传tracking  CompleteSale
   */
  public Map uploadTrackingNumber(String token, Map map) {
    Map returnMap = new HashMap<>();
    try {
      this.token = token;
      ShipmentTrackingDetailsType[] stdt;
      ShipmentType st;
      //CompleteSaleCall csc=new CompleteSaleCall(this.getApiContent());
      //csc.setOrderID(map.get("ebayId").toString());
      //csc.setOrderLineItemID(map.get("itemId").toString());

      stdt = new ShipmentTrackingDetailsType[1];
      stdt[0] = new ShipmentTrackingDetailsType();

      stdt[0].setShippingCarrierUsed(map.get("carrierCode").toString());
      stdt[0].setShipmentTrackingNumber(map.get("trackingNumber").toString());
      st = new ShipmentType();
      st.setShipmentTrackingDetails(stdt);
      //st.setShippedTime(map.get("trakingNumber").toString());
      //csc.setShipment(st);
      //csc.setShipped(true);
      //csc.completeSale();
      ApiCall call = new ApiCall(getApiContent());
      CompleteSaleRequestType req = new CompleteSaleRequestType();
      //req.setOrderID(map.get("orderId").toString());
      req.setTransactionID(map.get("transactionId").toString());
      req.setItemID(map.get("orderItemId").toString());
      req.setShipped(true);
      req.setShipment(st);
      CompleteSaleResponseType resp = (CompleteSaleResponseType) call.execute(req);
      if (resp.getAck().name().equals("SUCCESS")) {
        returnMap.put("success", "true");
      } else {

        returnMap.put("success","false");
        returnMap.put("orderDetailId",map.get("orderDetailId"));
        //System.out.println("orderId:" + map.get("orderId").toString() + ";orderItemId:" + map.get("orderItemId").toString() + ";errorMsg:" + resp.getErrors(0).getLongMessage());
        //Logger.info(sdf.format(new Date())+"orderId:" + map.get("orderId").toString() + ";orderItemId:" + map.get("orderItemId").toString() + ";errorMsg:" + resp.getErrors(0).getLongMessage());
        returnMap.put("msg","orderId:"+map.get("orderId").toString()+";orderItemId:"+map.get("orderItemId").toString()+";errorMsg:"+ resp.getErrors(0).getLongMessage());
      }

    } catch (Exception e) {
      returnMap.put("success","false");
      //System.out.println("orderId:"+map.get("orderId").toString()+";orderItemId:"+map.get("orderItemId").toString()+";errorMsg:"+ e.toString());
      Logger.info(sdf.format(new Date())+"msg","orderId:"+map.get("orderId").toString()+";orderItemId:"+map.get("orderItemId").toString()+";errorMsg:"+ e.toString());
      returnMap.put("msg","orderId:"+map.get("orderId").toString()+";orderItemId:"+map.get("orderItemId").toString()+";errorMsg:"+ e.toString());
    }
    return returnMap;
  }

  public GetTransactionDetailsResponseType getTransactionDetails(String transactionId,Map map) throws Exception{
    PayPalAPIInterfaceServiceService service = new PayPalAPIInterfaceServiceService(map);
    GetTransactionDetailsReq req = new GetTransactionDetailsReq();
    GetTransactionDetailsRequestType getTransactionDetailsRequest = new GetTransactionDetailsRequestType();
    getTransactionDetailsRequest.setTransactionID(transactionId);
    req.setGetTransactionDetailsRequest(getTransactionDetailsRequest);
    GetTransactionDetailsResponseType transactionDetailsResponseType = service.getTransactionDetails(req);
    return transactionDetailsResponseType;
  }
  public Map setPaypalConfigMap(Long id) throws Exception{
    Map map=new HashMap<>();
    PaypalConfig config=PaypalConfig.find.byId(id);
    map.put(Constants.MODE,Constants.LIVE);
    map.put("acct1.UserName",config.username);
    map.put("acct1.Password",config.password);
    map.put("acct1.Signature",config.signature);
    map.put("http.Retry","1");      //重新连接三次
    return map;

  }
}
