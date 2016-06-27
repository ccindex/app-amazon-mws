package actors;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;

import com.avaje.ebean.Ebean;

import akka.actor.UntypedActor;
import models.CronOrder;
import models.EbayLog;
import models.EbayLog.LogType;
import models.EbaySendData;
import play.Logger;

public class EbayBootUpActor extends UntypedActor {
	
	@Override
	public void onReceive(Object arg0) throws Exception {
		Logger.info("EbayBootUpActor:"+new Date());
		
		String ip = null;
		try {
			// 按ip处理 ：每台服务器只处理本账号数据
			ip = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
			EbayLog ebayLog = new EbayLog();
			ebayLog.time = new Date();
			ebayLog.type = LogType.ERROR;
			ebayLog.content = "EbayBootUpActor获取服务器本机ip出错";
			ebayLog.save();
			return;
		}
		
		try{
			//只检测当前ip的状态
			List<CronOrder> cronOrders = CronOrder.find.where().eq("status", CronOrder.Status.运行).eq("runnable", false).eq("apAddress", ip).findList();
			if(cronOrders==null){
				return;
			}
			for(CronOrder cronOrder:cronOrders){
				cronOrder.runnable=true;
				cronOrder.update();
			}
			
			List<EbaySendData> ebaySendDatas = Ebean.find(EbaySendData.class).where().eq("runnable", false).findList();
			if (ebaySendDatas != null) {
				for(int i=0,len=ebaySendDatas.size();i<len;i++){
					EbaySendData data=ebaySendDatas.get(i);
					if(i==0){
						data.runnable=true;
						data.ip=null;
						data.update();
					}else{
						data.delete();
					}
				}
			}
			
			if(Ebean.find(EbaySendData.class).where().eq("runnable", true).findRowCount()==0){
				EbayLog ebayLog = new EbayLog();
				ebayLog.time = new Date();
				ebayLog.type = LogType.ERROR;
				ebayLog.content = "EbayBootUpActor检测到:"+ip+"上,EbaySendData数据是空的";
				ebayLog.save();
			}
			
		}catch(Exception e){
			e.printStackTrace();
			
			EbayLog log=new EbayLog();
			log.type=EbayLog.LogType.ERROR;
			log.content="EbayBootUpActor异常:"+e.toString();
			log.time=new Date();
			log.save();
			
			Logger.error("EbayBootUpActor异常:"+e.toString());
		}
	}



}
