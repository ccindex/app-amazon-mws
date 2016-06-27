package amazon.mws.order;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import amazon.models.report.AmazonReportOrderSnapshot;
import amazon.util.APLogger;
import amazon.util.GlobalKit;
import amazon.util.OrderKit;

import com.amazonservices.mws.orders._2013_09_01.MarketplaceWebServiceOrdersException;
import com.amazonservices.mws.orders._2013_09_01.model.GetOrderRequest;
import com.amazonservices.mws.orders._2013_09_01.model.GetOrderResponse;
import com.amazonservices.mws.orders._2013_09_01.model.ResponseHeaderMetadata;

/**
 * Title: AmazonGetOrder.java
 *
 * @author Lc
 *
 * @date 2016年5月9日 上午10:03:12
 */
public class AmazonGetOrder extends AmazonOrderBase {
	
	private GetOrderRequest request;

	@Override
	public void start(Object... obj) throws Exception {
		APLogger.info("AmazonGetOrder.start() >>> start");
		
		init(obj);
		
		invokeGetOrder();
		
		APLogger.info("AmazonGetOrder.start() >>> end");
	}

	@Override
	public void init(Object... obj) throws Exception {
		Map<String, String> map = (Map<String, String>) obj[0];
		super.conf(map);
		
		request = new GetOrderRequest();
        request.setSellerId(sellerId);

        List<String> amazonOrderId = new ArrayList<String>();
        List<AmazonReportOrderSnapshot> arosList = (List<AmazonReportOrderSnapshot>) obj[1];
        for (int i=0; i<arosList.size(); i++) {
        	amazonOrderId.add(arosList.get(i).amazonOrderId);
        }
        request.setAmazonOrderId(amazonOrderId);
	}
	
    /**
     * Call the service, log response and exceptions.
     *
     * <br/>
     * Tips: 该 GetOrder 操作的最大请求限额为 6 个，恢复速率为每分钟 1 个请求。
     */
    public void invokeGetOrder() {
        try {
            // Call the service.
            GetOrderResponse response = client.getOrder(request);

            ordersHandler(response);
        } catch (MarketplaceWebServiceOrdersException ex) {
        	StringBuilder err = new StringBuilder();
        	err.append("AmazonGetOrder.invokeGetOrder()").append(GlobalKit.newline());
        	err.append("Service Exception:").append(GlobalKit.newline());
            ResponseHeaderMetadata rhmd = ex.getResponseHeaderMetadata();
            if(rhmd != null) {
            	err.append("RequestId: "+rhmd.getRequestId()).append(GlobalKit.newline());
            	err.append("Timestamp: "+rhmd.getTimestamp()).append(GlobalKit.newline());
            }
            err.append("Message: "+ex.getMessage()).append(GlobalKit.newline());
            err.append("StatusCode: "+ex.getStatusCode()).append(GlobalKit.newline());
            err.append("ErrorCode: "+ex.getErrorCode()).append(GlobalKit.newline());
            err.append("ErrorType: "+ex.getErrorType()).append(GlobalKit.newline());
            APLogger.error(err.toString());
        } catch (Exception e) {
        	APLogger.error("AmazonGetOrder.invokeGetOrder()", e);
        	
        	throw e;
		}
    }
    
    private void ordersHandler(GetOrderResponse response) {
    	if (response == null) {
    		APLogger.error("AmazonGetOrder.ordersHandler() - response == null");
    		return;
    	}
    	OrderKit.toOrder(response.toXML(), appName);
    }
	
}
