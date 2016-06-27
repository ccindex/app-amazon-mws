package actors;


import akka.actor.UntypedActor;
import models.CronOrder;
import play.Logger;
import play.api.Play;

import java.net.InetAddress;
import java.util.Date;
import java.util.List;

/**
 * 心跳
 */
public class HeartActor extends UntypedActor {
  @Override
  public void onReceive(Object message) throws Exception {
    System.out.println("--------------beart heart------------------");
    try {

      String ip =InetAddress.getLocalHost().getHostAddress();
      List<CronOrder> cronOrder=CronOrder.find.where().eq("apAddress",ip).findList();
      if(null!=cronOrder && cronOrder.size()>0){
        for(int i=0;i<cronOrder.size();i++){
          CronOrder order=CronOrder.find.byId(cronOrder.get(i).id);
          order.createdAt=new Date();
          order.update();
        }
      };
    }catch (Exception e){
      Logger.error("beart heart:"+e.getMessage());
    }
  }
}
