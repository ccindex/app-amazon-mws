package amazon.mws.order;

import java.util.List;
import java.util.Map;

import amazon.enums.order.OrderStatus;
import amazon.models.order.AmazonOrder;
import amazon.util.APLogger;
import amazon.util.GlobalKit;
import amazon.util.OrderKit;

import com.amazonservices.mws.orders._2013_09_01.MarketplaceWebServiceOrdersException;
import com.amazonservices.mws.orders._2013_09_01.model.ListOrderItemsByNextTokenRequest;
import com.amazonservices.mws.orders._2013_09_01.model.ListOrderItemsByNextTokenResponse;
import com.amazonservices.mws.orders._2013_09_01.model.ListOrderItemsRequest;
import com.amazonservices.mws.orders._2013_09_01.model.ListOrderItemsResponse;
import com.amazonservices.mws.orders._2013_09_01.model.ResponseHeaderMetadata;
import com.avaje.ebean.Ebean;

/**
 * Title: 根据指定的 AmazonOrderId 返回订单商品
 *
 * @author Lc
 *
 * @date 2016年4月28日 下午3:56:48
 */
public class AmazonListOrderItems extends AmazonOrderBase {

	private ListOrderItemsRequest request = null;
	
	private ListOrderItemsByNextTokenRequest requestByNextToken = null;

	private String nextToken = null;
	
	@Override
	public void start(Object... obj) throws Exception {
		init(obj);
		
		Map<String, String> map = (Map<String, String>) obj[0];
		
		List<AmazonOrder> aoList = null;
		int rows = 600;
		switch (map.get("appName")) {
		case "HS_US":
			rows = 1000;
			break;
		case "Ohuhu Direct":
			rows = 600;
			break;
		default:
			rows = 300;
			break;
		}
		aoList = Ebean.find(AmazonOrder.class).select("id, amazonOrderId").where().eq("appName", map.get("appName")).eq("status", OrderStatus.订单信息).orderBy().desc("id").setMaxRows(rows).findList();
		
		if (aoList != null && aoList.size() > 0) {
			for (AmazonOrder ao : aoList) {
				if (Ebean.find(AmazonOrder.class).where().eq("amazonOrderId", ao.amazonOrderId).eq("status", OrderStatus.商品信息).findList().size() > 0) {
					ao.status = OrderStatus.商品信息;
					Ebean.update(ao);
					continue;
				}
				
				request.setAmazonOrderId(ao.amazonOrderId);
				
				invokeListOrderItems(ao);
			}
		}
	}

	@Override
	public void init(Object... obj) throws Exception {
		Map<String, String> map = (Map<String, String>) obj[0];
		super.conf(map);
		
        request = new ListOrderItemsRequest();
        request.setSellerId(sellerId);
        request.setMWSAuthToken(mwsAuthToken);
	}
	
	/**
     * Call the service, log response and exceptions.
     * 
     * <br/>
     * Tips: 该 ListOrderItems 和 ListOrderItemsByNextToken 操作共享的最大请求限额为 30 个，恢复速率为每 2 秒钟 1 个请求。
     */
    public void invokeListOrderItems(AmazonOrder ao) throws Exception {
        try {
        	Thread.sleep(LIST_ORDER_ITEMS_INTERVAL_TIME);
        	
            // Call the service.
            ListOrderItemsResponse response = client.listOrderItems(request);
            
            listOrderItemsHandler(response, ao);
            
            nextListOrderItems(ao);
        } catch (MarketplaceWebServiceOrdersException ex) {
        	StringBuilder err = new StringBuilder();
        	err.append("AmazonListOrderItems.invokeListOrderItems()").append(GlobalKit.newline());
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
        	APLogger.error("AmazonListOrderItems.invokeListOrderItems()", e);
        	
        	throw e;
		}
    }

    /**
     * Call the service, log response and exceptions.
     *
     * <br/>
     * Tips: 该 ListOrderItems 和 ListOrderItemsByNextToken 操作共享的最大请求限额为 30 个，恢复速率为每 2 秒钟 1 个请求。
     */
    public void invokeListOrderItemsByNextToken(AmazonOrder ao) throws Exception {
        try {
        	Thread.sleep(LIST_ORDER_ITEMS_INTERVAL_TIME);
        	
            // Call the service.
            ListOrderItemsByNextTokenResponse response = client.listOrderItemsByNextToken(requestByNextToken);
            
            listOrderItemsByNextTokenHandler(response, ao);
            
            nextListOrderItems(ao);
        } catch (MarketplaceWebServiceOrdersException ex) {
        	StringBuilder err = new StringBuilder();
        	err.append("AmazonListOrderItems.invokeListOrderItemsByNextToken()").append(GlobalKit.newline());
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
        	APLogger.error("AmazonListOrderItems.invokeListOrderItemsByNextToken()", e);
        	
        	throw e;
		}
    }

    public void setListOrderItemsByNextToken() {
    	if (requestByNextToken == null) {
    		requestByNextToken = new ListOrderItemsByNextTokenRequest();
    		requestByNextToken.setSellerId(sellerId);
    		requestByNextToken.setMWSAuthToken(mwsAuthToken);
    	}
    	requestByNextToken.setNextToken(nextToken);
    }
    
    public void listOrderItemsHandler(ListOrderItemsResponse response, AmazonOrder ao) {
    	if (response == null) {
    		APLogger.error("AmazonListOrderItems.listOrderItemsHandler() - response == null");
    		return;
    	}
    	nextToken = OrderKit.toOrderItem(response.toXML(), ao);
    }
    
    public void listOrderItemsByNextTokenHandler(ListOrderItemsByNextTokenResponse response, AmazonOrder ao) {
    	if (response == null) {
    		APLogger.error("AmazonListOrderItems.listOrderItemsByNextTokenHandler() - response == null");
    		return;
    	}
    	nextToken = OrderKit.toOrderItem(response.toXML(), ao);
    }
    
    private void nextListOrderItems(AmazonOrder ao) throws Exception {
    	if (nextToken != null) {
    		setListOrderItemsByNextToken();
    		
    		invokeListOrderItemsByNextToken(ao);
    	}
    }

}
