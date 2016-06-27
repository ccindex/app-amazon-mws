package actors;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.avaje.ebean.Ebean;

import akka.actor.UntypedActor;
import misc.EbaySDK3;
import models.EbayLog;
import models.EbayMonetaryDetail;
import models.EbayOrderDetail;
import models.EbayOrderMaster;
import models.EbayPaypal;
import models.EbaySendData;
import models.EbayLog.LogType;
import play.Logger;
import urn.ebay.apis.eBLBaseComponents.PaymentTransactionType;

public class EbaySendDataActor extends UntypedActor {

	@Override
	public void onReceive(Object arg0) throws Exception {
		Logger.debug("EbaySendDataActor定时任务开始," + new Date());

		try {
			String ip = getIp();
			if (ip == null) {
				Logger.debug("EbaySendDataActor获取本机IP失败," + new Date());
				return;
			}
			// 同一时刻只有一个ap能把数据传到erp端
			List<EbaySendData> list = Ebean.find(EbaySendData.class).findList();
			if (list == null || list.size() == 0) {
				Logger.error("EbaySendData数据是空的");
				EbayLog ebayLog = new EbayLog();
				ebayLog.time = new Date();
				ebayLog.type = LogType.ERROR;
				ebayLog.content = ip + "上,EbaySendData数据是空的";
				ebayLog.save();
				return;
			}

			EbaySendData ebaySendData = list.get(0);

			if (!ebaySendData.runnable) {
				Logger.debug("EbaySendDataActor已经在运行,ip: " + ebaySendData.ip + "||" + new Date());
				return;
			}
			sendData(ebaySendData, ip);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private String getIp() {
		String ip = null;
		try {
			ip = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
			EbayLog ebayLog = new EbayLog();
			ebayLog.time = new Date();
			ebayLog.type = LogType.ERROR;
			ebayLog.content = "EbaySendDataActor,获取服务器本机ip出错";
			ebayLog.save();
		}
		return ip;
	}

	private void sendData(EbaySendData ebaySendData, String ip) {
		List<erp.models.EbayOrderMaster> erpMasterList=null;
		List<erp.models.EbayOrderDetail> erpDetailList=null;
		List<erp.models.EbayMonetaryDetail> erpMonetaryList=null;
		
		try {
			ebaySendData.runnable = false;
			ebaySendData.ip = ip;
			ebaySendData.update();

			handlePaypalFailed();

			long start = System.currentTimeMillis();

			
			Ebean.beginTransaction();
			List<EbayOrderMaster> ebayOrderMasters = Ebean.find(EbayOrderMaster.class).where().eq("isHandled", false)
					.eq("paypalFailed", false).orderBy("updateAt").setMaxRows(2000).findList();
			if (ebayOrderMasters != null) {
				erpMasterList=new ArrayList<>(ebayOrderMasters.size());
				for (EbayOrderMaster ebayOrderMaster : ebayOrderMasters) {
					erpMasterList.add(copyOrderMasterToERP(ebayOrderMaster));
					
					ebayOrderMaster.isHandled=true;
					ebayOrderMaster.update();
				}
				Ebean.getServer("erp").save(erpMasterList);
			}

			List<EbayOrderDetail> ebayOrderDetails = Ebean.find(EbayOrderDetail.class).where().eq("isHandled", false)
					.orderBy("updateAt").setMaxRows(2400).findList();
			if (ebayOrderDetails != null) {
				erpDetailList=new ArrayList<>(ebayOrderDetails.size());
				for (EbayOrderDetail ebayOrderDetail : ebayOrderDetails) {
					erpDetailList.add(copyOrderDetailToERP(ebayOrderDetail));
					
					ebayOrderDetail.isHandled=true;
					ebayOrderDetail.update();
				}
				Ebean.getServer("erp").save(erpDetailList);
			}

			List<EbayMonetaryDetail> ebayMonetaryDetails = Ebean.find(EbayMonetaryDetail.class).where()
					.eq("isHandled", false).orderBy("updateAt").setMaxRows(2400).findList();
			if (ebayMonetaryDetails != null) {
				erpMonetaryList=new ArrayList<>(ebayMonetaryDetails.size());
				for (EbayMonetaryDetail ebayMonetaryDetail : ebayMonetaryDetails) {
					erpMonetaryList.add(copyMonetaryDetailToERP(ebayMonetaryDetail));
					
					ebayMonetaryDetail.isHandled=true;
					ebayMonetaryDetail.update();
				}
				Ebean.getServer("erp").save(erpMonetaryList);
			}
			
			EbayLog ebayLog=new EbayLog();
			ebayLog.time=new Date();
			ebayLog.type=EbayLog.LogType.NORMAL;
			String logContent="EbaySendData完成,ip:"+ip+"||Master:" + (ebayOrderMasters == null ? -1 : ebayOrderMasters.size()) + ",detail:"
					+ (ebayOrderDetails == null ? -1: ebayOrderDetails.size()) + ",monetary:"
					+ (ebayMonetaryDetails == null ? -1 : ebayMonetaryDetails.size()) + ",transaction cost:"
					+ (System.currentTimeMillis() - start);
			ebayLog.content=logContent;
			ebayLog.save();
			
			ebaySendData.runnable = true;
			ebaySendData.ip = null;
			ebaySendData.update();
			
			Ebean.commitTransaction();

			Logger.info(logContent);
		} catch (Exception e) {
			e.printStackTrace();
			Ebean.rollbackTransaction();

			Logger.error("EbaySendData出现异常||" + ip + "||" + e.toString());

			if(erpMasterList!=null){
				Ebean.getServer("erp").delete(erpMasterList);
			}
			if(erpDetailList!=null){
				Ebean.getServer("erp").delete(erpDetailList);
			}
			if(erpMonetaryList!=null){
				Ebean.getServer("erp").delete(erpMonetaryList);
			}
			
			EbayLog ebayExceptionLog = new EbayLog();
			ebayExceptionLog.type = EbayLog.LogType.ERROR;
			ebayExceptionLog.content = "EbaySendData出现异常||" + ip + "||" + getExceptionDetail(e);
			if (ebayExceptionLog.content.length() >= 1000) {
				ebayExceptionLog.content = ebayExceptionLog.content.substring(0, 999);
			}
			ebayExceptionLog.time = new Date();
			ebayExceptionLog.save();
		} finally {
			Ebean.endTransaction();
			
			if(!ebaySendData.runnable){
				ebaySendData.runnable = true;
				ebaySendData.ip = null;
				ebaySendData.update();
			}
			
		}
	}

	private erp.models.EbayOrderMaster copyOrderMasterToERP(EbayOrderMaster ebayOrderMaster) {
		erp.models.EbayOrderMaster erpOrderMaster = new erp.models.EbayOrderMaster();
		erpOrderMaster.setId(ebayOrderMaster.id);
		erpOrderMaster.setAmountAdjust(ebayOrderMaster.amountAdjust);
		erpOrderMaster.setAmountAdjustCurrency(ebayOrderMaster.amountAdjustCurrency);
		erpOrderMaster.setAmountPaid(ebayOrderMaster.amountPaid);
		erpOrderMaster.setAmountPaidCurrency(ebayOrderMaster.amountPaidCurrency);
		erpOrderMaster.setAmountSaved(ebayOrderMaster.amountSaved);
		erpOrderMaster.setAmountSavedCurrency(ebayOrderMaster.amountSavedCurrency);
		erpOrderMaster.setBuyerCity(ebayOrderMaster.buyerCity);
		erpOrderMaster.setBuyerCountry(ebayOrderMaster.buyerCountry);
		erpOrderMaster.setBuyerId(ebayOrderMaster.buyerId);
		erpOrderMaster.setBuyerName(ebayOrderMaster.buyerName);
		erpOrderMaster.setBuyerPhone(ebayOrderMaster.buyerPhone);
		erpOrderMaster.setBuyerState(ebayOrderMaster.buyerState);
		erpOrderMaster.setBuyerStreet1(ebayOrderMaster.buyerStreet1);
		erpOrderMaster.setBuyerStreet2(ebayOrderMaster.buyerStreet2);
		erpOrderMaster.setBuyerZip(ebayOrderMaster.buyerZip);
		erpOrderMaster.setCheckoutStatus(ebayOrderMaster.checkoutStatus);
		erpOrderMaster.setCreateDate(ebayOrderMaster.createDate);
		erpOrderMaster.setEiasToken(ebayOrderMaster.eiasToken);
		erpOrderMaster.setFireAtDate(ebayOrderMaster.fireAtDate);
		erpOrderMaster.setGetItFast(ebayOrderMaster.getItFast);
		erpOrderMaster.setGlobalShippingCost(ebayOrderMaster.globalShippingCost);
		erpOrderMaster.setGlobalShippingCostCurrency(ebayOrderMaster.globalShippingCostCurrency);
		erpOrderMaster.setGlobalShippingImportCharge(ebayOrderMaster.globalShippingImportCharge);
		erpOrderMaster.setGlobalShippingImportChargeCurrency(ebayOrderMaster.globalShippingImportChargeCurrency);
		erpOrderMaster.setGlobalShippingService(ebayOrderMaster.globalShippingService);
		erpOrderMaster.setInsurance(ebayOrderMaster.insurance);
		erpOrderMaster.setInsuranceCurrency(ebayOrderMaster.insuranceCurrency);
		erpOrderMaster.setIntegratedMerchantCreditCardEnabled(ebayOrderMaster.integratedMerchantCreditCardEnabled);
		erpOrderMaster.setLastModifiedTime(ebayOrderMaster.lastModifiedTime);
		erpOrderMaster.setIsMultiLegShipping(ebayOrderMaster.isMultiLegShipping);
		erpOrderMaster.setOrderId(ebayOrderMaster.orderId);
		erpOrderMaster.setOrderStatus(ebayOrderMaster.orderStatus);
		erpOrderMaster.setPaidTime(ebayOrderMaster.paidTime);
		erpOrderMaster.setPaymentMethod(ebayOrderMaster.paymentMethod);
		erpOrderMaster.setPaypalDate(ebayOrderMaster.paypalDate);
		erpOrderMaster.setPaypalEmail(ebayOrderMaster.paypalEmail);
		erpOrderMaster.setPaypalStatus(ebayOrderMaster.paypalStatus);
		erpOrderMaster.setSalesRecordNumber(ebayOrderMaster.salesRecordNumber);
		erpOrderMaster.setSellerEmail(ebayOrderMaster.sellerEmail);
		erpOrderMaster.setSellerUserId(ebayOrderMaster.sellerUserId);
		erpOrderMaster.setShippedTime(ebayOrderMaster.shippedTime);
		erpOrderMaster.setShippingCity(ebayOrderMaster.shippingCity);
		erpOrderMaster.setShippingCost(ebayOrderMaster.shippingCost);
		erpOrderMaster.setShippingCostCurrency(ebayOrderMaster.shippingCostCurrency);
		erpOrderMaster.setShippingCountry(ebayOrderMaster.shippingCountry);
		erpOrderMaster.setShippingIncludedInTax(ebayOrderMaster.shippingIncludedInTax);
		erpOrderMaster.setShippingRecipientName(ebayOrderMaster.shippingRecipientName);
		erpOrderMaster.setShippingRecipientPhone(ebayOrderMaster.shippingRecipientPhone);
		erpOrderMaster.setShippingReferenceId(ebayOrderMaster.shippingReferenceId);
		erpOrderMaster.setShippingService(ebayOrderMaster.shippingService);
		erpOrderMaster.setShippingState(ebayOrderMaster.shippingState);
		erpOrderMaster.setShippingStreet1(ebayOrderMaster.shippingStreet1);
		erpOrderMaster.setShippingStreet2(ebayOrderMaster.shippingStreet2);
		erpOrderMaster.setShippingZip(ebayOrderMaster.shippingZip);
		erpOrderMaster.setSubtotal(ebayOrderMaster.subtotal);
		erpOrderMaster.setSubtotalCurrency(ebayOrderMaster.subtotalCurrency);
		erpOrderMaster.setTax(ebayOrderMaster.tax);
		erpOrderMaster.setTaxCurrency(ebayOrderMaster.taxCurrency);
		erpOrderMaster.setTaxPercent(ebayOrderMaster.taxPercent);
		erpOrderMaster.setTaxState(ebayOrderMaster.taxState);
		erpOrderMaster.setTotal(ebayOrderMaster.total);
		erpOrderMaster.setTotalCurrency(ebayOrderMaster.totalCurrency);
		erpOrderMaster.setErpPlatformAccountId(ebayOrderMaster.erpPlatformAccountId);
		erpOrderMaster.setOrderDetailNum(ebayOrderMaster.orderDetailNum);
		erpOrderMaster.setMonetaryDetailNum(ebayOrderMaster.monetaryDetailNum);
		erpOrderMaster.setCreatingUserRole(ebayOrderMaster.creatingUserRole);
		erpOrderMaster.setIsHandled(false);

		return erpOrderMaster;
	}

	private erp.models.EbayOrderDetail copyOrderDetailToERP(EbayOrderDetail orderDetail) {
		erp.models.EbayOrderDetail erpOrderDetail = new erp.models.EbayOrderDetail();
		erpOrderDetail.setId(orderDetail.id);
		erpOrderDetail.setMasterId(orderDetail.master.id);
		erpOrderDetail.setActualHandlingCost(orderDetail.actualHandlingCost);
		erpOrderDetail.setActualHandlingCostCurrency(orderDetail.actualHandlingCostCurrency);
		erpOrderDetail.setActualShippingCost(orderDetail.actualShippingCost);
		erpOrderDetail.setActualShippingCostCurrency(orderDetail.actualShippingCostCurrency);
		erpOrderDetail.setBuyerEmail(orderDetail.buyerEmail);
		erpOrderDetail.setCreateAt(orderDetail.createAt);
		erpOrderDetail.setEstimatedDeliveryTimeMax(orderDetail.estimatedDeliveryTimeMax);
		erpOrderDetail.setEstimatedDeliveryTimeMin(orderDetail.estimatedDeliveryTimeMin);
		erpOrderDetail.setItemId(orderDetail.itemId);
		erpOrderDetail.setPrice(orderDetail.price);
		erpOrderDetail.setPriceCurrency(orderDetail.priceCurrency);
		erpOrderDetail.setQuantity(orderDetail.quantity);
		erpOrderDetail.setShippingCarrier(orderDetail.shippingCarrier);
		erpOrderDetail.setShippingTrackingNum(orderDetail.shippingTrackingNum);
		erpOrderDetail.setSite(orderDetail.site);
		erpOrderDetail.setSku(orderDetail.sku);
		erpOrderDetail.setTaxAmount(orderDetail.taxAmount);
		erpOrderDetail.setTaxOnHandling(orderDetail.taxOnHandling);
		erpOrderDetail.setTaxOnShipping(orderDetail.taxOnShipping);
		erpOrderDetail.setTaxOnSubtotal(orderDetail.taxOnSubtotal);
		erpOrderDetail.setTitle(orderDetail.title);
		erpOrderDetail.setTotalTaxAmount(orderDetail.totalTaxAmount);
		erpOrderDetail.setTotalTaxAmountCurrency(orderDetail.totalTaxAmountCurrency);
		erpOrderDetail.setTransactionId(orderDetail.transactionId);
		erpOrderDetail.setStaticAlias(orderDetail.staticAlias);
		erpOrderDetail.setSite(orderDetail.site);
		erpOrderDetail.setBuyerEmail(orderDetail.buyerEmail);
		erpOrderDetail.setWasteRecyclingFeeTaxAmount(orderDetail.wasteRecyclingFeeTaxAmount);
		erpOrderDetail.setSalesRecordNumber(orderDetail.salesRecordNumber);
		erpOrderDetail.setIsHandled(false);
		
		return erpOrderDetail;
	}
	
	private erp.models.EbayMonetaryDetail copyMonetaryDetailToERP(EbayMonetaryDetail monetary) {
		erp.models.EbayMonetaryDetail erpMonetary = new erp.models.EbayMonetaryDetail();
		erpMonetary.setId(monetary.id);
		erpMonetary.setMasterId(monetary.master.id);
		erpMonetary.setAmount(monetary.amount);
		erpMonetary.setAmountCurrency(monetary.amountCurrency);
		erpMonetary.setFee(monetary.fee);
		erpMonetary.setFeeCurrency(monetary.feeCurrency);
		erpMonetary.setFromName(monetary.fromName);
		erpMonetary.setFromType(monetary.fromType);
		erpMonetary.setReferenceId(monetary.referenceId);
		erpMonetary.setReferenceIdType(monetary.referenceIdType);
		erpMonetary.setRefundType(monetary.refundType);
		erpMonetary.setStatus(monetary.status);
		erpMonetary.setTime(monetary.time);
		erpMonetary.setToName(monetary.toName);
		erpMonetary.setToType(monetary.toType);
		erpMonetary.setType(erp.models.EbayMonetaryDetail.TYPE.values()[monetary.type.ordinal()]);
		erpMonetary.setIsHandled(false);
		return erpMonetary;
	}

	private String getExceptionDetail(Exception e) {
		StringBuilder sb = new StringBuilder(e.toString());
		StackTraceElement[] stacks = e.getStackTrace();
		if (stacks != null) {
			for (int i = 0, len = stacks.length; i < len; i++) {
				sb.append("|at ").append(stacks[i]);
			}
		}
		return sb.toString();
	}

	/**
	 * 处理调用paypal接口失败的;获取成功后将isHandled标记为false，让ERP端更新paypal相关数据
	 */
	private void handlePaypalFailed() {
		List<EbayPaypal> paypals = EbayPaypal.finder.where().findList();

		if (paypals == null || paypals.size() == 0) {
			return;
		}

		EbaySDK3 ebaySDK3 = new EbaySDK3();
		try {
			Ebean.beginTransaction();
			for (EbayPaypal paypal : paypals) {
				try {
					PaymentTransactionType paypalDetail = ebaySDK3.getPaypalTransactionDetail(paypal.paypalConfigId,
							paypal.transactionId,paypal.sellerEmail);
					if (paypalDetail != null) {
						List<EbayMonetaryDetail> monetaryDetails = Ebean.find(EbayMonetaryDetail.class).where()
								.eq("referenceId", paypal.transactionId).findList();
						if (monetaryDetails != null) {
							for (EbayMonetaryDetail monetaryDetail : monetaryDetails) {
								ebaySDK3.parsePaypalTransactionDetail(monetaryDetail.master, paypalDetail);
								monetaryDetail.master.paypalFailed = false;
								monetaryDetail.master.update();
							}
						}
						paypal.delete();
						Logger.debug("ebay自动进单,成功获取了paypal的付款信息,transactionId:" + paypal.transactionId);
					} else {
						paypal.retryTimes++;
						paypal.update();
					}
				} catch (Exception e) {
					Logger.info("EbayPaypalInfoActor中获取失败:" + paypal.transactionId);
					paypal.retryTimes++;
					paypal.update();
				}
			}
			Ebean.commitTransaction();
		} catch (Exception e) {
			e.printStackTrace();
			Ebean.rollbackTransaction();
		} finally {
			Ebean.endTransaction();
		}

	}

}
