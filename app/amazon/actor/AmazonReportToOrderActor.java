package amazon.actor;

import java.net.InetAddress;
import java.util.List;
import java.util.Map;

import akka.actor.UntypedActor;
import amazon.models.order.AmazonOrderSchedule;
import amazon.models.report.AmazonReportOrderSnapshot;
import amazon.mws.order.AmazonGetOrder;
import amazon.util.APLogger;
import amazon.util.GlobalKit;

import com.avaje.ebean.Ebean;

/**
 * Title: AmazonReportToOrderActor.java
 * <br/>
 * actor max eq 3 / hour, account max eq 1(* 50).
 *
 * @author Lc
 *
 * @date 2016年5月9日 下午1:55:46
 */
public class AmazonReportToOrderActor extends UntypedActor {

	@Override
	public void onReceive(Object obj) throws Exception {
		try {
			APLogger.info("actor.AmazonReportToOrderActor >>> start");
			
			List<AmazonOrderSchedule> aosList = Ebean.find(AmazonOrderSchedule.class).where().eq("ipAddress", InetAddress.getLocalHost().getHostAddress()).findList();
			if (aosList != null && aosList.size() != 0) {
				for (AmazonOrderSchedule aos : aosList) {
					handler(aos);
				}
			} else {
				APLogger.warn("actor.AmazonReportToOrderActor - List<AmazonTaskConfig> == 0");
			}
			
			APLogger.info("actor.AmazonReportToOrderActor >>> end");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void handler(AmazonOrderSchedule aos) {
		try {
			Map<String, String> map = GlobalKit.authMap(aos.config);
		    
			List<AmazonReportOrderSnapshot> arosList = GlobalKit.queryOfRawSql(AmazonReportOrderSnapshot.class, "SELECT t1.id, t1.amazon_order_id FROM amazon_report_order_snapshot t1 JOIN amazon_report t2 ON (t1.report_id = t2.id) LEFT JOIN amazon_order t3 ON (t1.amazon_order_id = t3.amazon_order_id) WHERE t2.app_name = :app_name AND t3.amazon_order_id IS NULL GROUP BY t1.amazon_order_id LIMIT 50").setParameter("app_name", map.get("appName")).findList();
			if (arosList != null && arosList.size() != 0) {
				new AmazonGetOrder().start(map, arosList);
			}
		} catch (Exception e) {
			APLogger.error("actor.AmazonReportToOrderActor.handler()", e);
		}
	}

}
