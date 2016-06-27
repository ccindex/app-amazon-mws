package amazon.mws.order;

import com.amazonservices.mws.orders._2013_09_01.MarketplaceWebServiceOrdersClient;
import com.amazonservices.mws.orders._2013_09_01.MarketplaceWebServiceOrdersConfig;

/**
 * Title: AMWSOrdersService.java
 *
 * @author Lc
 *
 * @date 2016年5月5日 下午5:21:23
 */
public class AMWSOrdersService {

    private MarketplaceWebServiceOrdersClient client;

    public MarketplaceWebServiceOrdersClient getClient(String accessKey, String secretKey, String appName, String appVersion, String serviceURL) {
    	MarketplaceWebServiceOrdersConfig config = new MarketplaceWebServiceOrdersConfig();
        config.setServiceURL(serviceURL);
        
        // Set other client connection configurations here.
        client = new MarketplaceWebServiceOrdersClient(accessKey, secretKey, appName, appVersion, config);
        
        return client;
    }

}
