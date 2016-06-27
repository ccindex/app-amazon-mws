package amazon.mws.report;

import java.util.Map;

import play.Play;
import amazon.util.GlobalKit;

import com.amazonaws.mws.MarketplaceWebServiceClient;

/**
 * Title: AmazonReportBase.java
 *
 * @author Lc
 *
 * @date 2016年4月20日 下午4:10:20
 */
public abstract class AmazonReportBase {
	
	protected static final String TMP_REPORT_PATH = Play.application().configuration().getString("s3.file.path");
	
	// ----------------
	
	public abstract void start(Object ... obj) throws Exception;
	
	protected abstract void init(Object ... obj) throws Exception;
	
	protected String getOrderPath() {
		return TMP_REPORT_PATH + appName;
	}
	
	protected String getReportFilePath(String reportId) {
		return getOrderPath() + "/" + appName + "=" + reportId + "=" + REPORT_TYPE + "=" + GlobalKit.genReportNum();
	}

	// ----------------------------------------------------------------------------

	protected static final String TYPE_ORDER = "_GET_FLAT_FILE_ORDERS_DATA_";
	protected static final String TYPE_UPDATE_ORDER = "_GET_FLAT_FILE_ALL_ORDERS_DATA_BY_LAST_UPDATE_";
	
	protected static final int SLEEP_GET_REPORT_REQUEST_LIST_BY_NEXT_TOKEN = 2 * 1000;
	protected static final int SLEEP_GET_REPORT = 60 * 1000;

	protected static final int GET_REPORT_REQUEST_LIST_MAX_COUNT = 100;
	
	protected String REPORT_TYPE = TYPE_UPDATE_ORDER;

	// ----------------------------------------------------------------------------
	
    protected String accessKey;
    protected String secretKey;
    protected String appName;
    protected String appVersion;
    protected String serviceURL;
    protected String mwsAuthToken;
    
    protected String sellerId; // merchantId
    protected String marketplaceId;

	// ----------------------------------------------------------------------------
	
	protected MarketplaceWebServiceClient client;

	protected void conf(Map<String, String> map) {
		accessKey = map.get("accessKey");
		secretKey = map.get("secretKey");
		appName = map.get("appName");
		appVersion = map.get("appVersion");
		serviceURL = map.get("serviceURL");
		mwsAuthToken = map.get("mwsAuthToken");
		sellerId = map.get("merchantId");
		marketplaceId = map.get("marketplaceId");
		
		client = new AMWSReportService().getClient(accessKey, secretKey, appName, appVersion, serviceURL);
	}
	
}
