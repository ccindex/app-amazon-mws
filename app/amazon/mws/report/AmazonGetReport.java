package amazon.mws.report;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import amazon.models.report.AmazonReport;
import amazon.models.report.AmazonReport.Status;
import amazon.util.APLogger;
import amazon.util.GlobalKit;

import com.amazonaws.mws.MarketplaceWebServiceException;
import com.amazonaws.mws.model.GetReportRequest;
import com.amazonaws.mws.model.GetReportResponse;
import com.avaje.ebean.Ebean;

/**
 * Title: AmazonGetReport.java
 *
 * @author Lc
 *
 * @date 2016年4月22日 上午9:57:57
 */
public class AmazonGetReport extends AmazonReportBase {

	private GetReportRequest request;
	
	@Override
	public void start(Object ... obj) throws Exception {
		init(obj);
		
		Map<String, String> map = (Map<String, String>) obj[0];
		List<AmazonReport> list = Ebean.find(AmazonReport.class)
										.where()
										.eq("status", AmazonReport.Status.报告记录)
										.eq("reportProcessingStatus", "_DONE_")
										.eq("reportType", TYPE_UPDATE_ORDER)
										.eq("appName", map.get("appName"))
										.isNotNull("generatedReportId")
										.setMaxRows(100)
										.findList();
		if (list == null) {
			APLogger.warn("AmazonGetReport.start() - List<AmazonReport> list == 0");
			return;
		}
		
		AmazonReport reportInfo = null;
		for (int i=0; i<list.size(); i++) {
			try {
				Thread.sleep(SLEEP_GET_REPORT);
				
				reportInfo = list.get(i);
				
				String filepath = receiveReport(reportInfo);
				
				String md5Checksum = invokeGetReport();
				
				if (md5Checksum != null && md5Checksum.equals( GlobalKit.computeContentMD5Header(new FileInputStream(filepath)) )) {
					reportInfo.status = Status.订单文件;
					reportInfo.path = filepath;
					reportInfo.md5Checksum = md5Checksum;
					Ebean.update(reportInfo);
				} else {
					APLogger.warn("Local file MD5Checksum invalid, reportInfo.reportRequestId: " + reportInfo.reportRequestId);
				}
			} catch (Exception e) {
				APLogger.error("AmazonGetReport.start()", e);
			}
		}
	}
	
	protected void init(Object ... obj) throws Exception {
		Map<String, String> map = (Map<String, String>) obj[0];
		super.conf(map);
		
		// 创建文件目录
		File file = new File( getOrderPath() );
		if (file != null && !file.exists()) {
			APLogger.info("created file directory.");
			
			file.mkdirs();
		}
		
		request = new GetReportRequest();
		request.setMerchant(sellerId);
	}
	
	/**
	 * 生成本地报告文件
	 * 
	 * @return
	 * @throws IOException
	 */
	private String receiveReport(AmazonReport reportInfo) throws Exception {
		request.setReportId(reportInfo.generatedReportId);
		
		REPORT_TYPE = reportInfo.reportType;
		
		String filepath = getReportFilePath(reportInfo.generatedReportId);
		OutputStream report = new FileOutputStream(filepath);
		request.setReportOutputStream(report);
		
		return filepath;
	}
	
	
	/**
     * Get Report  request
     * The GetReport operation returns the contents of a report. Reports can potentially be
     * very large (>100MB) which is why we only return one report at a time, and in a
     * streaming fashion.
     * 
     * <br/>
     * Tips: GetReport 操作的最大请求限额为 15 个，恢复速率为每分钟 1 个请求。
     *   
     * @return md5Checksum
     */
    private String invokeGetReport() throws Exception {
    	GetReportResponse response = null;
        try {
        	response = client.getReport(request);
        } catch (MarketplaceWebServiceException ex) {
        	StringBuilder err = new StringBuilder();
        	err.append("AmazonGetReport.invokeGetReport()").append(GlobalKit.newline());
        	err.append("Service Exception:").append(GlobalKit.newline());
        	
        	err.append("Caught Exception: " + ex.getMessage()).append(GlobalKit.newline());
        	err.append("Response Status Code: " + ex.getStatusCode()).append(GlobalKit.newline());
        	err.append("Error Code: " + ex.getErrorCode()).append(GlobalKit.newline());
        	err.append("Error Type: " + ex.getErrorType()).append(GlobalKit.newline());
        	err.append("Request ID: " + ex.getRequestId()).append(GlobalKit.newline());
        	err.append("XML: " + ex.getXML()).append(GlobalKit.newline());
        	err.append("ResponseHeaderMetadata: " + ex.getResponseHeaderMetadata()).append(GlobalKit.newline());
        	APLogger.error(err.toString());
        	
        	throw ex;
        }
        
        return response.getGetReportResult().getMD5Checksum();
    }

}
