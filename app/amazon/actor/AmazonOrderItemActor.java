package amazon.actor;

import java.net.InetAddress;
import java.util.List;
import java.util.Map;

import akka.actor.UntypedActor;
import amazon.models.order.AmazonOrderSchedule;
import amazon.mws.order.AmazonListOrderItems;
import amazon.util.APLogger;
import amazon.util.GlobalKit;

import com.avaje.ebean.Ebean;

/**
 * Title: AmazonOrderItemActor.java
 * <br/>
 * actor max eq 3600, account max eq 600.
 *
 * @author Lc
 *
 * @date 2016年5月19日 上午11:51:54
 */
public class AmazonOrderItemActor extends UntypedActor {

	@Override
	public void onReceive(Object arg0) throws Exception {
		try {
			APLogger.info("actor.AmazonOrderItemActor >>> start");
			
			List<AmazonOrderSchedule> aosList = Ebean.find(AmazonOrderSchedule.class).where().eq("ipAddress", InetAddress.getLocalHost().getHostAddress()).findList();
			if (aosList != null && aosList.size() > 0) {
				for (AmazonOrderSchedule aos : aosList) {
					handler(aos);
				}
			} else {
				APLogger.warn("actor.AmazonOrderItemActor - List<AmazonTaskConfig> == 0");
			}
			
			APLogger.info("actor.AmazonOrderItemActor >>> end");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void handler(AmazonOrderSchedule aos) {
		try {
			Map<String, String> map = GlobalKit.authMap(aos.config);
		    
		    new AmazonListOrderItems().start(map);
		} catch (Exception e) {
			APLogger.error("actor.AmazonOrderItemActor.handler()", e);
		}
	}

}
