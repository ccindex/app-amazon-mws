package amazon.actor;

import java.net.InetAddress;
import java.util.List;
import java.util.Map;

import com.avaje.ebean.Ebean;

import akka.actor.UntypedActor;
import amazon.models.order.AmazonOrderSchedule;
import amazon.models.report.AmazonReport;
import amazon.models.report.AmazonReportSchedule;
import amazon.util.APLogger;
import amazon.util.GlobalKit;
import amazon.util.OrderKit;

/**
 * Title: AmazonReportSnapshotActor.java
 * <br/>
 * actor max eq 1000.
 *
 * @author Lc
 *
 * @date 2016年5月7日 下午3:50:42
 */
public class AmazonReportSnapshotActor extends UntypedActor {

	@Override
	public void onReceive(Object arg0) throws Exception {
		try {
			APLogger.info("actor.AmazonReportSnapshotActor >>> start");
			
			List<AmazonReportSchedule> arsList = Ebean.find(AmazonReportSchedule.class).select("config").where().eq("ipAddress", InetAddress.getLocalHost().getHostAddress()).findList();
			if (arsList != null && arsList.size() > 0) {
				for (AmazonReportSchedule ars : arsList) {
					Map<String, String> map = GlobalKit.authMap(ars.config);
					List<AmazonReport> arlist = Ebean.find(AmazonReport.class).where().eq("appName", map.get("appName")).eq("status", AmazonReport.Status.订单文件).setMaxRows(1000).findList();
					if (arlist != null && arlist.size() > 0) {
						for (AmazonReport report : arlist) {
							OrderKit.snapshot(report);
						}
					}
				}
			}
			
			APLogger.info("actor.AmazonReportSnapshotActor >>> end");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
