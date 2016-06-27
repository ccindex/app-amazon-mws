package amazon.actor;

import java.net.InetAddress;
import java.util.List;
import java.util.Map;

import akka.actor.UntypedActor;
import amazon.enums.order.ScheduleStatus;
import amazon.models.order.AmazonOrderSchedule;
import amazon.mws.order.AmazonListOrders;
import amazon.util.APLogger;
import amazon.util.GlobalKit;

import com.avaje.ebean.Ebean;

/**
 * Title: AmazonOrderActor.java
 *
 * @author Lc
 *
 * @date 2016年4月29日 下午5:14:09
 */
public class AmazonOrderActor extends UntypedActor {

	@Override
	public void onReceive(Object obj) throws Exception {
		try {
			APLogger.info("actor.AmazonOrderActor >>> start");
			
			List<AmazonOrderSchedule> aosList = Ebean.find(AmazonOrderSchedule.class).where().eq("ipAddress", InetAddress.getLocalHost().getHostAddress()).ne("status", ScheduleStatus.GREEN).findList();
			if (aosList != null && aosList.size() == 0) {
				aosList = Ebean.find(AmazonOrderSchedule.class).where().eq("ipAddress", InetAddress.getLocalHost().getHostAddress()).findList();
				for (AmazonOrderSchedule aos : aosList) {
					handler(aos);
				}
			} else {
				APLogger.warn("actor.AmazonOrderActor - status: YELLOW");
			}
			
			APLogger.info("actor.AmazonOrderActor >>> end");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void handler(AmazonOrderSchedule aos) throws Exception {
		try {
			String createdBefore = GlobalKit.getCreatedBefore();

			Map<String, String> map = GlobalKit.authMap(aos.config);
			map.put("fromTime", GlobalKit.dateFormat.format(aos.lastTime));
			map.put("toTime", createdBefore);
			
			aos.status = ScheduleStatus.YELLOW;
			Ebean.update(aos);
		    
		    new AmazonListOrders().start(map);
		    
		    aos.lastTime = GlobalKit.dateFormat.parse(createdBefore);
		} catch (Exception e) {
			APLogger.error("actor.AmazonOrderActor.handler()", e);
		} finally {
			aos.status = ScheduleStatus.GREEN;
			Ebean.update(aos);
		}
	}

}
