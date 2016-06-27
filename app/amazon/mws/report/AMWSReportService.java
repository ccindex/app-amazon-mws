package amazon.mws.report;

import com.amazonaws.mws.MarketplaceWebServiceClient;
import com.amazonaws.mws.MarketplaceWebServiceConfig;

/**
 * Title: AMWSReportService.java
 *
 * @author Lc
 *
 * @date 2016年5月5日 上午10:45:26
 */
public class AMWSReportService {

	private MarketplaceWebServiceClient client;
	
	public MarketplaceWebServiceClient getClient(String accessKey, String secretKey, String appName, String appVersion, String serviceURL) {
		MarketplaceWebServiceConfig config = new MarketplaceWebServiceConfig();
		config.setServiceURL(serviceURL);
		
		client = new MarketplaceWebServiceClient(accessKey, secretKey, appName, appVersion, config);
		
		return client;
	}
	
}
