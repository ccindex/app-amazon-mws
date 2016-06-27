package actors;

import akka.actor.UntypedActor;
import misc.*;
import models.CronOrder;
import models.EbayLog;
import models.EbayOrderMaster;
import models.EbayPaypal;
import models.EbayLog.LogType;
import models.EbayOrderDetail;
import play.Logger;
import play.libs.Json;
import urn.ebay.apis.eBLBaseComponents.PaymentTransactionType;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.databind.JsonNode;

/**
 *  ebay 获取订单定时任务详细
 */
public class CronOrderActor extends UntypedActor {
  

  @Override
  public void onReceive(Object message) throws Exception {
	  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	  Logger.debug("ebay自动进单定时任务开始,"+sdf.format(new Date()));
	  
	  String ip = null;
		try {
			// 按ip处理 ：每台服务器只处理本账号数据
			ip = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
			EbayLog ebayLog = new EbayLog();
			ebayLog.time = new Date();
			ebayLog.type = LogType.ERROR;
			ebayLog.content = "ebay自动进单,获取服务器本机ip出错";
			ebayLog.save();
			return;
		}
		try{
			List<CronOrder> list = CronOrder.find.where().eq("status", CronOrder.Status.运行).eq("runnable", true).eq("apAddress", ip).findList();
			if(list==null){
				return;
			}
			EbaySDK3 ebaySDK3 = new EbaySDK3();
			for(CronOrder cronOrder:list){
				
				try{
					cronOrder.runnable = false;
					cronOrder.update();
					
					Date before = cronOrder.fireAt;
					Date now = new Date();
					if(cronOrder.account==null){
						Logger.error("ebay自动进单,cronOrder对应的account为空:"+cronOrder.accountName);
						continue;
					}else if(cronOrder.account.platform==null){
						Logger.error("ebay自动进单,cronOrder.account对应的platform为空:"+cronOrder.accountName);
						continue;
					}else if(cronOrder.account.platform.channel==null){
						Logger.error("ebay自动进单,cronOrder.account.platform对应的channel为空:"+cronOrder.accountName);
						continue;
					}
					
					if (now.getTime() - before.getTime() > cronOrder.minute * 60000) {
						if ("ebay".equals(cronOrder.account.platform.channel)) {
							//先按下单时间获取，再按状态修改时间获取；最后更新fireAt时间
							ebaySDK3.handleByCreateTime(cronOrder);
							ebaySDK3.handleByModTime(cronOrder);
						}
					}
				}catch(Exception e){
					e.printStackTrace();
				}finally {
					cronOrder.runnable=true;
					cronOrder.update();
				}
				
			}
		}catch(Exception e){
			e.printStackTrace();
			Logger.info("ebay自动进单未知异常||"+sdf.format(new Date())+ "||" + e.toString());
		}
	  
      
  }
  
  
}
