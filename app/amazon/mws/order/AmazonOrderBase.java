package amazon.mws.order;

import java.util.Map;

import com.amazonservices.mws.orders._2013_09_01.MarketplaceWebServiceOrdersClient;

/**
 * Title: AmazonOrderBase.java
 *
 * @author Lc
 *
 * @date 2016年4月26日 下午6:50:15
 */
public abstract class AmazonOrderBase {

	public static final int LIST_ORDER_INTERVAL_TIME = 60 * 1000;
	
	public static final int LIST_ORDER_ITEMS_INTERVAL_TIME = 2 * 1000;
	
	public static final int MAX_RESULTS_PER_PAGE = 100;
	
	// ----------------------------------------------------------------------------
	
    protected String accessKey;
    protected String secretKey;
    protected String appName;
    protected String appVersion;
    protected String sellerId;
    protected String marketplaceId;
    protected String serviceURL;
    protected String mwsAuthToken;
    protected String developerId;

	// ----------------------------------------------------------------------------
	
	protected MarketplaceWebServiceOrdersClient client;

	protected void conf(Map<String, String> map) {
		accessKey = map.get("accessKey");
		secretKey = map.get("secretKey");
		appName = map.get("appName");
		appVersion = map.get("appVersion");
		sellerId = map.get("merchantId");
		marketplaceId = map.get("marketplaceId");
		serviceURL = map.get("serviceURL");
		mwsAuthToken = map.get("mwsAuthToken");
		developerId = map.get("developerId");
		
		client = new AMWSOrdersService().getClient(accessKey, secretKey, appName, appVersion, serviceURL);
	}
	
	public abstract void start(Object ... obj) throws Exception;
	
	public abstract void init(Object ... obj) throws Exception;
	
}
