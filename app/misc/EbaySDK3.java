package misc;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import com.avaje.ebean.Ebean;
import com.ebay.sdk.ApiContext;
import com.ebay.sdk.ApiCredential;
import com.ebay.sdk.ApiException;
import com.ebay.sdk.SdkException;
import com.ebay.sdk.call.GetOrdersCall;
import com.ebay.soap.eBLBaseComponents.AddressType;
import com.ebay.soap.eBLBaseComponents.AmountType;
import com.ebay.soap.eBLBaseComponents.CheckoutStatusType;
import com.ebay.soap.eBLBaseComponents.CompleteStatusCodeType;
import com.ebay.soap.eBLBaseComponents.DetailLevelCodeType;
import com.ebay.soap.eBLBaseComponents.InsuranceDetailsType;
import com.ebay.soap.eBLBaseComponents.ItemType;
import com.ebay.soap.eBLBaseComponents.MultiLegShippingServiceType;
import com.ebay.soap.eBLBaseComponents.OrderStatusCodeType;
import com.ebay.soap.eBLBaseComponents.OrderType;
import com.ebay.soap.eBLBaseComponents.PaginationType;
import com.ebay.soap.eBLBaseComponents.PaymentTransactionType;
import com.ebay.soap.eBLBaseComponents.RefundTransactionInfoType;
import com.ebay.soap.eBLBaseComponents.SalesTaxType;
import com.ebay.soap.eBLBaseComponents.ShippingDetailsType;
import com.ebay.soap.eBLBaseComponents.ShippingPackageInfoType;
import com.ebay.soap.eBLBaseComponents.ShippingServiceOptionsType;
import com.ebay.soap.eBLBaseComponents.SortOrderCodeType;
import com.ebay.soap.eBLBaseComponents.TaxDetailsType;
import com.ebay.soap.eBLBaseComponents.TradingRoleCodeType;
import com.ebay.soap.eBLBaseComponents.TransactionType;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paypal.core.Constants;

import models.CronOrder;
import models.EbayLog;
import models.EbayOrderDetail;
import models.EbayOrderMaster;
import models.EbayPaypal;
import models.PaypalConfig;
import models.EbayLog.LogType;
import models.EbayMonetaryDetail;
import play.Logger;
import play.Play;
import play.libs.Json;
import urn.ebay.api.PayPalAPI.GetTransactionDetailsReq;
import urn.ebay.api.PayPalAPI.GetTransactionDetailsRequestType;
import urn.ebay.api.PayPalAPI.GetTransactionDetailsResponseType;
import urn.ebay.api.PayPalAPI.PayPalAPIInterfaceServiceService;
import urn.ebay.apis.eBLBaseComponents.AckCodeType;
import urn.ebay.apis.eBLBaseComponents.ErrorType;

/**
 * @author wq
 */
public class EbaySDK3 {
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	private PaypalConfig paypalConfig;
	private long start;
	private final static ObjectMapper jsonObjectMapper = new ObjectMapper();
	static {
		jsonObjectMapper.setSerializationInclusion(Include.NON_DEFAULT);
	}

	/**
	 * 获取paypal交易详细失败的transactionId的集合
	 */
	private List<EbayPaypal> paypalFailedList = new ArrayList<>();

	/**
	 * 按ModTime获取订单 获取订单并保存成功后会将cronOrder的fireAt更新
	 */
	public void handleByModTime(CronOrder cronOrder) {
		start = System.currentTimeMillis();
		paypalConfig = null;

		JsonNode config = Json.parse(cronOrder.config);
		String token = config.get("token").asText();
		ApiContext apiContext = getApiContent(token);
		GetOrdersCall apiCall = new GetOrdersCall(apiContext);

		Date maxEndDate = getMaxAllowedEndDate(cronOrder.fireAt);
		initGetOrderCallApiForModTime(apiCall, cronOrder.fireAt, maxEndDate, false);
		Logger.debug(cronOrder.accountName + ",ebay自动进单,根据modTime||fireAt: " + sdf.format(cronOrder.fireAt) + " :end "
				+ sdf.format(maxEndDate));

		EbayLog ebayStartLog = new EbayLog();
		ebayStartLog.type = EbayLog.LogType.NORMAL;
		ebayStartLog.content = cronOrder.accountName + ",ebay自动进单开始,根据modTime||from " + sdf.format(cronOrder.fireAt)
				+ " to " + sdf.format(maxEndDate);
		ebayStartLog.time = new Date();
		ebayStartLog.save();

		int currentPage = 0;
		try {
			Ebean.beginTransaction();

			getOrderInTimeInterval(apiCall, cronOrder.fireAt, maxEndDate, cronOrder, false);

			// 没有成功获取paypal交易详情的 最后再试一次
			handlePaypalFailed(cronOrder);

			Ebean.commitTransaction();
			
			Logger.debug(cronOrder.accountName + ",ebay自动进单调用完成,根据modTime||start:" + sdf.format(cronOrder.fireAt) + "||end:"
					+ sdf.format(maxEndDate) + "||总调用时间:" + (System.currentTimeMillis() - start) + ",当前时间:"
					+ sdf.format(new Date()));

			cronOrder.fireAt = maxEndDate;
		} catch (Exception e) {
			e.printStackTrace();

			Logger.error(cronOrder.accountName + ",ebay自动进单,根据modTime,getOrders()异常||" + sdf.format(new Date()) + "||from "
					+ sdf.format(cronOrder.fireAt) + " to " + sdf.format(maxEndDate) + "||" + getExceptionDetail(e));
			Ebean.rollbackTransaction();

			EbayLog ebayExceptionLog = new EbayLog();
			ebayExceptionLog.type = EbayLog.LogType.ERROR;
			ebayExceptionLog.content = cronOrder.accountName + ",ebay订单获取出错,根据modTime||cronOrder:" + "||from "
					+ sdf.format(cronOrder.fireAt) + " to " + sdf.format(maxEndDate) + "||page:" + currentPage + "||"
					+ getExceptionDetail(e);
			if (ebayExceptionLog.content.length() >= 1000) {
				ebayExceptionLog.content = ebayExceptionLog.content.substring(0, 999);
			}
			ebayExceptionLog.time = new Date();
			ebayExceptionLog.save();
		} finally {
			Ebean.endTransaction();

			for (EbayPaypal paypal : paypalFailedList) {
				paypal.save();
			}
			paypalFailedList.clear();
		}
	}

	/**
	 * 根据createTime获取订单
	 */
	public void handleByCreateTime(CronOrder cronOrder) {
		start = System.currentTimeMillis();
		paypalConfig = null;

		JsonNode config = Json.parse(cronOrder.config);
		String token = config.get("token").asText();
		ApiContext apiContext = getApiContent(token);
		GetOrdersCall apiCall = new GetOrdersCall(apiContext);

		Date maxEndDate = getMaxAllowedEndDate(cronOrder.fireAt);
		initGetOrderCallApiForCreateTime(apiCall, cronOrder.fireAt, maxEndDate);

		Logger.debug(cronOrder.accountName + ",ebay自动进单,根据createTime||fireAt: " + sdf.format(cronOrder.fireAt) + " :end "
				+ sdf.format(maxEndDate));

		EbayLog ebayStartLog = new EbayLog();
		ebayStartLog.type = EbayLog.LogType.NORMAL;
		ebayStartLog.content = cronOrder.accountName + ",ebay自动进单开始,根据createTime||from " + sdf.format(cronOrder.fireAt)
				+ " to " + sdf.format(maxEndDate);
		ebayStartLog.time = new Date();
		ebayStartLog.save();

		int currentPage = 0;
		try {
			OrderType[] orders = apiCall.getOrders();
			Ebean.beginTransaction();
			int pageNum = apiCall.getReturnedPaginationResult().getTotalNumberOfPages();
			int returnedTotal = apiCall.getReturnedPaginationResult().getTotalNumberOfEntries();
			int actualTotal = 0;
			Logger.debug(cronOrder.accountName + ",ebay自动进单,根据createTime,第一次调用返回的总页数:" + pageNum + " :总个数 " + returnedTotal);
			// 获取最近的记录时，总数会变;而且返回的总个数不准
			for (int i = 1; i <= pageNum; i++) {
				currentPage = i;
				if (i != 1) {
					PaginationType paginationType = new PaginationType();
					paginationType.setPageNumber(i);
					paginationType.setEntriesPerPage(100);
					apiCall.setPagination(paginationType);
					orders = apiCall.getOrders();
				}
				if (orders == null) {
					continue;
				}
				parseOrdersToDB(orders, cronOrder);
				pageNum = apiCall.getReturnedPaginationResult().getTotalNumberOfPages();
				actualTotal += orders.length;
			}
			returnedTotal = apiCall.getReturnedPaginationResult().getTotalNumberOfEntries();

			EbayLog ebaySuccessLog = new EbayLog();
			ebaySuccessLog.content = cronOrder.accountName + ",ebay订单获取完成,根据createTime||返回的总数:" + returnedTotal + ",实际的总数:"
					+ actualTotal + "||fireAt:" + sdf.format(cronOrder.fireAt) + "||costTime:"
					+ (System.currentTimeMillis() - start);
			ebaySuccessLog.type = LogType.NORMAL;
			ebaySuccessLog.time = new Date();
			ebaySuccessLog.save();

			// 没有成功获取paypal交易详情的 最后再试一次
			Iterator<EbayPaypal> iterator = paypalFailedList.iterator();
			while (iterator.hasNext()) {
				EbayPaypal ebayPaypal = iterator.next();
				urn.ebay.apis.eBLBaseComponents.PaymentTransactionType paypalDetail = getPaypalTransactionDetail(
						cronOrder.paypalId, ebayPaypal.transactionId, ebayPaypal.sellerEmail);
				if (paypalDetail != null) {
					iterator.remove();
					List<EbayMonetaryDetail> monetaryDetails = Ebean.find(EbayMonetaryDetail.class).where()
							.eq("referenceId", ebayPaypal.transactionId).findList();
					if (monetaryDetails != null) {
						for (EbayMonetaryDetail monetaryDetail : monetaryDetails) {
							parsePaypalTransactionDetail(monetaryDetail.master, paypalDetail);
							monetaryDetail.master.paypalFailed = false;
							monetaryDetail.master.update();
						}
					}
				} else {
					Logger.error(cronOrder.accountName + ",ebay自动进单,根据createTime||调用paypal接口依然失败," + sdf.format(new Date())
							+ "||transactionId:" + ebayPaypal.transactionId);
				}
			}
			Ebean.commitTransaction();

			Logger.info(cronOrder.accountName + ",ebay自动进单调用完成,根据createTime|| total page:" + pageNum + " costTime: "
					+ (System.currentTimeMillis() - start) + "||actualTotal:" + actualTotal + "||"
					+ sdf.format(new Date()));
		} catch (Exception e) {
			e.printStackTrace();

			Logger.error(cronOrder.accountName + ",ebay自动进单,根据createTime,getOrders()异常||" + sdf.format(new Date()) + "||from "
					+ sdf.format(cronOrder.fireAt) + " to " + sdf.format(maxEndDate) + "||" + getExceptionDetail(e));
			Ebean.rollbackTransaction();

			EbayLog ebayExceptionLog = new EbayLog();
			ebayExceptionLog.type = EbayLog.LogType.ERROR;
			ebayExceptionLog.content = cronOrder.accountName + ",ebay订单获取出错,根据modTime||cronOrder:" + "||from "
					+ sdf.format(cronOrder.fireAt) + " to " + sdf.format(maxEndDate) + "||page:" + currentPage + "||"
					+ getExceptionDetail(e);
			if (ebayExceptionLog.content.length() >= 1000) {
				ebayExceptionLog.content = ebayExceptionLog.content.substring(0, 999);
			}
			ebayExceptionLog.time = new Date();
			ebayExceptionLog.save();
		} finally {
			Ebean.endTransaction();

			for (EbayPaypal paypal : paypalFailedList) {
				paypal.save();
			}
			paypalFailedList.clear();
		}
	}

	/**
	 * 由一段时间间隔来获取订单。如果结果需要分多页获取，则将时间间隔缩短重新获取
	 * @author wq
	 */
	private void getOrderInTimeInterval(GetOrdersCall apiCall, Date startDate, Date maxEndDate, CronOrder cronOrder,
			boolean inInnerCall) throws ApiException, SdkException, Exception {
		Long[] times = checkFirstTime(apiCall, startDate, maxEndDate, cronOrder, inInnerCall);
		if (times != null) {
			Date newStartDate = new Date(startDate.getTime());
			for (int i = 0, len = times.length; i < len; i++) {
				Date newEndDate = new Date(times[i]);
				if (newEndDate.before(newStartDate)) {
					continue;
				}
				if (newEndDate.after(maxEndDate)) {
					newEndDate.setTime(maxEndDate.getTime());
				}
				initGetOrderCallApiForModTime(apiCall, newStartDate, newEndDate, true);
				getOrderInTimeInterval(apiCall, newStartDate, newEndDate, cronOrder, true);

				newStartDate.setTime(newEndDate.getTime());
			}
		}
	}

	/**
	 * @return 为避免分页需要将时间段分成几个部分,返回中间分段的截止时间；有的时间点订单很密集
	 * @author wq
	 */
	private Long[] checkFirstTime(GetOrdersCall apiCall, Date startDate, Date endDate, CronOrder cronOrder,
			boolean inInnerCall) throws ApiException, SdkException, Exception {
		OrderType[] orders = apiCall.getOrders();
		if (orders == null) {
			Logger.error(cronOrder.accountName + ",ebay自动进单,getOrders返回null.");
			return null;
		}
		int pageNum = apiCall.getReturnedPaginationResult().getTotalNumberOfPages();
		int returnedTotal = apiCall.getReturnedPaginationResult().getTotalNumberOfEntries();

		if (pageNum <= 1 && returnedTotal <= 100) {
			// 正常
			Logger.debug(cronOrder.accountName + ",ebay自动进单,不分页,调用返回的总页数:" + pageNum + ":总个数 " + returnedTotal
					+ "||start:" + sdf.format(startDate) + "||end:" + sdf.format(endDate));
			parseOrdersToDB(orders, cronOrder);

			EbayLog ebaySuccessLog = new EbayLog();
			ebaySuccessLog.content = cronOrder.accountName + ",ebay订单获取完成,不分页||返回的总数:" + returnedTotal + "||实际总数:"
					+ (orders != null ? orders.length : "null.") + "||start:" + sdf.format(startDate) + "||end:"
					+ sdf.format(endDate);
			ebaySuccessLog.type = LogType.NORMAL;
			ebaySuccessLog.time = new Date();
			ebaySuccessLog.save();
			return null;
		} else {
			int pieces = Math.max((int) Math.ceil(returnedTotal / 100.0f), pageNum);
			Long[] times = new Long[pieces];

			long maxInterval = endDate.getTime() - startDate.getTime();
			long betterInteval = (long) (maxInterval / pieces);

			for (int i = 0; i < pieces - 1; i++) {
				// 有可能以第100个订单的lastModifiedTime为截止时间获取订单会返回超过100个，直接取倒数第10个
				if (i == 0 && !inInnerCall) {
					OrderType orderType = orders[orders.length - 10];
					// 按照文档说明，这里一定不为空
					if (orderType.getCheckoutStatus() != null
							&& orderType.getCheckoutStatus().getLastModifiedTime() != null) {
						times[0] = orderType.getCheckoutStatus().getLastModifiedTime().getTimeInMillis() - 1000;
					}
					if (times[0] == null || times[0] > endDate.getTime() || times[0] < startDate.getTime()) {
						times[0] = startDate.getTime() + betterInteval;
					}
					continue;
				}

				times[i] = startDate.getTime() + betterInteval * (i + 1);
			}
			times[pieces - 1] = endDate.getTime();

			Logger.debug(cronOrder.accountName + ",ebay自动进单,分页,调用返回的总页数:" + pageNum + ":总个数 " + returnedTotal
					+ "||start:" + sdf.format(startDate) + "||end:" + sdf.format(endDate) + "||将分成" + pieces
					+ "个子时间段获取:" + Arrays.asList(times));

			EbayLog log = new EbayLog();
			log.content = cronOrder.accountName + ",ebay自动进单,分页,调用返回的总页数:" + pageNum + ":总个数 " + returnedTotal
					+ "||start:" + sdf.format(startDate) + "||end:" + sdf.format(endDate) + "||将分成" + pieces
					+ "个子时间段获取.";
			log.type = LogType.NORMAL;
			log.time = new Date();
			log.save();

			return times;
		}
	}

	private void handlePaypalFailed(CronOrder cronOrder) {
		Iterator<EbayPaypal> iterator = paypalFailedList.iterator();
		while (iterator.hasNext()) {
			EbayPaypal ebayPaypal = iterator.next();
			urn.ebay.apis.eBLBaseComponents.PaymentTransactionType paypalDetail = getPaypalTransactionDetail(
					cronOrder.paypalId, ebayPaypal.transactionId, ebayPaypal.sellerEmail);
			if (paypalDetail != null) {
				iterator.remove();
				List<EbayMonetaryDetail> monetaryDetails = Ebean.find(EbayMonetaryDetail.class).where()
						.eq("referenceId", ebayPaypal.transactionId).findList();
				if (monetaryDetails != null) {
					for (EbayMonetaryDetail monetaryDetail : monetaryDetails) {
						parsePaypalTransactionDetail(monetaryDetail.master, paypalDetail);
						monetaryDetail.master.paypalFailed = false;
						monetaryDetail.master.update();
					}
				}
			} else {
				Logger.error(cronOrder.accountName + ",ebay自动进单||调用paypal接口依然失败," + sdf.format(new Date())
						+ "||transactionId:" + ebayPaypal.transactionId);
			}
		}
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
	 * 有的订单(如111775121598-1508454325001)会包含4个字节的字符，现有的数据库字符集是UTF8处理不了，过滤之;
	 */
	private String filter4BytesUTF8(String text) {
		try {
			byte[] bytes = text.getBytes("utf-8");
			ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
			int i = 0;
			while (i < bytes.length) {
				short b = bytes[i];
				if (b > 0) {
					buffer.put(bytes[i++]);
					continue;
				}
				b += 256;
				if ((b ^ 0xC0) >> 4 == 0) {
					buffer.put(bytes, i, 2);
					i += 2;
				} else if ((b ^ 0xE0) >> 4 == 0) {
					buffer.put(bytes, i, 3);
					i += 3;
				} else if ((b ^ 0xF0) >> 4 == 0) {
					i += 4;
				} else {
					buffer.put(bytes[i++]);
				}
			}
			buffer.flip();
			return new String(buffer.array(), "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			Logger.error("ebay自动进单||filter4BytesUTF8 异常:" + e.toString());
			return text;
		}
	}

	/**
	 * 解析订单并保存到数据库
	 */
	private void parseOrdersToDB(OrderType[] orders, CronOrder cronOrder) {
		if (orders == null) {
			return;
		}
		for (OrderType order : orders) {
			//去重
			if(order.getCheckoutStatus()!=null&&order.getCheckoutStatus().getLastModifiedTime()!=null){
				if(Ebean.find(EbayOrderMaster.class).where()
						.eq("orderId", order.getOrderID())
						.eq("lastModifiedTime", order.getCheckoutStatus().getLastModifiedTime().getTime())
						.findRowCount()>0){
					continue;
				}
			}
			
			EbayOrderMaster orderMaster = new EbayOrderMaster();
			orderMaster.orderId = order.getOrderID();
			orderMaster.buyerId = order.getBuyerUserID();
			orderMaster.erpPlatformAccountId = cronOrder.erpAccountId;

			//只有合并订单才有
			if(order.getCreatingUserRole()!=null){
				orderMaster.creatingUserRole=order.getCreatingUserRole().value();
			}
			
			if (order.getAdjustmentAmount() != null) {
				orderMaster.amountAdjust = new BigDecimal(Double.toString(order.getAdjustmentAmount().getValue()));
				if (order.getAdjustmentAmount().getCurrencyID() != null) {
					orderMaster.amountAdjustCurrency = order.getAdjustmentAmount().getCurrencyID().value();
				}
			}
			if (order.getAmountPaid() != null) {
				orderMaster.amountPaid = new BigDecimal(Double.toString(order.getAmountPaid().getValue()));
				if (order.getAmountPaid().getCurrencyID() != null) {
					orderMaster.amountPaidCurrency = order.getAmountPaid().getCurrencyID().value();
				}
			}
			if (order.getAmountSaved() != null) {
				orderMaster.amountSaved = new BigDecimal(Double.toString(order.getAmountSaved().getValue()));
				if (order.getAmountSaved().getCurrencyID() != null) {
					orderMaster.amountSavedCurrency = order.getAmountSaved().getCurrencyID().value();
				}
			}

			AddressType shippingAddress = order.getShippingAddress();
			if (shippingAddress != null) {
				orderMaster.buyerName = filter4BytesUTF8(shippingAddress.getName());
				orderMaster.buyerPhone = shippingAddress.getPhone();
				orderMaster.buyerStreet1 = filter4BytesUTF8(shippingAddress.getStreet1());
				orderMaster.buyerStreet2 = shippingAddress.getStreet2();
				orderMaster.buyerCity = shippingAddress.getCityName();
				orderMaster.buyerZip = shippingAddress.getPostalCode();
				orderMaster.buyerState = shippingAddress.getStateOrProvince();
				orderMaster.buyerCountry = shippingAddress.getCountryName();
			}

			// global shipping
			orderMaster.isMultiLegShipping = order.isIsMultiLegShipping();
			if (order.isIsMultiLegShipping() && order.getMultiLegShippingDetails() != null
					&& order.getMultiLegShippingDetails().getSellerShipmentToLogisticsProvider() != null) {
				AddressType domesticAddress = order.getMultiLegShippingDetails().getSellerShipmentToLogisticsProvider()
						.getShipToAddress();
				if (domesticAddress != null) {
					orderMaster.shippingRecipientName = filter4BytesUTF8(domesticAddress.getName());
					orderMaster.shippingRecipientPhone = domesticAddress.getPhone();
					orderMaster.shippingStreet1 = filter4BytesUTF8(domesticAddress.getStreet1());
					orderMaster.shippingStreet2 = domesticAddress.getStreet2();
					orderMaster.shippingCity = domesticAddress.getCityName();
					orderMaster.shippingZip = domesticAddress.getPostalCode();
					orderMaster.shippingState = domesticAddress.getStateOrProvince();
					orderMaster.shippingCountry = domesticAddress.getCountryName();
					orderMaster.shippingReferenceId = domesticAddress.getReferenceID();
				}
				MultiLegShippingServiceType shippingServiceDetail = order.getMultiLegShippingDetails()
						.getSellerShipmentToLogisticsProvider().getShippingServiceDetails();
				if (shippingServiceDetail != null) {
					orderMaster.shippingService = shippingServiceDetail.getShippingService();
					AmountType shippingCost = shippingServiceDetail.getTotalShippingCost();
					if (shippingCost != null) {
						orderMaster.shippingCost = new BigDecimal(Double.toString(shippingCost.getValue()));
						if (shippingCost.getCurrencyID() != null) {
							orderMaster.shippingCostCurrency = shippingCost.getCurrencyID().value();
						}
					}
				}
			}

			ShippingServiceOptionsType shippingServiceOptionsType = order.getShippingServiceSelected();
			// 如果是GSP，这里就是国际运费
			if (shippingServiceOptionsType != null) {
				if (orderMaster.isMultiLegShipping) {
					orderMaster.globalShippingService = shippingServiceOptionsType.getShippingService();
					AmountType shippingCost = shippingServiceOptionsType.getShippingServiceCost();
					if (shippingCost != null) {
						orderMaster.globalShippingCost = new BigDecimal(Double.toString(shippingCost.getValue()));
						if (shippingCost.getCurrencyID() != null) {
							orderMaster.globalShippingCostCurrency = shippingCost.getCurrencyID().value();
						}
					}
					AmountType importCharge = shippingServiceOptionsType.getImportCharge();
					if (importCharge != null) {
						orderMaster.globalShippingImportCharge = new BigDecimal(
								Double.toString(importCharge.getValue()));
						if (importCharge.getCurrencyID() != null) {
							orderMaster.globalShippingImportChargeCurrency = importCharge.getCurrencyID().value();
						}
					}
				} else {
					orderMaster.shippingService = shippingServiceOptionsType.getShippingService();
					AmountType shippingCost = shippingServiceOptionsType.getShippingServiceCost();
					if (shippingCost != null) {
						orderMaster.shippingCost = new BigDecimal(Double.toString(shippingCost.getValue()));
						if (shippingCost.getCurrencyID() != null) {
							orderMaster.shippingCostCurrency = shippingCost.getCurrencyID().value();
						}
					}
				}
			}

			ShippingDetailsType shippingDetailsType = order.getShippingDetails();
			if (shippingDetailsType != null) {
				orderMaster.getItFast = shippingDetailsType.isGetItFast();
				orderMaster.salesRecordNumber = shippingDetailsType.getSellingManagerSalesRecordNumber();

				SalesTaxType salesTaxType = shippingDetailsType.getSalesTax();
				if (salesTaxType != null) {
					AmountType salesTaxAcount = salesTaxType.getSalesTaxAmount();
					if (salesTaxAcount != null) {
						orderMaster.tax = new BigDecimal(Double.toString(salesTaxAcount.getValue()));
						if (salesTaxAcount.getCurrencyID() != null) {
							orderMaster.taxCurrency = salesTaxAcount.getCurrencyID().value();
						}
					}
					orderMaster.taxPercent = new BigDecimal(Double.toString(salesTaxType.getSalesTaxPercent()));
					orderMaster.shippingIncludedInTax = salesTaxType.isShippingIncludedInTax();
					orderMaster.taxState = salesTaxType.getSalesTaxState();
				}
				InsuranceDetailsType insuranceDetailsType = shippingDetailsType.getInsuranceDetails();
				if (insuranceDetailsType != null) {
					AmountType insuranceFee = insuranceDetailsType.getInsuranceFee();
					if (insuranceFee != null) {
						orderMaster.insurance = new BigDecimal(Double.toString(insuranceFee.getValue()));
						if (insuranceFee.getCurrencyID() != null) {
							orderMaster.insuranceCurrency = insuranceFee.getCurrencyID().value();
						}
					}
				}
			}

			AmountType total = order.getTotal();
			if (total != null) {
				orderMaster.total = new BigDecimal(Double.toString(total.getValue()));
				if (total.getCurrencyID() != null) {
					orderMaster.totalCurrency = total.getCurrencyID().value();
				}
			}

			AmountType subtotal = order.getSubtotal();
			if (subtotal != null) {
				orderMaster.subtotal = new BigDecimal(Double.toString(subtotal.getValue()));
				if (subtotal.getCurrencyID() != null) {
					orderMaster.subtotalCurrency = subtotal.getCurrencyID().value();
				}
			}

			if (order.getCreatedTime() != null) {
				orderMaster.createDate = order.getCreatedTime().getTime();
			}
			if (order.getShippedTime() != null) {
				orderMaster.shippedTime = order.getShippedTime().getTime();
			}

			orderMaster.fireAtDate = cronOrder.fireAt;
			if (order.getOrderStatus() != null) {
				orderMaster.orderStatus = order.getOrderStatus().value();
			}

			CheckoutStatusType checkoutStatusType = order.getCheckoutStatus();
			boolean isPaid = false;
			if (checkoutStatusType != null) {
				orderMaster.checkoutStatus = checkoutStatusType.getStatus().value();
				com.ebay.soap.eBLBaseComponents.BuyerPaymentMethodCodeType paymentMethod = checkoutStatusType
						.getPaymentMethod();
				if (paymentMethod != null) {
					orderMaster.paymentMethod = paymentMethod.value();
					isPaid = (order.getOrderStatus() == OrderStatusCodeType.SHIPPED
							|| order.getOrderStatus() == OrderStatusCodeType.COMPLETED)
							&& (checkoutStatusType.getStatus() == CompleteStatusCodeType.COMPLETE)
							// PayPalCredit,PayPal
							&& paymentMethod.value().toLowerCase().contains("paypal");
				}
				if (checkoutStatusType.getLastModifiedTime() != null) {
					orderMaster.lastModifiedTime = checkoutStatusType.getLastModifiedTime().getTime();
				}

				if (isPaid) {
					if (order.getMonetaryDetails() != null && order.getMonetaryDetails().getPayments() != null
							&& order.getMonetaryDetails().getPayments().getPayment() != null
							&& order.getMonetaryDetails().getPayments().getPayment().length > 0) {
						String paymentTransactionId = null;
						for (PaymentTransactionType paymentTransactionType : order.getMonetaryDetails().getPayments()
								.getPayment()) {
							if (paymentTransactionType.getReferenceID() != null) {
								paymentTransactionId = paymentTransactionType.getReferenceID().getValue();
								break;
							}
						}

							if (paymentTransactionId != null) {
								urn.ebay.apis.eBLBaseComponents.PaymentTransactionType paypalDetail = getPaypalTransactionDetail(
										cronOrder.paypalId, paymentTransactionId, order.getSellerEmail());
								if (paypalDetail == null) {
									EbayPaypal ebayPaypal = new EbayPaypal();
									ebayPaypal.transactionId = paymentTransactionId;
									ebayPaypal.paypalConfigId = cronOrder.paypalId;
									ebayPaypal.sellerEmail = order.getSellerEmail();
									paypalFailedList.add(ebayPaypal);
								} else {
									parsePaypalTransactionDetail(orderMaster, paypalDetail);
								}
							} else {
								orderMaster.paypalFailed = true;
								Logger.error("ebay自动进单,没有获取到paypalTransactionId: " + cronOrder.accountName
										+ "||orderId:" + order.getOrderID() + "||buyerId:" + order.getBuyerUserID()
										+ "||" + new Date());
							}

					}

				}
			}

			if (order.getPaidTime() != null) {
				orderMaster.paidTime = order.getPaidTime().getTime();
			}
			orderMaster.sellerEmail = order.getSellerEmail();
			orderMaster.sellerUserId = order.getSellerUserID();
			orderMaster.eiasToken = order.getEIASToken();
			orderMaster.integratedMerchantCreditCardEnabled = order.isIntegratedMerchantCreditCardEnabled();

			orderMaster.save();

			//////////////
			// monetary:payment+refund
			//////////////
			int monetaryCount = 0;
			if (order.getMonetaryDetails() != null) {
				// 付款信息,有的payment是国际运费,没有transactionId，像111298334709-1499793568001
				if (order.getMonetaryDetails().getPayments() != null
						&& order.getMonetaryDetails().getPayments().getPayment() != null) {
					for (PaymentTransactionType paymentTransactionType : order.getMonetaryDetails().getPayments()
							.getPayment()) {
						EbayMonetaryDetail monetary = new EbayMonetaryDetail();
						monetary.master = orderMaster;
						monetary.type = EbayMonetaryDetail.TYPE.PAYMENT;
						if (paymentTransactionType.getPaymentStatus() != null) {
							monetary.status = paymentTransactionType.getPaymentStatus().value();
						}
						if (paymentTransactionType.getPaymentTime() != null) {
							monetary.time = paymentTransactionType.getPaymentTime().getTime();
						}
						if (paymentTransactionType.getPaymentAmount() != null) {
							monetary.amount = new BigDecimal(
									Double.toString(paymentTransactionType.getPaymentAmount().getValue()));
							if (paymentTransactionType.getPaymentAmount().getCurrencyID() != null) {
								monetary.amountCurrency = paymentTransactionType.getPaymentAmount().getCurrencyID()
										.value();
							}
						}
						if (paymentTransactionType.getFeeOrCreditAmount() != null) {
							monetary.fee = new BigDecimal(
									Double.toString(paymentTransactionType.getFeeOrCreditAmount().getValue()));
							if (paymentTransactionType.getFeeOrCreditAmount().getCurrencyID() != null) {
								monetary.feeCurrency = paymentTransactionType.getFeeOrCreditAmount().getCurrencyID()
										.value();
							}
						}
						if (paymentTransactionType.getReferenceID() != null) {
							monetary.referenceId = paymentTransactionType.getReferenceID().getValue();
							if (paymentTransactionType.getReferenceID().getType() != null) {
								monetary.referenceIdType = paymentTransactionType.getReferenceID().getType().value();
							}
						}
						if (paymentTransactionType.getPayer() != null) {
							monetary.fromName = paymentTransactionType.getPayer().getValue();
							if (paymentTransactionType.getPayer().getType() != null) {
								monetary.fromType = paymentTransactionType.getPayer().getType().value();
							}
						}
						if (paymentTransactionType.getPayee() != null) {
							monetary.toName = paymentTransactionType.getPayee().getValue();
							if (paymentTransactionType.getPayee().getType() != null) {
								monetary.toType = paymentTransactionType.getPayee().getType().value();
							}
						}
						monetary.save();
						monetaryCount++;
					}

				}

				// 退款信息
				if (order.getMonetaryDetails().getRefunds() != null
						&& order.getMonetaryDetails().getRefunds().getRefund() != null) {
					for (RefundTransactionInfoType refundInfoType : order.getMonetaryDetails().getRefunds()
							.getRefund()) {
						EbayMonetaryDetail monetary = new EbayMonetaryDetail();
						monetary.master = orderMaster;
						monetary.type = EbayMonetaryDetail.TYPE.REFUND;
						if (refundInfoType.getRefundStatus() != null) {
							monetary.status = refundInfoType.getRefundStatus().value();
						}
						if (refundInfoType.getRefundTime() != null) {
							monetary.time = refundInfoType.getRefundTime().getTime();
						}
						if (refundInfoType.getRefundAmount() != null) {
							monetary.amount = new BigDecimal(
									Double.toString(refundInfoType.getRefundAmount().getValue()));
							if (refundInfoType.getRefundAmount().getCurrencyID() != null) {
								monetary.amountCurrency = refundInfoType.getRefundAmount().getCurrencyID().value();
							}
						}
						if (refundInfoType.getFeeOrCreditAmount() != null) {
							monetary.fee = new BigDecimal(
									Double.toString(refundInfoType.getFeeOrCreditAmount().getValue()));
							if (refundInfoType.getFeeOrCreditAmount().getCurrencyID() != null) {
								monetary.feeCurrency = refundInfoType.getFeeOrCreditAmount().getCurrencyID().value();
							}
						}
						if (refundInfoType.getReferenceID() != null) {
							monetary.referenceId = refundInfoType.getReferenceID().getValue();
							if (refundInfoType.getReferenceID().getType() != null) {
								monetary.referenceIdType = refundInfoType.getReferenceID().getType().value();
							}
						}
						if (refundInfoType.getRefundType() != null) {
							monetary.refundType = refundInfoType.getRefundType().value();
						}
						if (refundInfoType.getRefundTo() != null) {
							monetary.toName = refundInfoType.getRefundTo().getValue();
							if (refundInfoType.getRefundTo().getType() != null) {
								monetary.toType = refundInfoType.getRefundTo().getType().value();
							}
						}
						monetary.save();
						monetaryCount++;
					}
				}

			}

			//////////////
			// detail:transaction
			//////////////
			int detailCount = 0;
			if (order.getTransactionArray() != null) {
				TransactionType[] transactions = order.getTransactionArray().getTransaction();
				if (transactions != null) {
					for (TransactionType tc : transactions) {
						EbayOrderDetail detail = new EbayOrderDetail();
						detail.master = orderMaster;
						if (tc.getBuyer() != null) {
							detail.buyerEmail = tc.getBuyer().getEmail();
							if ("Invalid Request".equals(detail.buyerEmail)) {
								detail.buyerEmail = null;
							}
							detail.staticAlias = tc.getBuyer().getStaticAlias();
						}
						detail.transactionId = tc.getTransactionID();
						detail.isHandled = false;
						ItemType item = tc.getItem();
						if (item != null) {
							detail.itemId = item.getItemID();
							detail.title = item.getTitle();
							if (detail.title != null && detail.title.length() >= 2000) {
								detail.title = detail.title.substring(2000 - 1);
							}
							// sku在item或者variation里面
							if (item.getSKU() != null) {
								detail.sku = item.getSKU();
							} else if (tc.getVariation() != null) {
								detail.sku = tc.getVariation().getSKU();
							}
						}
						detail.quantity = tc.getQuantityPurchased();
						AmountType amount = tc.getTransactionPrice();
						if (amount != null) {
							detail.price = new BigDecimal(Double.toString(amount.getValue()));
							if (amount.getCurrencyID() != null) {
								detail.priceCurrency = amount.getCurrencyID().value();
							}
						}
						if (tc.getShippingDetails() != null) {
							com.ebay.soap.eBLBaseComponents.ShipmentTrackingDetailsType[] shipmentDetails = tc
									.getShippingDetails().getShipmentTrackingDetails();
							if (shipmentDetails != null && shipmentDetails.length > 0) {
								detail.shippingCarrier = shipmentDetails[0].getShippingCarrierUsed();
								detail.shippingTrackingNum = shipmentDetails[0].getShipmentTrackingNumber();
							}
							detail.salesRecordNumber=tc.getShippingDetails().getSellingManagerSalesRecordNumber();
						}
						if (tc.getTaxes() != null) {
							AmountType totalTax = tc.getTaxes().getTotalTaxAmount();
							if (totalTax != null) {
								detail.totalTaxAmount = new BigDecimal(Double.toString(totalTax.getValue()));
								if (totalTax.getCurrencyID() != null) {
									detail.totalTaxAmountCurrency = totalTax.getCurrencyID().value();
								}
							}

							if (tc.getTaxes().getTaxDetails() != null) {
								for (TaxDetailsType taxDetailsType : tc.getTaxes().getTaxDetails()) {
									if (taxDetailsType.getImposition() == null) {
										continue;
									}
									switch (taxDetailsType.getImposition()) {
									case SALES_TAX:
										if (taxDetailsType.getTaxAmount() != null) {
											detail.taxAmount = new BigDecimal(
													Double.toString(taxDetailsType.getTaxAmount().getValue()));
										}
										if (taxDetailsType.getTaxOnSubtotalAmount() != null) {
											detail.taxOnSubtotal = new BigDecimal(Double
													.toString(taxDetailsType.getTaxOnSubtotalAmount().getValue()));
										}
										if (taxDetailsType.getTaxOnShippingAmount() != null) {
											detail.taxOnShipping = new BigDecimal(Double
													.toString(taxDetailsType.getTaxOnShippingAmount().getValue()));
										}
										if (taxDetailsType.getTaxOnHandlingAmount() != null) {
											detail.taxOnHandling = new BigDecimal(Double
													.toString(taxDetailsType.getTaxOnHandlingAmount().getValue()));
										}
										break;
									case WASTE_RECYCLING_FEE:
										if (taxDetailsType.getTaxAmount() != null) {
											detail.wasteRecyclingFeeTaxAmount = new BigDecimal(
													Double.toString(taxDetailsType.getTaxAmount().getValue()));
										}
										break;
									default:
										break;
									}
								}
							}
						}
						if (tc.getActualHandlingCost() != null) {
							detail.actualHandlingCost = new BigDecimal(
									Double.toString(tc.getActualHandlingCost().getValue()));
							if (tc.getActualHandlingCost().getCurrencyID() != null) {
								detail.actualHandlingCostCurrency = tc.getActualHandlingCost().getCurrencyID().value();
							}
						}
						if (tc.getActualShippingCost() != null) {
							detail.actualShippingCost = new BigDecimal(
									Double.toString(tc.getActualShippingCost().getValue()));
							if (tc.getActualShippingCost().getCurrencyID() != null) {
								detail.actualShippingCostCurrency = tc.getActualShippingCost().getCurrencyID().value();
							}
						}
						if (tc.getShippingServiceSelected() != null
								&& tc.getShippingServiceSelected().getShippingPackageInfo() != null
								&& tc.getShippingServiceSelected().getShippingPackageInfoLength() > 0) {
							ShippingPackageInfoType shippingPackageInfoType = tc.getShippingServiceSelected()
									.getShippingPackageInfo(0);
							if (shippingPackageInfoType.getEstimatedDeliveryTimeMin() != null) {
								detail.estimatedDeliveryTimeMin = shippingPackageInfoType.getEstimatedDeliveryTimeMin()
										.getTime();
							}
							if (shippingPackageInfoType.getEstimatedDeliveryTimeMax() != null) {
								detail.estimatedDeliveryTimeMax = shippingPackageInfoType.getEstimatedDeliveryTimeMax()
										.getTime();
							}
						}

						detail.createAt = new Date();
						detail.save();
						detailCount++;
					}
				}
			}
			if (monetaryCount > 0 || detailCount > 0) {
				orderMaster.monetaryDetailNum = monetaryCount;
				orderMaster.orderDetailNum = detailCount;
				orderMaster.update();
			}

			// 保存原始数据
			try {
				String json = jsonObjectMapper.writeValueAsString(order);
				json = filter4BytesUTF8(json);
				saveOriginalDataToFile(json, orderMaster);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
				Logger.error("ebay自动进单,原始order数据转成json失败.||orderId:" + order.getOrderID() + "||buyerId:"
						+ order.getBuyerUserID());
			}

			// try {
			// EbayOriginalMaster originalData = new EbayOriginalMaster();
			// originalData.master = orderMaster;
			// OrderType mOrderType=order;
			// String json = jsonObjectMapper.writeValueAsString(order);
			// originalData.originalData = filter4BytesUTF8(json);
			// if(originalData.originalData.length()>=25000){
			//// originalData.originalData=originalData.originalData.substring(0,25000-1);
			// Logger.error("ebay自动进单,originalData太大.||orderId:" +
			// order.getOrderID() + "||buyerId:"
			// + order.getBuyerUserID());
			// }
			// originalData.save();
			// } catch (JsonProcessingException e) {
			// e.printStackTrace();
			// Logger.error("ebay自动进单,原始order数据转成json失败.||orderId:" +
			// order.getOrderID() + "||buyerId:"
			// + order.getBuyerUserID());
			// }

		}
	}

	private ApiContext getApiContent(String token) {
		ApiContext apiContext = new ApiContext();

		ApiCredential apiCredential = apiContext.getApiCredential();
		apiCredential.seteBayToken(token);
		// "https://api.sandbox.ebay.com/wsapi"
		apiContext.setApiServerUrl("https://api.ebay.com/wsapi");
		// 超时时间120秒
		apiContext.setTimeout(120000);

		return apiContext;
	}

	/**
	 * ebay限定使用modify time查询的最大范围是30天; 根据ebay的注释，不要获取当前时间最近2分钟的订单;
	 * 避免一次获取超过100条(分页)，时间范围最多取12个小时的
	 * 
	 * @author wq
	 */
	private Date getMaxAllowedEndDate(Date startDate) {
		Calendar now = Calendar.getInstance();
		Calendar aMonthLaterFromStartDate = Calendar.getInstance();
		aMonthLaterFromStartDate.setTime(startDate);
		aMonthLaterFromStartDate.add(Calendar.HOUR_OF_DAY, 12);
		if (now.after(aMonthLaterFromStartDate)) {
			return aMonthLaterFromStartDate.getTime();
		} else {
			return now.getTime();
		}
	}

	/**
	 * @param startDate
	 *            设置为上一次的前2分钟,参考http://www.ebay.cn/show.php?contentid=6171
	 */
	private void initGetOrderCallApiForModTime(GetOrdersCall apiCall, Date startDate, Date endDate,
			boolean inInnerCall) {
		DetailLevelCodeType[] level = new DetailLevelCodeType[] { DetailLevelCodeType.RETURN_ALL };
		apiCall.setDetailLevel(level);
		apiCall.setOrderRole(TradingRoleCodeType.SELLER);

		Calendar begin = Calendar.getInstance();
		Calendar end = Calendar.getInstance();
		begin.setTime(startDate);
		if (!inInnerCall) {
			begin.add(Calendar.MINUTE, -2);
		}
		end.setTime(endDate);

		apiCall.setModTimeFrom(begin);
		apiCall.setModTimeTo(end);
		apiCall.setSortingOrder(SortOrderCodeType.ASCENDING);
	}

	private void initGetOrderCallApiForCreateTime(GetOrdersCall apiCall, Date startDate, Date endDate) {
		DetailLevelCodeType[] level = new DetailLevelCodeType[] { DetailLevelCodeType.RETURN_ALL };
		apiCall.setDetailLevel(level);
		apiCall.setOrderRole(TradingRoleCodeType.SELLER);

		Calendar begin = Calendar.getInstance();
		Calendar end = Calendar.getInstance();
		begin.setTime(startDate);
		begin.add(Calendar.MINUTE, -2);

		end.setTime(endDate);

		apiCall.setCreateTimeFrom(begin);
		apiCall.setCreateTimeTo(end);
		apiCall.setSortingOrder(SortOrderCodeType.ASCENDING);
	}

	private void saveOriginalDataToFile(String originalData, EbayOrderMaster orderMaster) {
		SimpleDateFormat fileSdf = new SimpleDateFormat("yyyyMMdd");
		String rootPath = Play.application().configuration().getString("s3.file.path");
		// /ebay/20160530/
		String dirPath = rootPath + File.separator + "ebay" + File.separator + fileSdf.format(orderMaster.fireAtDate);
		File dir = new File(dirPath);
		if (!dir.exists()) {
			if (!dir.mkdirs()) {
				Logger.error("ebay自动进单保存原始数据时 无法创建文件夹:" + dir.getAbsolutePath() + "||" + new Date());
				return;
			}
		}
		String path = dirPath + File.separator + orderMaster.id + "_" + orderMaster.orderId;
		File file = new File(path);
		try (FileWriter fileWriter = new FileWriter(file)) {
			fileWriter.write(originalData);
		} catch (IOException e) {
			e.printStackTrace();
			Logger.error("ebay自动进单保存原始数据至文件出错," + e.toString());
		}

	}

	///////////////////////////////
	// 获取Paypal交易详情，主要是email
	///////////////////////////////
	public urn.ebay.apis.eBLBaseComponents.PaymentTransactionType getPaypalTransactionDetail(long paypalConfigId,
			String transactionId, String sellerEmail) {
		if (paypalConfig == null) {
			paypalConfig = PaypalConfig.find.byId(paypalConfigId);
		}
		GetTransactionDetailsResponseType paypalResponse = null;
		if (paypalConfig != null) {
			Map<String, String> map = new HashMap<>();
			map.put(Constants.MODE, Constants.LIVE);
			map.put("acct1.UserName", paypalConfig.username);
			map.put("acct1.Password", paypalConfig.password);
			map.put("acct1.Signature", paypalConfig.signature);
			map.put("http.Retry", "3");
			map.put("http.ConnectionTimeOut", "10000");
			map.put("http.ReadTimeOut", "30000");

			try {
				paypalResponse = getTransactionDetailsResponse(transactionId, map);
			} catch (Exception e) {
				try {
					paypalResponse = getTransactionDetailsResponse(transactionId, map);
				} catch (Exception e1) {
					e1.printStackTrace();
					Logger.info("ebay自动进单:调用paypal接口获取交易详情出现异常||" + sdf.format(new Date()) + "||transactionId:"
							+ transactionId);
				}
			}
		}
		if (paypalResponse != null) {
			if (paypalResponse.getAck() == AckCodeType.SUCCESS) {
				return paypalResponse.getPaymentTransactionDetails();
			} else {
				if (paypalResponse.getErrors() != null) {
					for (ErrorType errorType : paypalResponse.getErrors()) {
						Logger.error("ebay自动进单解析:paypal第一次调用出错,transactionId:" + transactionId + ",errorCode:"
								+ errorType.getErrorCode() + "||" + errorType.getLongMessage() + "||paypalConfigId:"
								+ paypalConfigId + "||paypal username:" + paypalConfig.username);

						// You do not have permission to get the details of this
						// transaction
						// 应该是账号变了的原因，换个账号试试
						if ("10007".equals(errorType.getErrorCode())) {
							PaypalConfig paypalConfig = PaypalConfig.find.where().eq("account", sellerEmail)
									.orderBy("id desc").setMaxRows(1).findUnique();
							if (paypalConfig != null && paypalConfig.id != paypalConfigId) {
								urn.ebay.apis.eBLBaseComponents.PaymentTransactionType anotherPaypalDetail = getPaypalTransactionDetailAsAnotherAccount(
										paypalConfig, transactionId, sellerEmail);
								if (anotherPaypalDetail != null) {
									return anotherPaypalDetail;
								}
							}
							break;
						}
					}
				}
				Logger.error("ebay自动进单解析:paypal账号有问题,transactionId:" + transactionId + ",paypal_username:"
						+ paypalConfig.username);
			}
		}

		return null;
	}

	// 换paypal账号试试
	private urn.ebay.apis.eBLBaseComponents.PaymentTransactionType getPaypalTransactionDetailAsAnotherAccount(
			PaypalConfig paypalConfig, String transactionId, String sellerEmail) {
		GetTransactionDetailsResponseType paypalResponse = null;
		if (paypalConfig != null) {
			Map<String, String> map = new HashMap<>();
			map.put(Constants.MODE, Constants.LIVE);
			map.put("acct1.UserName", paypalConfig.username);
			map.put("acct1.Password", paypalConfig.password);
			map.put("acct1.Signature", paypalConfig.signature);
			map.put("http.Retry", "3");
			map.put("http.ConnectionTimeOut", "10000");
			map.put("http.ReadTimeOut", "30000");

			try {
				paypalResponse = getTransactionDetailsResponse(transactionId, map);
			} catch (Exception e) {
				try {
					paypalResponse = getTransactionDetailsResponse(transactionId, map);
				} catch (Exception e1) {
					e1.printStackTrace();
					Logger.info("ebay自动进单:调用paypal接口第二次换了账号获取详情依然失败||" + sdf.format(new Date()) + "||transactionId:"
							+ transactionId);
				}
			}
		}
		if (paypalResponse != null) {
			if (paypalResponse.getAck() == AckCodeType.SUCCESS) {
				return paypalResponse.getPaymentTransactionDetails();
			} else if (paypalResponse.getErrors() != null) {
				Logger.error("ebay自动进单解析:paypal第二次换了账号依然有问题,transactionId:" + transactionId + ",paypal_username:"
						+ paypalConfig.username);
				for (ErrorType errorType : paypalResponse.getErrors()) {
					Logger.error("ebay自动进单解析:paypal第二次换账号调用出错:errorCode:" + errorType.getErrorCode() + "||"
							+ errorType.getLongMessage());
				}
			}
		}
		return null;
	}

	private GetTransactionDetailsResponseType getTransactionDetailsResponse(String transactionId,
			Map<String, String> map) throws Exception {
		PayPalAPIInterfaceServiceService service = new PayPalAPIInterfaceServiceService(map);
		GetTransactionDetailsReq req = new GetTransactionDetailsReq();
		GetTransactionDetailsRequestType getTransactionDetailsRequest = new GetTransactionDetailsRequestType();
		getTransactionDetailsRequest.setTransactionID(transactionId);
		req.setGetTransactionDetailsRequest(getTransactionDetailsRequest);
		GetTransactionDetailsResponseType transactionDetailsResponseType = service.getTransactionDetails(req);
		return transactionDetailsResponseType;
	}

	public void parsePaypalTransactionDetail(EbayOrderMaster orderMaster,
			urn.ebay.apis.eBLBaseComponents.PaymentTransactionType paypalDetail) {
		if (paypalDetail != null) {
			urn.ebay.apis.eBLBaseComponents.PayerInfoType payerInfo = paypalDetail.getPayerInfo();
			if (payerInfo != null) {
				orderMaster.paypalEmail = payerInfo.getPayer();
			}
			urn.ebay.apis.eBLBaseComponents.PaymentInfoType info = paypalDetail.getPaymentInfo();
			if (info != null) {
				if (info.getPaymentStatus() != null) {
					orderMaster.paypalStatus = info.getPaymentStatus().getValue();
				}
				if (info.getPaymentDate() != null) {
					try {
						SimpleDateFormat paypalSdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
						// 返回的是标准时间，需要和其它的时间统一成北京时间
						paypalSdf.setTimeZone(TimeZone.getTimeZone("GMT"));

						orderMaster.paypalDate = paypalSdf.parse(info.getPaymentDate());
					} catch (ParseException e) {
					}
				}
			}
		}
	}
	///////////////////////////////

}
