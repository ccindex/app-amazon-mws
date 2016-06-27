package amazon.mws.order;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.datatype.XMLGregorianCalendar;

import amazon.util.APLogger;
import amazon.util.GlobalKit;
import amazon.util.OrderKit;

import com.amazonservices.mws.orders._2013_09_01.MarketplaceWebServiceOrdersException;
import com.amazonservices.mws.orders._2013_09_01.model.ListOrdersByNextTokenRequest;
import com.amazonservices.mws.orders._2013_09_01.model.ListOrdersByNextTokenResponse;
import com.amazonservices.mws.orders._2013_09_01.model.ListOrdersRequest;
import com.amazonservices.mws.orders._2013_09_01.model.ListOrdersResponse;
import com.amazonservices.mws.orders._2013_09_01.model.ResponseHeaderMetadata;

/**
 * Title: 获取指定时间段内所创建或更新的订单
 *
 * @author Lc
 *
 * @date 2016年4月26日 下午6:50:49
 */
public class AmazonListOrders extends AmazonOrderBase {

	private ListOrdersRequest request;
	
	private ListOrdersByNextTokenRequest requestByNextToken;
	
	private String nextToken = null;

	@Override
	public void start(Object... obj) throws Exception {
		init(obj);
		
		invokeListOrders();
	}

	@Override
	public void init(Object... obj) throws Exception {
		Map<String, String> map = (Map<String, String>) obj[0];
		super.conf(map);
	
		List<String> marketplaceId = new ArrayList<String>();
		marketplaceId.add( map.get("marketplaceId") );
		
		XMLGregorianCalendar fromTime = GlobalKit.convertToXMLGregorianCalendar( map.get("fromTime") );
		XMLGregorianCalendar toTime = GlobalKit.convertToXMLGregorianCalendar( map.get("toTime") );
		
		request = new ListOrdersRequest();
        request.setSellerId(sellerId);
        request.setLastUpdatedAfter(fromTime);
        request.setLastUpdatedBefore(toTime);
        request.setMarketplaceId(marketplaceId);
        request.setMaxResultsPerPage(MAX_RESULTS_PER_PAGE);
	}
	
    /**
     * Call the service, log response and exceptions.
     * 
     * <br/>
     * Tips: ListOrders 操作的最大请求限额为 6 个，恢复速率为每分钟 1 个请求。
     */
    public void invokeListOrders() throws Exception {
        try {
        	Thread.sleep(LIST_ORDER_INTERVAL_TIME);
        	
            // Call the service.
            ListOrdersResponse response = client.listOrders(request);
            
            listOrdersHandler(response);
            
            nextListOrders();
        } catch (MarketplaceWebServiceOrdersException ex) {
            StringBuilder err = new StringBuilder();
        	err.append("AmazonListOrders.invokeListOrders()").append(GlobalKit.newline());
        	err.append("Service Exception:").append(GlobalKit.newline());
            ResponseHeaderMetadata rhmd = ex.getResponseHeaderMetadata();
            if(rhmd != null) {
            	err.append("RequestId: " + rhmd.getRequestId()).append(GlobalKit.newline());
            	err.append("Timestamp: " + rhmd.getTimestamp()).append(GlobalKit.newline());
            }
            err.append("Message: " + ex.getMessage()).append(GlobalKit.newline());
            err.append("StatusCode: " + ex.getStatusCode()).append(GlobalKit.newline());
            err.append("ErrorCode: " + ex.getErrorCode()).append(GlobalKit.newline());
            err.append("ErrorType: " + ex.getErrorType()).append(GlobalKit.newline());
            APLogger.error(err.toString());
        } catch (Exception e) {
        	APLogger.error("AmazonListOrders.invokeListOrders()", e);
        	
        	throw e;
		}
    }
    
    /**
     * Call the service, log response and exceptions.
     *
     *	<br/>
     *	Tips: ListOrdersByNextToken 操作的最大请求限额为 6 个，恢复速率为每分钟 1 个请求。
     */
    public void invokeListOrdersByNextToken() throws Exception {
        try {
        	Thread.sleep(LIST_ORDER_INTERVAL_TIME);
        	
            // Call the service.
            ListOrdersByNextTokenResponse response = client.listOrdersByNextToken(requestByNextToken);
            
            listOrdersByNextTokenHandler(response);
            
            nextListOrders();
        } catch (MarketplaceWebServiceOrdersException ex) {
        	StringBuilder err = new StringBuilder();
        	err.append("AmazonListOrders.invokeListOrdersByNextToken()").append(GlobalKit.newline());
        	err.append("Service Exception:").append(GlobalKit.newline());
            ResponseHeaderMetadata rhmd = ex.getResponseHeaderMetadata();
            if(rhmd != null) {
            	err.append("RequestId: " + rhmd.getRequestId()).append(GlobalKit.newline());
            	err.append("Timestamp: " + rhmd.getTimestamp()).append(GlobalKit.newline());
            }
            err.append("Message: " + ex.getMessage()).append(GlobalKit.newline());
            err.append("StatusCode: " + ex.getStatusCode()).append(GlobalKit.newline());
            err.append("ErrorCode: " + ex.getErrorCode()).append(GlobalKit.newline());
            err.append("ErrorType: " + ex.getErrorType()).append(GlobalKit.newline());
            APLogger.error(err.toString());
        } catch (Exception e) {
        	APLogger.error("AmazonListOrders.invokeListOrdersByNextToken()", e);
        	
        	throw e;
    	}
    }
    
    private void listOrdersHandler(ListOrdersResponse response) {
    	if (response == null) {
    		APLogger.error("AmazonListOrders.listOrdersHandler() - response == null");
    		return;
    	}
    	nextToken = OrderKit.toOrder(response.toXML(), appName);
    }
    
    private void listOrdersByNextTokenHandler(ListOrdersByNextTokenResponse response) {
    	if (response == null) {
    		APLogger.error("AmazonListOrders.listOrdersByNextTokenHandler() - response == null");
    		return;
    	}
    	nextToken = OrderKit.toOrder(response.toXML(), appName);
    }
    
    private void setListOrdersByNextTokenRequest() {
    	if (requestByNextToken == null) {
    		requestByNextToken = new ListOrdersByNextTokenRequest();
            requestByNextToken.setSellerId(sellerId);
            requestByNextToken.setMWSAuthToken(mwsAuthToken);
    	}
    	requestByNextToken.setNextToken(nextToken);
    }
    
    private void nextListOrders() throws Exception {
    	if (nextToken != null) {
    		setListOrdersByNextTokenRequest();
    		
    		invokeListOrdersByNextToken();
    	}
    }

}
