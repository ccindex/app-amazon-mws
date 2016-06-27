package amazon.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.XML;

import play.libs.Json;
import amazon.enums.order.OrderStatus;
import amazon.models.order.AmazonOrder;
import amazon.models.order.AmazonOrderItem;
import amazon.models.report.AmazonReport;
import amazon.models.report.AmazonReport.Status;
import amazon.models.report.AmazonReportOrderSnapshot;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.EbeanServer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;

/**
 * Title: OrderKit.java
 *
 * @author Lc
 *
 * @date 2016年4月21日 下午2:18:36
 */
public class OrderKit {

	public static void snapshot(AmazonReport report) {
		List<AmazonReportOrderSnapshot> aosList = null;
		BufferedReader br = null;
		InputStreamReader isr = null;
		
		try {
			File file = new File(report.path);

			String charsetName = "UTF-8";
			if (report.appName.indexOf("JP") != -1) {
				charsetName = GlobalKit.getEncode("JP");
			} else {
				charsetName = GlobalKit.getEncode("UTF-8");
			}
			
			isr = new InputStreamReader(new FileInputStream(file), charsetName);
			br = new BufferedReader(isr);
			String line = null;
			if ((line = br.readLine()) != null) {
				aosList = new ArrayList<>();
				while ((line = br.readLine()) != null) {
					AmazonReportOrderSnapshot aos = parse(line);
					if (aos != null) {
						aos.reportId = report.id;
						aosList.add( aos );
					}
				}
			}
			if (aosList != null && aosList.size() != 0) {
				try {
					Ebean.beginTransaction();
					
					Ebean.save(aosList);
					
					report.status = Status.订单记录;
					Ebean.update(report);
					
					Ebean.commitTransaction();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					Ebean.endTransaction();
				}
				
			} else {
				report.status = Status.数据异常;
				Ebean.update(report);
			}
		} catch (Exception e) {
			APLogger.error("OrderKit.snapshot() - path: " + report.path, e);
		} finally {
			try {
				if (isr != null) {
					isr.close();
				}
				if (br != null){
					br.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static AmazonReportOrderSnapshot parse(String content) throws Exception {
		AmazonReportOrderSnapshot aos = null;
		
		if (content != null && !"".equals(content)) {
			String[] order = content.split("\t", -1);

			if (order.length > 30 && order.length < 33) {
				throw new RuntimeException("OrderKit.parse() - 数据列数异常，有效列数为30，实际列数为: " + order.length);
			}
			
			aos = new AmazonReportOrderSnapshot();
			aos.amazonOrderId = order[0];
			aos.merchantOrderId = order[1];
			aos.purchaseDate = order[2];
			aos.lastUpdatedDate = order[3];
			aos.orderStatus = order[4];
			aos.fulfillmentChannel = order[5];
			aos.salesChannel = order[6];
			aos.orderChannel = order[7];
			aos.url = order[8];
			aos.shipServiceLevel = order[9];
			aos.productName = order[10];
			aos.sku = order[11];
			aos.asin = order[12];
			aos.itemStatus = order[13];
			aos.quantity = order[14];
			aos.currency = order[15];
			aos.itemPrice = order[16];
			aos.itemTax = order[17];
			aos.shippingPrice = order[18];
			aos.shippingTax = order[19];
			aos.giftWrapPrice = order[20];
			aos.giftWrapTax = order[21];
			aos.itemPromotionDiscount = order[22];
			aos.shipPromotionDiscount = order[23];
			aos.shipCity = order[24];
			aos.shipState = order[25];
			aos.shipPostalCode = order[26];
			aos.shipCountry = order[27];
			aos.promotionIds = order[28];
			
			if (Ebean.find(AmazonReportOrderSnapshot.class).select("id").where().eq("amazonOrderId", aos.amazonOrderId).eq("orderStatus", aos.orderStatus).eq("sku", aos.sku).findList().size() > 0) {
				return null;
			}
		}

		return aos;
	}
	
	// ------------------------------------------------------------------------------
	
	public static String toValue(JsonNode jsonNode, String key) {
		if (jsonNode == null)
			return null;
		
		if (jsonNode.get(key) == null)
			return null;
		
		if (jsonNode.get(key).isTextual())
			return jsonNode.get(key).asText();
		
		return jsonNode.get(key).toString();
	}
	
	public static JsonNode toJson(String xml) throws Exception {
		return Json.parse( XML.toJSONObject(xml).toString() );
	}
	
	public static String toOrder(String response, String appName) {
		String nextToken = null;

		List<AmazonOrder> aoList = new ArrayList<AmazonOrder>();
		try {
			APLogger.debug("response: " + response);

			JsonNode resultJson = null;
			
			JsonNode jsonNode = toJson(response);
			if (jsonNode.get("ListOrdersResponse") != null) {
				resultJson = jsonNode.get("ListOrdersResponse").get("ListOrdersResult");
			} else if (jsonNode.get("ListOrdersByNextTokenResponse") != null) {
				resultJson = jsonNode.get("ListOrdersByNextTokenResponse").get("ListOrdersByNextTokenResult");
			} else if (jsonNode.get("GetOrderResponse") != null) {
				resultJson = jsonNode.get("GetOrderResponse").get("GetOrderResult");
			} else {
				throw new RuntimeException("jsonNode.get is null");
			}

			nextToken = toValue(resultJson, "NextToken");

			APLogger.debug("jsonNode: " + jsonNode.toString());
			APLogger.debug("nextToken: " + nextToken);
			
			if (resultJson.get("Orders") == null) {
				return null;
			}
			
			JsonNode orders = resultJson.get("Orders").get("Order");
			if (JsonNodeType.ARRAY.equals(orders.getNodeType())) {
				Iterator<JsonNode> it = orders.iterator();
				while (it.hasNext()) {
					JsonNode orderJson = it.next();

					String amazonOrderId = toValue(orderJson, "AmazonOrderId");
					String orderStatus = toValue(orderJson, "OrderStatus");
					if (amazonOrderId == null || orderStatus == null) {
						APLogger.error(orderJson.toString());
						continue;
					}
					if (Ebean.find(AmazonOrder.class).select("id").where().eq("amazonOrderId", amazonOrderId).eq("orderStatus", orderStatus).findUnique() != null) {
						continue;
					}
					
					AmazonOrder order = getOrder(orderJson);
					order.appName = appName;
					aoList.add(order);
				}
			} else {
				JsonNode orderJson = orders;
				AmazonOrder order = getOrder(orderJson);
				order.appName = appName;
				aoList.add(order);
			}
			
			if (aoList.size() > 0) {
				Ebean.save(aoList);
			}
		} catch (Exception e) {
			APLogger.error(response);
			APLogger.error("OrderKit.toOrder()", e);
		}
		
		return nextToken;
	}
	
	public static AmazonOrder getOrder(JsonNode orderJson) {
		AmazonOrder order = new AmazonOrder();
		
		order.amazonOrderId = toValue(orderJson, "AmazonOrderId");
		order.lastUpdateDate = toValue(orderJson, "LastUpdateDate");
		order.sellerOrderId = toValue(orderJson, "SellerOrderId");
		order.purchaseDate = toValue(orderJson, "PurchaseDate");
		order.orderStatus = toValue(orderJson, "OrderStatus");
		order.fulfillmentChannel = toValue(orderJson, "FulfillmentChannel");
		order.salesChannel = toValue(orderJson, "SalesChannel");
		order.orderChannel = toValue(orderJson, "OrderChannel");
		order.shipServiceLevel = toValue(orderJson, "ShipServiceLevel");
		
		// 类型：Address
		JsonNode shippingAddress = orderJson.get("ShippingAddress");
		order.name = toValue(shippingAddress, "Name");
		order.addressLine1 = toValue(shippingAddress, "AddressLine1");
		order.addressLine2 = toValue(shippingAddress, "AddressLine2");
		order.addressLine3 = toValue(shippingAddress, "AddressLine3");
		order.city = toValue(shippingAddress, "City");
		order.county = toValue(shippingAddress, "County");
		order.district = toValue(shippingAddress, "District");
		order.stateOrRegion = toValue(shippingAddress, "StateOrRegion");
		order.postalCode = toValue(shippingAddress, "PostalCode");
		order.countryCode = toValue(shippingAddress, "CountryCode");
		order.phone = toValue(shippingAddress, "Phone");
		
		// 类型：Money
		JsonNode orderTotal = orderJson.get("OrderTotal");
		order.currencyCode = toValue(orderTotal, "CurrencyCode");
		order.amount = toValue(orderTotal, "Amount");
		
		order.numberOfItemsShipped = toValue(orderJson, "NumberOfItemsShipped");
		order.numberOfItemsUnshipped = toValue(orderJson, "NumberOfItemsUnshipped");
		order.paymentExecutionDetail = toValue(orderJson, "PaymentExecutionDetail");
		order.paymentMethod = toValue(orderJson, "PaymentMethod");
		order.marketplaceId = toValue(orderJson, "MarketplaceId");
		order.buyerEmail = toValue(orderJson, "BuyerEmail");
		order.buyerName = toValue(orderJson, "BuyerName");
		order.shipmentServiceLevelCategory = toValue(orderJson, "ShipmentServiceLevelCategory");
		order.shippedByAmazonTFM = toValue(orderJson, "ShippedByAmazonTFM");
		order.tfmShipmentStatus = toValue(orderJson, "TFMShipmentStatus");
		order.cbaDisplayableShippingLabel = toValue(orderJson, "CbaDisplayableShippingLabel");
		order.orderType = toValue(orderJson, "OrderType");
		order.earliestShipDate = toValue(orderJson, "EarliestShipDate");
		order.latestShipDate = toValue(orderJson, "LatestShipDate");
		order.earliestDeliveryDate = toValue(orderJson, "EarliestDeliveryDate");
		order.latestDeliveryDate = toValue(orderJson, "LatestDeliveryDate");
		order.isBusinessOrder = toValue(orderJson, "IsBusinessOrder");
		order.isPremiumOrder = toValue(orderJson, "IsPremiumOrder");
		order.isPrime = toValue(orderJson, "IsPrime");
		order.mwsResponse = orderJson.toString();
		
		return order;
	}
	
	public static String toOrderItem(String response, AmazonOrder ao) {
		String nextToken = null;
		
		List<AmazonOrderItem> aoiList = new ArrayList<AmazonOrderItem>();
		try {
			APLogger.debug("response: " + response);
			
			JsonNode resultJson = null;
			
			JsonNode jsonNode = toJson(response);
			if (jsonNode.get("ListOrderItemsResponse") != null) {
				resultJson = jsonNode.get("ListOrderItemsResponse").get("ListOrderItemsResult");
			}

			nextToken = toValue(resultJson, "NextToken");
			
			APLogger.debug("jsonNode: " + jsonNode.toString());
			APLogger.debug("nextToken: " + nextToken);
			
			JsonNode items = resultJson.get("OrderItems").get("OrderItem");
			if (JsonNodeType.ARRAY.equals(items.getNodeType())) {
				Iterator<JsonNode> it = items.iterator();
				while (it.hasNext()) {
					JsonNode itemJson = it.next();
					AmazonOrderItem aoi = getItem(itemJson);
					aoi.orderId = ao.id;
					aoiList.add(aoi);
				}
			} else {
				JsonNode itemJson = items;
				AmazonOrderItem aoi = getItem(itemJson);
				aoi.orderId = ao.id;
				aoiList.add(aoi);
			}
			
			if (aoiList != null && aoiList.size() > 0) {
				try {
					Ebean.beginTransaction();
					
					Ebean.save(aoiList);
					
					ao.status = OrderStatus.商品信息;
					Ebean.update(ao);
					
					Ebean.commitTransaction();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					Ebean.endTransaction();
				}
			} else {
				ao.status = OrderStatus.数据异常;
				Ebean.update(ao);
			}
		} catch (Exception e) {
			APLogger.error(response);
			APLogger.error("OrderKit.toOrderItem()", e);
		}
		
		return nextToken;
	}
	
	public static AmazonOrderItem getItem(JsonNode itemJson) {
		AmazonOrderItem aoi = new AmazonOrderItem();
		
		aoi.asin = toValue(itemJson, "ASIN");
		aoi.sellerSKU = toValue(itemJson, "SellerSKU");
		aoi.orderItemId = toValue(itemJson, "OrderItemId");
		aoi.title = toValue(itemJson, "Title");
		aoi.quantityOrdered = toValue(itemJson, "QuantityOrdered");
		aoi.quantityShipped = toValue(itemJson, "QuantityShipped");

		// 类型： Money（ItemPrice）
		JsonNode ItemPrice = itemJson.get("ItemPrice");
		aoi.itemPriceCurrencyCode = toValue(ItemPrice, "CurrencyCode");
		aoi.itemPriceAmount = toValue(ItemPrice, "Amount");

		// 类型： Money（ShippingPrice）
		JsonNode ShippingPrice = itemJson.get("ShippingPrice");
		aoi.shippingPriceCurrencyCode = toValue(ShippingPrice, "CurrencyCode");
		aoi.shippingPriceAmount = toValue(ShippingPrice, "Amount");

		// 类型： Money（GiftWrapPrice）
		JsonNode GiftWrapPrice = itemJson.get("GiftWrapPrice");
		aoi.giftWrapPriceCurrencyCode = toValue(GiftWrapPrice, "CurrencyCode");
		aoi.giftWrapPriceAmount = toValue(GiftWrapPrice, "Amount");

		// 类型： Money（ItemTax）
		JsonNode ItemTax = itemJson.get("ItemTax");
		aoi.itemTaxCurrencyCode = toValue(ItemTax, "CurrencyCode");
		aoi.itemTaxAmount = toValue(ItemTax, "Amount");

		// 类型： Money（ShippingTax）
		JsonNode ShippingTax = itemJson.get("ShippingTax");
		aoi.shippingTaxCurrencyCode = toValue(ShippingTax, "CurrencyCode");
		aoi.shippingTaxAmount = toValue(ShippingTax, "Amount");

		// 类型： Money（GiftWrapTax）
		JsonNode GiftWrapTax = itemJson.get("GiftWrapTax");
		aoi.giftWrapTaxCurrencyCode = toValue(GiftWrapTax, "CurrencyCode");
		aoi.giftWrapTaxAmount = toValue(GiftWrapTax, "Amount");

		// 类型：Money（ShippingDiscount）
		JsonNode ShippingDiscount = itemJson.get("ShippingDiscount");
		aoi.shippingDiscountCurrencyCode = toValue(ShippingDiscount, "CurrencyCode");
		aoi.shippingDiscountAmount = toValue(ShippingDiscount, "Amount");

		// 类型：Money（PromotionDiscount）
		JsonNode PromotionDiscount = itemJson.get("PromotionDiscount");
		aoi.promotionDiscountCurrencyCode = toValue(PromotionDiscount, "CurrencyCode");
		aoi.promotionDiscountAmount = toValue(PromotionDiscount, "Amount");

		aoi.promotionIds = toValue(itemJson, "PromotionIds");

		// 类型：Money（CODFee）
		JsonNode CODFee = itemJson.get("CODFee");
		aoi.codFeeCurrencyCode = toValue(CODFee, "CurrencyCode");
		aoi.codFeeAmount = toValue(CODFee, "Amount");

		// 类型：Money（CODFeeDiscount）
		JsonNode CODFeeDiscount = itemJson.get("CODFeeDiscount");
		aoi.codFeeDiscountCurrencyCode = toValue(CODFeeDiscount, "CurrencyCode");
		aoi.codFeeDiscountAmount = toValue(CODFeeDiscount, "Amount");

		aoi.giftMessageText = toValue(itemJson, "GiftMessageText");
		aoi.giftWrapLevel = toValue(itemJson, "GiftWrapLevel");

		// 类型：InvoiceData（InvoiceData）发票信息（仅适用于中国）
		JsonNode InvoiceData = itemJson.get("InvoiceData");
		aoi.invoiceDataInvoiceRequirement = toValue(InvoiceData, "InvoiceRequirement");
		aoi.invoiceDataBuyerSelectedInvoiceCategory = toValue(InvoiceData, "BuyerSelectedInvoiceCategory");
		aoi.invoiceDataInvoiceTitle = toValue(InvoiceData, "InvoiceTitle");
		aoi.invoiceDataInvoiceInformation = toValue(InvoiceData, "InvoiceInformation");

		aoi.conditionNote = toValue(itemJson, "ConditionNote");
		aoi.conditionId = toValue(itemJson, "ConditionId");
		aoi.conditionSubtypeId = toValue(itemJson, "ConditionSubtypeId");
		aoi.scheduledDeliveryStartDate = toValue(itemJson, "ScheduledDeliveryStartDate");
		aoi.scheduledDeliveryEndDate = toValue(itemJson, "ScheduledDeliveryEndDate");
		
		aoi.mwsResponse = itemJson.toString();
		
		return aoi;
	}
	
	// ------------------------------------------------------------------------------
	
	private static EbeanServer es = null;
	
	public static EbeanServer getERPEbeanServer() {
		if (es == null) {
			es = Ebean.getServer("erp");
		}
		return es;
	}
	
	public static void pushToERP() throws Exception {
		es = getERPEbeanServer();
		if (es == null) {
			throw new APException("OrderKit.pushToERP() - ERP Database connection failed.");
		}
		
		try {
			Ebean.beginTransaction();
			
			List<amazon.erp.models.AmazonOrder> _aoList = null;
			List<amazon.erp.models.AmazonOrderItem> _aoiList = null;
			
			List<AmazonOrder> aoList = checkDuplicateEntryForOrder();
			if (aoList != null && aoList.size() > 0) {
				_aoList = new ArrayList<>();
				for (AmazonOrder ao : aoList) {
					ao.isHandle = true;
					Ebean.update(ao);
					
					amazon.erp.models.AmazonOrder _ao = ao.cp();
					_aoList.add(_ao);
				}
				es.save(_aoList);
			}
			
			List<AmazonOrderItem> aoiList = checkDuplicateEntryForItem();
			if (aoiList != null && aoiList.size() > 0) {
				_aoiList = new ArrayList<>();
				for (AmazonOrderItem aoi : aoiList) {
					aoi.isHandle = true;
					Ebean.update(aoi);
					
					amazon.erp.models.AmazonOrderItem _aoi = aoi.cp();
					_aoiList.add(_aoi);
				}
				es.save(_aoiList);
			}

			Ebean.commitTransaction();
		} catch (Exception e) {
			APLogger.error("OrderKit.pushToERP()", e);
		} finally {
			Ebean.endTransaction();
		}
	}
	
	private static List<AmazonOrder> checkDuplicateEntryForOrder() throws Exception {
		List<AmazonOrder> aoList = Ebean.find(AmazonOrder.class).select("id").where().eq("status", OrderStatus.商品信息).eq("isHandle", false).setMaxRows(800).findList();
		if (aoList != null && aoList.size() > 0) {
			List<Long> idList = new ArrayList<Long>();
			for (AmazonOrder ao : aoList) {
				idList.add(ao.id);
			}
			
			List<amazon.erp.models.AmazonOrder> _aoList = es.find(amazon.erp.models.AmazonOrder.class).select("id").where().in("id", idList).findList();
			if (_aoList != null && _aoList.size() >0) {
				for (amazon.erp.models.AmazonOrder _ao : _aoList) {
					AmazonOrder ao = Ebean.find(AmazonOrder.class, _ao.id);
					ao.isHandle = true;
					Ebean.update(ao);
					
					idList.remove(_ao.id);
				}
			}
			
			if (idList.size() > 0) {
				aoList = null;
				aoList = Ebean.find(AmazonOrder.class).where().eq("status", OrderStatus.商品信息).eq("isHandle", false).in("id", idList).setMaxRows(800).findList();
			}
		}
		
		return aoList;
	}
	
	private static List<AmazonOrderItem> checkDuplicateEntryForItem() throws Exception {
		List<AmazonOrderItem> aoiList = Ebean.find(AmazonOrderItem.class).select("id").where().eq("isHandle", false).setMaxRows(1000).findList();
		if (aoiList != null && aoiList.size() > 0) {
			List<Long> idList = new ArrayList<Long>();
			for (AmazonOrderItem aoi : aoiList) {
				idList.add(aoi.id);
			}
			
			List<amazon.erp.models.AmazonOrderItem> _aoiList = es.find(amazon.erp.models.AmazonOrderItem.class).select("id").where().in("id", idList).findList();
			if (_aoiList != null && _aoiList.size() > 0) {
				for (amazon.erp.models.AmazonOrderItem _aoi : _aoiList) {
					AmazonOrderItem aoi = Ebean.find(AmazonOrderItem.class, _aoi.id);
					aoi.isHandle = true;
					Ebean.update(aoi);
					
					idList.remove(_aoi.id);
				}
			}
			
			if (idList.size() > 0) {
				aoiList = null;
				aoiList = Ebean.find(AmazonOrderItem.class).where().eq("isHandle", false).in("id", idList).setMaxRows(1000).findList();
			}
		}
		
		return aoiList;
	}
	
}
