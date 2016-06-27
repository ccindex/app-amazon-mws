package amazon.mws.report;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import amazon.models.report.AmazonReport;
import amazon.util.APLogger;

import com.amazonaws.mws.MarketplaceWebServiceException;
import com.amazonaws.mws.model.GetReportListRequest;
import com.amazonaws.mws.model.GetReportListResponse;
import com.amazonaws.mws.model.GetReportListResult;
import com.amazonaws.mws.model.IdList;
import com.amazonaws.mws.model.ReportInfo;
import com.amazonaws.mws.model.ResponseMetadata;
import com.avaje.ebean.Ebean;

/**
 * Title: AmazonGetReportList.java
 *
 * @author Lc
 *
 * @date 2016年4月23日 下午3:22:14
 */
public class AmazonGetReportList extends AmazonReportBase {

	private GetReportListRequest request;
	
	@Override
	public void start(Object ... obj) throws Exception {
		APLogger.info("AmazonGetReportList.start() >>> start");

		init(obj);
		
		List<AmazonReport> list = Ebean.find(AmazonReport.class)
												.where()
												.eq("status", AmazonReport.Status.报告记录)
												.eq("reportProcessingStatus", "_DONE_")
												.isNotNull("generatedReportId")
												.eq("reportType", "_GET_FLAT_FILE_ALL_ORDERS_DATA_BY_LAST_UPDATE_")
												.findList();
		AmazonReport reportInfo = null;
		for (int i=0; i<list.size(); i++) {
			Thread.sleep(SLEEP_GET_REPORT);

			reportInfo = list.get(i);

			List<String> id = new ArrayList<String>();
	        id.add(reportInfo.reportRequestId);
	        IdList idList = new IdList();
	        idList.setId(id);
	        request.setReportRequestIdList(idList);
	        
			invokeGetReportList();
		}
		
		APLogger.info("AmazonGetReportList.start() >>> end");
	}

	@Override
	protected void init(Object ... obj) throws Exception {
		Map<String, String> map = (Map<String, String>) obj[0];
		super.conf(map);
		
		request = new GetReportListRequest();
        request.setMerchant(super.sellerId);
        request.setMaxCount(GET_REPORT_REQUEST_LIST_MAX_COUNT);
	}
    
	/**
     * Get Report List  request sample
     * returns a list of reports; by default the most recent ten reports,
     * regardless of their acknowledgement status
     * 
     * <br/>
     * Tips: GetReportList 操作的最大请求限额为 10 个，恢复速率为每分钟 1 个请求。
     */
    public void invokeGetReportList() {
        try {
            GetReportListResponse response = client.getReportList(request);

            System.out.println ("GetReportList Action Response");
            System.out.println ("=============================================================================");
            System.out.println ();

            System.out.print("    GetReportListResponse");
            System.out.println();
            if (response.isSetGetReportListResult()) {
                System.out.print("        GetReportListResult");
                System.out.println();
                GetReportListResult  getReportListResult = response.getGetReportListResult();
                if (getReportListResult.isSetNextToken()) {
                    System.out.print("            NextToken");
                    System.out.println();
                    System.out.print("                " + getReportListResult.getNextToken());
                    System.out.println();
                }
                if (getReportListResult.isSetHasNext()) {
                    System.out.print("            HasNext");
                    System.out.println();
                    System.out.print("                " + getReportListResult.isHasNext());
                    System.out.println();
                }
                java.util.List<ReportInfo> reportInfoListList = getReportListResult.getReportInfoList();
                for (ReportInfo reportInfoList : reportInfoListList) {
                    System.out.print("            ReportInfoList");
                    System.out.println();
                    if (reportInfoList.isSetReportId()) {
                        System.out.print("                ReportId");
                        System.out.println();
                        System.out.print("                    " + reportInfoList.getReportId());
                        System.out.println();
                    }
                    if (reportInfoList.isSetReportType()) {
                        System.out.print("                ReportType");
                        System.out.println();
                        System.out.print("                    " + reportInfoList.getReportType());
                        System.out.println();
                    }
                    if (reportInfoList.isSetReportRequestId()) {
                        System.out.print("                ReportRequestId");
                        System.out.println();
                        System.out.print("                    " + reportInfoList.getReportRequestId());
                        System.out.println();
                    }
                    if (reportInfoList.isSetAvailableDate()) {
                        System.out.print("                AvailableDate");
                        System.out.println();
                        System.out.print("                    " + reportInfoList.getAvailableDate());
                        System.out.println();
                    }
                    if (reportInfoList.isSetAcknowledged()) {
                        System.out.print("                Acknowledged");
                        System.out.println();
                        System.out.print("                    " + reportInfoList.isAcknowledged());
                        System.out.println();
                    }
                    if (reportInfoList.isSetAcknowledgedDate()) {
                        System.out.print("                AcknowledgedDate");
                        System.out.println();
                        System.out.print("                    " + reportInfoList.getAcknowledgedDate());
                        System.out.println();
                    }
                }
            } 
            if (response.isSetResponseMetadata()) {
                System.out.print("        ResponseMetadata");
                System.out.println();
                ResponseMetadata  responseMetadata = response.getResponseMetadata();
                if (responseMetadata.isSetRequestId()) {
                    System.out.print("            RequestId");
                    System.out.println();
                    System.out.print("                " + responseMetadata.getRequestId());
                    System.out.println();
                }
            } 
            System.out.println();
            System.out.println(response.getResponseHeaderMetadata());
            System.out.println();


        } catch (MarketplaceWebServiceException ex) {

            System.out.println("Caught Exception: " + ex.getMessage());
            System.out.println("Response Status Code: " + ex.getStatusCode());
            System.out.println("Error Code: " + ex.getErrorCode());
            System.out.println("Error Type: " + ex.getErrorType());
            System.out.println("Request ID: " + ex.getRequestId());
            System.out.print("XML: " + ex.getXML());
            System.out.println("ResponseHeaderMetadata: " + ex.getResponseHeaderMetadata());
        }
    }
	
}
