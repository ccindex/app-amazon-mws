package misc;

import actors.CronOrderActor;
import actors.EbayBootUpActor;
import actors.EbaySendDataActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import amazon.actor.AmazonInitActor;
import amazon.actor.AmazonOrderActor;
import amazon.actor.AmazonOrderItemActor;
import amazon.actor.AmazonPushOrderActor;
import amazon.actor.AmazonReportActor;
import amazon.actor.AmazonReportSnapshotActor;
import amazon.actor.AmazonReportToOrderActor;
import play.Application;
import play.GlobalSettings;
import play.data.format.Formatters;
import play.libs.Akka;
import scala.concurrent.duration.FiniteDuration;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * 任务调度
 */
public class Global extends GlobalSettings {

  private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS");
  private static final SimpleDateFormat DATE_FORMAT_SIMPLE = new SimpleDateFormat("yyyy-MM-dd");

  @Override
  public void onStart(Application application) {
	  super.onStart(application);
	    
    Formatters.register(Date.class, new Formatters.SimpleFormatter<Date>() {
      @Override
      public String print(Date date, Locale locale) {
        return DATE_FORMAT.format(date);
      }

      @Override
      public Date parse(String s, Locale locale) throws ParseException {
        try {
          Long millis = Long.valueOf(s);
          return new Date(millis);
        } catch (NumberFormatException e) {
          try {
            return DATE_FORMAT.parse(s);
          } catch (ParseException ex) {
            return DATE_FORMAT_SIMPLE.parse(s);
          }
        }
      }
    });

    //ebay自动获取订单
    ActorRef cronOrderActor = Akka.system().actorOf(Props.create(CronOrderActor.class), "cron-order");
    Akka.system().scheduler().schedule(
            FiniteDuration.create(10, TimeUnit.MINUTES),
            FiniteDuration.create(20, TimeUnit.MINUTES),
            cronOrderActor,
            "cron-order",
            Akka.system().dispatcher(),
            cronOrderActor
    );
    
    //ebay自动进单时出错导致runnable永远为false，需要启动时重置为true
    ActorRef ebayBootUpActor = Akka.system().actorOf(Props.create(EbayBootUpActor.class), "ebay-boot-up");
    Akka.system().scheduler().scheduleOnce(
    		FiniteDuration.create(5, TimeUnit.MINUTES), 
    		ebayBootUpActor, "ebay-boot-up",
    		Akka.system().dispatcher(),
          ebayBootUpActor);
    
    //ebay订单数据复制到ERP端
    ActorRef ebaySendDataActor = Akka.system().actorOf(Props.create(EbaySendDataActor.class), "ebay-send-data");
    Akka.system().scheduler().schedule(
            FiniteDuration.create(15, TimeUnit.MINUTES),
            FiniteDuration.create(20, TimeUnit.MINUTES),
            ebaySendDataActor,
            "ebay-send-data",
            Akka.system().dispatcher(),
            ebaySendDataActor
    );

    // --------------------------------------------------------------------------

	ActorRef amazonInitActor = Akka.system().actorOf(Props.create(AmazonInitActor.class), "AmazonInitActor");
	Akka.system().scheduler().scheduleOnce(
		FiniteDuration.create(1, TimeUnit.MINUTES),
		amazonInitActor,
		"AmazonInitActor",
		Akka.system().dispatcher(),
		amazonInitActor
	);
    
	ActorRef amazonOrderActor = Akka.system().actorOf(Props.create(AmazonOrderActor.class), "AmazonOrderActor");
	Akka.system().scheduler().schedule(
		FiniteDuration.create(10, TimeUnit.MINUTES),
		FiniteDuration.create(2, TimeUnit.HOURS), 
		amazonOrderActor,
		"AmazonOrderActor", 
		Akka.system().dispatcher(), 
		amazonOrderActor
	);

	ActorRef amazonOrderItemActor = Akka.system().actorOf(Props.create(AmazonOrderItemActor.class), "AmazonOrderItemActor");
	Akka.system().scheduler().schedule(
		FiniteDuration.create(30, TimeUnit.MINUTES),
		FiniteDuration.create(1, TimeUnit.HOURS), 
		amazonOrderItemActor,
		"AmazonOrderItemActor", 
		Akka.system().dispatcher(), 
		amazonOrderItemActor
	);

	ActorRef amazonReportActor = Akka.system().actorOf(Props.create(AmazonReportActor.class), "AmazonReportActor");
	Akka.system().scheduler().schedule(
		FiniteDuration.create(1, TimeUnit.HOURS),
		FiniteDuration.create(2, TimeUnit.HOURS), 
		amazonReportActor,
		"AmazonReportActor", 
		Akka.system().dispatcher(), 
		amazonReportActor
	);
	
	ActorRef amazonReportSnapshotActor = Akka.system().actorOf(Props.create(AmazonReportSnapshotActor.class), "AmazonReportSnapshotActor");
	Akka.system().scheduler().schedule(
		FiniteDuration.create(2, TimeUnit.HOURS),
		FiniteDuration.create(2, TimeUnit.HOURS), 
		amazonReportSnapshotActor,
		"AmazonReportSnapshotActor", 
		Akka.system().dispatcher(), 
		amazonReportSnapshotActor
	);

	ActorRef amazonReportToOrderActor = Akka.system().actorOf(Props.create(AmazonReportToOrderActor.class), "AmazonReportToOrderActor");
	Akka.system().scheduler().schedule(
		FiniteDuration.create(3, TimeUnit.HOURS),
		FiniteDuration.create(20, TimeUnit.MINUTES), 
		amazonReportToOrderActor,
		"AmazonReportToOrderActor", 
		Akka.system().dispatcher(), 
		amazonReportToOrderActor
	);

	ActorRef amazonPushOrderActor = Akka.system().actorOf(Props.create(AmazonPushOrderActor.class), "AmazonPushOrderActor");
	Akka.system().scheduler().schedule(
		FiniteDuration.create(1, TimeUnit.HOURS),
		FiniteDuration.create(6, TimeUnit.MINUTES), 
		amazonPushOrderActor,
		"AmazonPushOrderActor", 
		Akka.system().dispatcher(), 
		amazonPushOrderActor
	);

    // --------------------------------------------------------------------------  
  }
}