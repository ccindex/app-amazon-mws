package amazon.mws.report;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import amazon.models.report.AmazonReport;
import amazon.util.APLogger;
import amazon.util.GlobalKit;

import com.amazonaws.mws.MarketplaceWebServiceException;
import com.amazonaws.mws.model.GetReportRequestListByNextTokenRequest;
import com.amazonaws.mws.model.GetReportRequestListByNextTokenResponse;
import com.amazonaws.mws.model.GetReportRequestListByNextTokenResult;
import com.amazonaws.mws.model.GetReportRequestListRequest;
import com.amazonaws.mws.model.GetReportRequestListResponse;
import com.amazonaws.mws.model.GetReportRequestListResult;
import com.amazonaws.mws.model.ReportRequestInfo;
import com.amazonaws.mws.model.TypeList;
import com.avaje.ebean.Ebean;

/**
 * Title: AmazonGetReportRequestList.java
 *
 * @author Lc
 *
 * @date 2016年4月20日 下午4:19:06
 */
public class AmazonGetReportRequestList extends AmazonReportBase {

	private GetReportRequestListRequest request;
	private GetReportRequestListByNextTokenRequest requestByNextToken;
	
	private boolean hasNext = false;
	private String nextToken = null;

	@Override
	public void start(Object ... obj) throws Exception {
		init(obj);
		
		invokeGetReportRequestList();
	}
	
	protected void init(Object ... obj) throws Exception {
		Map<String, String> map = (Map<String, String>) obj[0];
		super.conf(map);
		
		request = new GetReportRequestListRequest();
		request.setMerchant(sellerId);
		request.setMaxCount(GET_REPORT_REQUEST_LIST_MAX_COUNT);

		List<String> list = new ArrayList<String>();
    	list.add(TYPE_UPDATE_ORDER);
    	TypeList typeList = new TypeList(list);
    	request.setReportTypeList(typeList);

    	request.setRequestedFromDate(GlobalKit.convertToXMLGregorianCalendar( map.get("fromTime") ));
	}
	
	/**
     * Get Report Request List  request
     * returns a list of report requests ids and their associated metadata
     *   
     * <br/>
     * Tips: GetReportRequestList 操作的最大请求限额为 10 个，恢复速率为每 45 秒 1 个请求。
     */
    private void invokeGetReportRequestList() throws Exception {
        try {
            GetReportRequestListResponse response = client.getReportRequestList(request);

            if (response.isSetGetReportRequestListResult()) {
                GetReportRequestListResult  getReportRequestListResult = response.getGetReportRequestListResult();
                
                if (getReportRequestListResult.isSetHasNext()) {
                    hasNext = getReportRequestListResult.isHasNext();
                }
                if (getReportRequestListResult.isSetNextToken()) {
                	nextToken = getReportRequestListResult.getNextToken();
                }
                
                java.util.List<ReportRequestInfo> reportRequestInfoList = getReportRequestListResult.getReportRequestInfoList();
                
                reportRequestInfoListHandler(reportRequestInfoList);
            }

            nextReportRequestList();
        } catch (MarketplaceWebServiceException ex) {
        	StringBuilder err = new StringBuilder();
        	err.append("AmazonGetReportRequestList.invokeGetReportRequestList()").append(GlobalKit.newline());
        	err.append("Service Exception:").append(GlobalKit.newline());
        	err.append("Caught Exception: " + ex.getMessage()).append(GlobalKit.newline());
        	err.append("Response Status Code: " + ex.getStatusCode()).append(GlobalKit.newline());
        	err.append("Error Code: " + ex.getErrorCode()).append(GlobalKit.newline());
        	err.append("Error Type: " + ex.getErrorType()).append(GlobalKit.newline());
        	err.append("Request ID: " + ex.getRequestId()).append(GlobalKit.newline());
        	err.append("XML: " + ex.getXML()).append(GlobalKit.newline());
        	err.append("ResponseHeaderMetadata: " + ex.getResponseHeaderMetadata()).append(GlobalKit.newline());
        	APLogger.error(err.toString());
        } catch (Exception e) {
			APLogger.error("AmazonGetReportRequestList.invokeGetReportRequestList()", e);
			
			throw e;
		} finally {
			reset();
		}
    }

	private void reset() {
		hasNext = false;
		nextToken = null;
	}
    
    private void setGetReportRequestListByNextTokenRequest() throws Exception {
		if (requestByNextToken == null) {
			requestByNextToken = new GetReportRequestListByNextTokenRequest();
			requestByNextToken.setMerchant(super.sellerId);
		}
		requestByNextToken.setNextToken(nextToken);
	}
	
	private void nextReportRequestList() throws Exception {
		if (hasNext && nextToken != null) {
			Thread.sleep(SLEEP_GET_REPORT_REQUEST_LIST_BY_NEXT_TOKEN);

			setGetReportRequestListByNextTokenRequest();
			
			invokeGetReportRequestListByNextToken(requestByNextToken);
		}
	}
    
    /**
     * Get Report Request List By Next Token  request
     * retrieve the next batch of list items and if there are more items to retrieve
     *   
     * <br/>
     * Tips: GetReportRequestListByNextToken 操作的最大请求限额为 30 个，恢复速率为每 2 秒 1 个请求。
     *   
     * @param request Action to invoke
     */
    private void invokeGetReportRequestListByNextToken(GetReportRequestListByNextTokenRequest request) throws Exception {
        try {
            GetReportRequestListByNextTokenResponse response = client.getReportRequestListByNextToken(request);
            if (response.isSetGetReportRequestListByNextTokenResult()) {
                GetReportRequestListByNextTokenResult  getReportRequestListByNextTokenResult = response.getGetReportRequestListByNextTokenResult();
               
                if (getReportRequestListByNextTokenResult.isSetHasNext()) {
                    hasNext = getReportRequestListByNextTokenResult.isHasNext();
                }
                if (getReportRequestListByNextTokenResult.isSetNextToken()) {
                	nextToken = getReportRequestListByNextTokenResult.getNextToken();
                }
                
                java.util.List<ReportRequestInfo> reportRequestInfoList = getReportRequestListByNextTokenResult.getReportRequestInfoList();

                reportRequestInfoListHandler(reportRequestInfoList);
            }

            nextReportRequestList();
        } catch (MarketplaceWebServiceException ex) {
        	StringBuilder err = new StringBuilder();
        	err.append("AmazonGetReportRequestList.invokeGetReportRequestListByNextToken()").append(GlobalKit.newline());
        	err.append("Service Exception:").append(GlobalKit.newline());
        	err.append("Caught Exception: " + ex.getMessage()).append(GlobalKit.newline());
        	err.append("Response Status Code: " + ex.getStatusCode()).append(GlobalKit.newline());
        	err.append("Error Code: " + ex.getErrorCode()).append(GlobalKit.newline());
        	err.append("Error Type: " + ex.getErrorType()).append(GlobalKit.newline());
        	err.append("Request ID: " + ex.getRequestId()).append(GlobalKit.newline());
        	err.append("XML: " + ex.getXML()).append(GlobalKit.newline());
        	err.append("ResponseHeaderMetadata: " + ex.getResponseHeaderMetadata()).append(GlobalKit.newline());
        	APLogger.error(err.toString());
        } catch (Exception e) {
        	APLogger.error("AmazonGetReportRequestList.invokeGetReportRequestListByNextToken()", e);
        	
        	throw e;
		} finally {
			reset();
		}
    }
    
    /**
	 * Report Request Info List Handler
	 * 
	 * @param reportRequestInfoList
	 * @throws Exception
	 */
	private void reportRequestInfoListHandler(java.util.List<ReportRequestInfo> reportRequestInfoList) throws Exception {
		if (reportRequestInfoList == null) {
			return;
		}
		
		List<AmazonReport> list = new ArrayList<AmazonReport>();
		
		for (ReportRequestInfo reportRequestInfo : reportRequestInfoList) {
			AmazonReport info = null;
			
            if (reportRequestInfo.isSetReportRequestId()) {
                if (Ebean.find(AmazonReport.class).select("id").where().eq("reportRequestId", reportRequestInfo.getReportRequestId()).findUnique() != null) {
                	continue;
                }
                
                info = new AmazonReport();
                info.appName = super.appName;
                info.url = super.serviceURL;
                info.reportRequestId = reportRequestInfo.getReportRequestId();
            }
            if (reportRequestInfo.isSetReportType()) {
                info.reportType = reportRequestInfo.getReportType();
            }
            if (reportRequestInfo.isSetStartDate()) {
                info.startDate = reportRequestInfo.getStartDate().toString();
            }
            if (reportRequestInfo.isSetEndDate()) {
                info.endDate = reportRequestInfo.getEndDate().toString();
            }
            if (reportRequestInfo.isSetSubmittedDate()) {
                info.submittedDate = reportRequestInfo.getSubmittedDate().toString();
            }
            if (reportRequestInfo.isSetCompletedDate()) {
                info.completedDate = reportRequestInfo.getCompletedDate().toString();
            }                    
            if (reportRequestInfo.isSetReportProcessingStatus()) {
                info.reportProcessingStatus = reportRequestInfo.getReportProcessingStatus();
            }
            if(reportRequestInfo.isSetStartedProcessingDate()) {
                info.startedProcessingDate = reportRequestInfo.getStartedProcessingDate().toString();
            }
            if(reportRequestInfo.isSetGeneratedReportId()) {
                info.generatedReportId = reportRequestInfo.getGeneratedReportId();
            }
            
            list.add(info);
        }
		
		if (list.size() > 0) {
			Ebean.save(list);
		}
	}

}
