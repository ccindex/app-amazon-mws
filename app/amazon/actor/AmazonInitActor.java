package amazon.actor;

import java.net.InetAddress;
import java.util.List;

import akka.actor.UntypedActor;
import amazon.enums.order.ScheduleStatus;
import amazon.models.order.AmazonOrderSchedule;
import amazon.models.report.AmazonReportSchedule;
import amazon.util.APLogger;

import com.avaje.ebean.Ebean;

/**
 * Title: AmazonInitActor.java
 *
 * @author Lc
 *
 * @date 2016年5月18日 下午1:57:38
 */
public class AmazonInitActor extends UntypedActor {

	@Override
	public void onReceive(Object arg0) throws Exception {
		try {
			APLogger.info("actor.AmazonInitActor >>> start");
			
			List<AmazonReportSchedule> arsList = Ebean.find(AmazonReportSchedule.class).where().eq("ipAddress", InetAddress.getLocalHost().getHostAddress()).findList();
			for (AmazonReportSchedule ars : arsList) {
				ars.status = ScheduleStatus.GREEN;
				Ebean.update(ars);
			}
			List<AmazonOrderSchedule> aosList = Ebean.find(AmazonOrderSchedule.class).where().eq("ipAddress", InetAddress.getLocalHost().getHostAddress()).findList();
			for (AmazonOrderSchedule aos : aosList) {
				aos.status = ScheduleStatus.GREEN;
				Ebean.update(aos);
			}
			
			APLogger.info("actor.AmazonInitActor >>> end");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
