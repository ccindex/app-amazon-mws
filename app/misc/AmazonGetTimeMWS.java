package misc;


import com.amazonaws.mws.MarketplaceWebService;
import com.amazonaws.mws.MarketplaceWebServiceClient;
import com.amazonaws.mws.MarketplaceWebServiceConfig;
import com.amazonaws.mws.MarketplaceWebServiceException;
import com.amazonaws.mws.model.*;
import com.fasterxml.jackson.databind.JsonNode;
import models.*;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import play.Logger;
import play.Play;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.*;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

/**
 * amazon订单类
 */
public class AmazonGetTimeMWS {
  SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  private String accessKeyId;
  private String secretAccessKey;
  private String merchantId;
  private String amazonURL;
  private String marketplaceId;
  private MarketplaceWebService service;
  private GetReportListRequest requestList;
  private GetReportRequest request;
  private GetReportListResponse responseList;
  private GetReportResponse response;

  public String orderType = "_GET_FLAT_FILE_ORDERS_DATA_";
  public String orderUpdateType = "_GET_FLAT_FILE_ALL_ORDERS_DATA_BY_LAST_UPDATE_";

  public AmazonGetTimeMWS(JsonNode api) {
    this.accessKeyId = api.get("access_key_id").textValue();
    this.secretAccessKey = api.get("secret_access_key").textValue();
    this.merchantId = api.get("merchant_id").textValue();
    this.marketplaceId=api.get("marketplace_id").textValue();      //一个对应一个账号
    this.amazonURL = api.get("service_url").textValue();

    MarketplaceWebServiceConfig config = new MarketplaceWebServiceConfig();
    config.setServiceURL(this.amazonURL);
    this.service = new MarketplaceWebServiceClient(this.accessKeyId, this.secretAccessKey, "", "", config);
  }

  /**
   * 配置amazon数据
   */
  public void requestOrderAmazon() throws DatatypeConfigurationException {
    this.requestList = new GetReportListRequest();
    this.requestList.setMerchant(this.merchantId);
    this.requestList.setMaxCount(100);

    //设置获取的报告今天开始
    GregorianCalendar cal = new GregorianCalendar();
    GregorianCalendar calEnd = new GregorianCalendar();
    Calendar c=Calendar.getInstance();
    c.set(2015,11, 1, 0, 0, 0);  //2015-12-01
    cal.setTime(c.getTime());
    calEnd.setTime(new Date());
    XMLGregorianCalendar gc = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
    XMLGregorianCalendar end = DatatypeFactory.newInstance().newXMLGregorianCalendar(calEnd);
    this.requestList.setAvailableFromDate(gc);
    this.requestList.setAvailableToDate(end);
    List<String> stringList = new ArrayList<>();
    stringList.add(this.orderType);
    stringList.add(this.orderUpdateType);
    TypeList typeList = new TypeList(stringList);
    this.requestList.setReportTypeList(typeList);
  }

  /**
   * 获取amazon reportListResult
   * @return
   */
  public List<List<ReportInfo>> responseOrderAmazon() {
    List<List<ReportInfo>> list=new ArrayList<>();

    try {
      this.responseList = this.service.getReportList(this.requestList);

      if (this.responseList.isSetGetReportListResult()) {
        GetReportListResult getReportListResult = this.responseList.getGetReportListResult();
        //增加如list 如果还有下一页
        list.add(getReportListResult.getReportInfoList());
        int index=1;
        if(getReportListResult.isHasNext()){
          GetReportListByNextTokenRequest reportListByNextTokenRequest=new GetReportListByNextTokenRequest();
          GetReportListByNextTokenResponse reportListByNextTokenResponse=new GetReportListByNextTokenResponse();
          String anto_token=getReportListResult.getNextToken();
          reportListByNextTokenRequest.setNextToken(anto_token);
          reportListByNextTokenRequest.setMerchant(this.merchantId);
          reportListByNextTokenResponse=this.service.getReportListByNextToken(reportListByNextTokenRequest);
          GetReportListByNextTokenResult reportListByNextTokenResult=reportListByNextTokenResponse.getGetReportListByNextTokenResult();
          list.add(reportListByNextTokenResult.getReportInfoList());
          while (reportListByNextTokenResult.isHasNext()){
            index++;
            if(index>=10){
              Thread.sleep(45000);
            }
            anto_token=reportListByNextTokenResult.getNextToken();
            reportListByNextTokenRequest.setNextToken(anto_token);
            reportListByNextTokenRequest.setMerchant(this.merchantId);
            reportListByNextTokenResponse=this.service.getReportListByNextToken(reportListByNextTokenRequest);
            reportListByNextTokenResult=reportListByNextTokenResponse.getGetReportListByNextTokenResult();
            list.add(reportListByNextTokenResult.getReportInfoList());
          }
        }
        return list;
      }
      //System.out.println("1.data is null");
      Logger.error(new Date()+":data is null!");
    } catch (Exception e) {
      e.printStackTrace();
    }

    return null;
  }

  /**
   * 生成filename
   * @param reportId
   * @param reportType
   * @return
   */
  public String generateReportFileName(String reportId, String reportType) {
    Date date = new Date();
    return reportId + "-" + reportType + "-" + date.getYear() + "-" + date.getMonth() + "-" + date.getDate();
  }

  /**
   * 设置输出参数 request
   * @param reportInfo
   * @param reportFileName
   * @param reportFilePath
   */
  public void requestOrderDetailAmazon(ReportInfo reportInfo, String reportFileName, String reportFilePath) {
    this.request = new GetReportRequest();
    this.request.setMerchant(this.merchantId);
    this.request.setReportId(reportInfo.getReportId());

    try {
      OutputStream report = new FileOutputStream(Play.application().configuration().getString("s3.file.path") + reportFilePath + reportFileName);
      request.setReportOutputStream(report);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void responseOrderDetailAmazon() {
    try {
      this.response = this.service.getReport(this.request);
    } catch (MarketplaceWebServiceException e) {
      e.printStackTrace();
    }
  }

  /**
   * 处理amazon订单
   * @param reportInfoList
   * @param cronOrder
   */
  public void dealWithOrderDetailsAmazon(List<List<ReportInfo>> reportInfoList, CronOrder cronOrder) {
    Logger.info(sdf.format(new Date())+"--deal orders;account:" +cronOrder.accountName);
    User me = User.find.where().eq("email", "cron@cron.com").findUnique();
    String reportFileName;
    File reportFile;
    long startTime;
    long endTime;
    ReportList amazonReportList;
    LocalDate now = LocalDate.now();
    int month = now.getMonth().getValue();
    int year = now.getYear();
    String reportFilePath = year + "/" + month + "/";

    File dirPath = new File(Play.application().configuration().getString("s3.file.path") + reportFilePath);
    if (!dirPath.exists()) {
      dirPath.mkdirs();
    }
    int index=1;
    if(null!=reportInfoList && reportInfoList.size()>0) {
      for (int i = 0; i < reportInfoList.size(); i++) {
        List<ReportInfo> listReportInfo = reportInfoList.get(i);
        //对reportinfo循环，如 reportrequestid不为空表示已经请求第一次
        for (ReportInfo reportInfo : listReportInfo) {
          if (ReportList.findByRequestId(reportInfo.getReportId()) == null) {
            index++;
            if (index > 15) {
              try {
                Thread.sleep(60000);
              } catch (InterruptedException e) {
                e.printStackTrace();
              }
            }
            startTime = System.currentTimeMillis();
            reportFileName = this.generateReportFileName(reportInfo.getReportId(), reportInfo.getReportType());
            this.requestOrderDetailAmazon(reportInfo, reportFileName, reportFilePath);
            this.responseOrderDetailAmazon();      //获取amazon返回值  response

            reportFile = new File(Play.application().configuration().getString("s3.file.path") + reportFilePath + reportFileName);
            endTime = System.currentTimeMillis();

            if (reportFile.exists()) {
              amazonReportList = new ReportList(reportInfo.getReportId(), reportInfo.getReportType());
              amazonReportList.save();

              S3File s3File = new S3File(reportFileName, "text/plain", me, reportFile.length(), "" + year + "/" + month + "/" + reportFileName, reportInfo.getReportId(), reportFileName);

              //TODO 不做文件处理 只保存文件 然后返回给ERP
              String action = "";
              if (reportInfo.getReportType().equals(this.orderType)) {
                action = "order-amazon";
              } else {
                action = "order-update-amazon";
              }
              s3File.action = action;
              s3File.cronOrder = cronOrder;
              s3File.type = reportInfo.getReportType();
              s3File.save();
              OrderLog logOrderAmazon = new OrderLog(reportFile.length(), endTime - startTime, cronOrder);
              logOrderAmazon.save();

            } else {
              OrderLog logOrderAmazon = new OrderLog(reportFile.length(), endTime - startTime, cronOrder);
              logOrderAmazon.status = false;
              logOrderAmazon.save();
            }
          }
        }
      }
    }

  }

  public void dealWithOrderAmazon(CronOrder cronOrder) throws DatatypeConfigurationException {
    this.requestOrderAmazon();
    this.dealWithOrderDetailsAmazon(this.responseOrderAmazon(), cronOrder);
  }
  public Map uploadTrackingNumber(String fileName, String feedType){
    Map returnMap = new HashMap<>();
    try {
      SubmitFeedRequest request=getSubmitFeedRequest(fileName,feedType);
      SubmitFeedResponse response = this.service.submitFeed(request);
      //System.out.print();
      if(!response.getSubmitFeedResult().getFeedSubmissionInfo().getFeedProcessingStatus().equals("_CANCELLED_")){
        //Logger.info(sdf.format(new Date())+"--上传状态："+response.getSubmitFeedResult().getFeedSubmissionInfo().getFeedProcessingStatus());
        returnMap.put("success", "true");
      }else{
        returnMap.put("success", "false");
        returnMap.put("msg", response.getResponseHeaderMetadata().getResponseContext());
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return returnMap;
  }

  /**
   * region 构造请求 Submit Amazon信息
   */
  public SubmitFeedRequest getSubmitFeedRequest(String fileName, String feedType)  throws Exception//传入两个参数:1.文件名称 2.文件操作类别
  {
    SubmitFeedRequest request = new SubmitFeedRequest();
    request.setMerchant(this.merchantId);

    final IdList marketplaces = new IdList(Arrays.asList(this.marketplaceId));
    //Logger.info("merchantId：" + this.merchantId+"设置marketplaceId：" + this.marketplaceId);
    request.setMarketplaceIdList(marketplaces);
    //request.setContentType();
    File file=new File(fileName);
    request.setFeedContent(new FileInputStream(file));
    request.setFeedType(feedType);
    request.setContentMD5(computeContentMD5HeaderValue(new FileInputStream(new File(fileName))));
    return request;
  }

  /**
   * <?xml version="1.0" encoding="UTF-8"?>
   *<AmazonEnvelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:noNamespaceSchemaLocation="amzn-envelope.xsd">
   *<Header>
   *<DocumentVersion>1.01</DocumentVersion>
   *<MerchantIdentifier>MYID</MerchantIdentifier>
   *</Header>
   *<MessageType>OrderFulfillment</MessageType>
   *<Message>
   *<MessageID>1</MessageID>
   *<OrderFulfillment>
   *<AmazonOrderID>XXXXXXXXXXXXXXXX</AmazonOrderID>
   *<FulfillmentDate>2012-14-12T11:00:00</FulfillmentDate>
   *<FulfillmentData>
   *<CarrierName>USPS</CarrierName>
   *<ShippingMethod>Standard</ShippingMethod>
   *<ShipperTrackingNumber>XXXXXXXXXXXXXXXXXXX</ShipperTrackingNumber>
   *</FulfillmentData>
   *<Item>
   *<AmazonOrderItemCode>1234567890123456789</AmazonOrderItemCode>
   *<Quantity>1</Quantity>
   *</Item>
   *</OrderFulfillment>
   *</Message>
   *</AmazonEnvelope>
   * @param trackingNumber
   * @return
   * @throws Exception
   */
  public String createUploadXml(TrackingNumber trackingNumber,Map temp) throws Exception {
    String fileName = "";
    Document document = DocumentHelper.createDocument();
    Element AmazonEnvelope = document.addElement("AmazonEnvelope");
    AmazonEnvelope.addNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
    AmazonEnvelope.addAttribute("xsi:noNamespaceSchemaLocation","amzn-envelope.xsd");
    Element Header = AmazonEnvelope.addElement("Header");
    Element DocumentVersion = Header.addElement("DocumentVersion");
    Element MerchantIdentifier = Header.addElement("MerchantIdentifier");
    Element MessageType = AmazonEnvelope.addElement("MessageType");

    DocumentVersion.setText("1.02");
    MerchantIdentifier.setText("amazon-TrackingNumber");
    MessageType.setText("OrderFulfillment");
    // <MerchantIdentifier>此选项可以随便填写，<ShippingMethod>根据自己的需求可以有可以没有，如果要确认多个订单可以增加多个<message>
    List<Map> data = trackingNumber.data;
    Element Message = AmazonEnvelope.addElement("Message");
    Element MessageID = Message.addElement("MessageID");
    Element OrderFulfillment = Message.addElement("OrderFulfillment");
    Element AmazonOrderID = OrderFulfillment.addElement("AmazonOrderID");
    Element MerchantFulfillmentID = OrderFulfillment.addElement("MerchantFulfillmentID");
    Element FulfillmentDate = OrderFulfillment.addElement("FulfillmentDate");
    Element FulfillmentData = OrderFulfillment.addElement("FulfillmentData");
    Element CarrierName = FulfillmentData.addElement("CarrierName");
    Element ShippingMethod = FulfillmentData.addElement("ShippingMethod");
    Element ShipperTrackingNumber = FulfillmentData.addElement("ShipperTrackingNumber");
    //Element Item = OrderFulfillment.addElement("Item");
    //Element AmazonOrderItemCode = Item.addElement("AmazonOrderItemCode");
    ShippingMethod.setText("Standard");
    FulfillmentDate.setText(temp.get("shipDate").toString());
    MessageID.setText("1");
    AmazonOrderID.setText(temp.get("orderId").toString());
    MerchantFulfillmentID.setText(temp.get("orderId").toString());
    CarrierName.setText(temp.get("carrierCode").toString());

    ShipperTrackingNumber.setText(temp.get("trackingNumber").toString());
    //AmazonOrderItemCode.setText(temp.get("orderItemId").toString());

    //Logger.info("================orderId：" + temp.get("orderId").toString() + ";carrierCode：" + temp.get("carrierCode").toString() + ";trackingNumber：" + temp.get("trackingNumber").toString());
    //Logger.info("================orderItemId：" + temp.get("orderItemId").toString()+"==============================================");

    fileName=Play.application().configuration().getString("template.file.path") + "amazon-"+new SimpleDateFormat("yyMMddHHmmss").format(new Date())+".xml";
    File file = new File(fileName);
    file.getParentFile().mkdirs();
    if(!file.exists()){
      file.createNewFile();
    }
    Writer filewriter = new FileWriter(fileName);
    OutputFormat format = OutputFormat.createPrettyPrint();//缩减型格式
    format.setEncoding("utf-8");//设置编码
    //format.setTrimText(false);//设置text中是否要删除其中多余的空格
    XMLWriter xmlWriter = new XMLWriter(filewriter,format);
    xmlWriter.write(document);
    xmlWriter.close();
    return fileName;
  }

  public static String computeContentMD5HeaderValue( FileInputStream fis )
      throws IOException, NoSuchAlgorithmException {

    DigestInputStream dis = new DigestInputStream( fis,
        MessageDigest.getInstance("MD5"));

    byte[] buffer = new byte[8192];
    while( dis.read( buffer ) > 0 );

    String md5Content = new String(
        org.apache.commons.codec.binary.Base64.encodeBase64(
            dis.getMessageDigest().digest()) );

    // Effectively resets the stream to be beginning of the file
    // via a FileChannel.
    fis.getChannel().position(0);

    return md5Content;
  }
}
