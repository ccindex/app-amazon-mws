package amazon.actor;

import java.net.InetAddress;

import akka.actor.UntypedActor;
import amazon.models.order.AmazonOrderSchedule;
import amazon.util.APLogger;
import amazon.util.OrderKit;

import com.avaje.ebean.Ebean;

/**
 * Title: AmazonPushOrderActor.java
 * <br/>
 * actor max eq 1. order max eq 800. item max eq 1000.
 *
 * @author Lc
 *
 * @date 2016年5月16日 下午1:19:38
 */
public class AmazonPushOrderActor extends UntypedActor {

	@Override
	public void onReceive(Object arg0) throws Exception {
		try {
			APLogger.info("actor.AmazonPushOrderActor >>> start");
			
			// TODO 暂时由47.90.7.213推送订单，后续考虑采用一台服务器单独处理这类任务 
			AmazonOrderSchedule aos = Ebean.find(AmazonOrderSchedule.class).select("ipAddress").where().eq("ipAddress", "10.24.181.186").setMaxRows(1).findUnique();
			if (InetAddress.getLocalHost().getHostAddress().equals(aos.ipAddress)) {
				OrderKit.pushToERP(); // 推送订单只由一台AP处理即可，从而避免事务处理可能引起的效率及异常问题
			}
			
			APLogger.info("actor.AmazonPushOrderActor >>> end");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
