package amazon.actor;

import java.net.InetAddress;
import java.util.List;
import java.util.Map;

import akka.actor.UntypedActor;
import amazon.enums.order.ScheduleStatus;
import amazon.models.report.AmazonReportSchedule;
import amazon.mws.report.AmazonGetReport;
import amazon.mws.report.AmazonGetReportRequestList;
import amazon.util.APLogger;
import amazon.util.GlobalKit;

import com.avaje.ebean.Ebean;

/**
 * Title: AmazonReportActor.java
 *
 * @author Lc
 *
 * @date 2016年5月5日 上午10:53:45
 */
public class AmazonReportActor extends UntypedActor {

	@Override
	public void onReceive(Object arg0) throws Exception {
		try {
			APLogger.info("actor.AmazonReportActor >>> start");
			
			List<AmazonReportSchedule> arsList = Ebean.find(AmazonReportSchedule.class).where().eq("ipAddress", InetAddress.getLocalHost().getHostAddress()).ne("status", ScheduleStatus.GREEN).findList();
			if (arsList != null && arsList.size() == 0) {
				arsList = Ebean.find(AmazonReportSchedule.class).where().eq("ipAddress", InetAddress.getLocalHost().getHostAddress()).findList();
				for (AmazonReportSchedule ars : arsList) {
					handler(ars);
				}
			} else {
				APLogger.warn("actor.AmazonReportActor - status: YELLOW");
			}
			
			APLogger.info("actor.AmazonReportActor >>> end");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void handler(AmazonReportSchedule ars) {
		try {
			String toTime = GlobalKit.getCurrentTime();
			
			Map<String, String> map = GlobalKit.authMap(ars.config);
			map.put("fromTime", GlobalKit.dateFormat.format(ars.lastTime));
			map.put("toTime", toTime);

			ars.status = ScheduleStatus.YELLOW;
			Ebean.update(ars);
		    
		    new AmazonGetReportRequestList().start(map);
		    
		    new AmazonGetReport().start(map);
		    
		    ars.lastTime = GlobalKit.dateFormat.parse(toTime);
		} catch (Exception e) {
			APLogger.error("actor.AmazonReportActor.handler()", e);
		} finally {
			ars.status = ScheduleStatus.GREEN;
			Ebean.update(ars);
		}
	}

}
