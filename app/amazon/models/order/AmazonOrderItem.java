package amazon.models.order;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Version;

import play.data.format.Formats;
import play.db.ebean.Model;

/**
 * Title: 订单商品信息
 *
 * @author Lc
 *
 * @date 2016年4月27日 下午5:46:51
 */
@Entity
public class AmazonOrderItem extends Model {

	@Id
	public Long id;
	
	public String asin;
	public String sellerSKU;
	public String orderItemId;
	@Column(length=2000)
	public String title;
	public String quantityOrdered;
	public String quantityShipped;

	// 类型： Money（ItemPrice）
	public String itemPriceCurrencyCode;
	public String itemPriceAmount;
	
	// 类型： Money（ShippingPrice）
	public String shippingPriceCurrencyCode;
	public String shippingPriceAmount;
	
	// 类型： Money（GiftWrapPrice）
	public String giftWrapPriceCurrencyCode;
	public String giftWrapPriceAmount;
	
	// 类型： Money（ItemTax）
	public String itemTaxCurrencyCode;
	public String itemTaxAmount;
	
	// 类型： Money（ShippingTax）
	public String shippingTaxCurrencyCode;
	public String shippingTaxAmount;
	
	// 类型： Money（GiftWrapTax）
	public String giftWrapTaxCurrencyCode;
	public String giftWrapTaxAmount;
	
	// 类型：Money（ShippingDiscount）
	public String shippingDiscountCurrencyCode;
	public String shippingDiscountAmount;
	
	// 类型：Money（PromotionDiscount）
	public String promotionDiscountCurrencyCode;
	public String promotionDiscountAmount;
	
	public String promotionIds;
	
	// 类型：Money（CODFee）
	public String codFeeCurrencyCode;
	public String codFeeAmount;
	
	// 类型：Money（CODFeeDiscount）
	public String codFeeDiscountCurrencyCode;
	public String codFeeDiscountAmount;
	
	public String giftMessageText;
	public String giftWrapLevel;
	
	// 类型：InvoiceData（InvoiceData）发票信息（仅适用于中国）
	public String invoiceDataInvoiceRequirement;
	public String invoiceDataBuyerSelectedInvoiceCategory;
	public String invoiceDataInvoiceTitle;
	public String invoiceDataInvoiceInformation;
	
	public String conditionNote;
	public String conditionId;
	public String conditionSubtypeId;
	public String scheduledDeliveryStartDate;
	public String scheduledDeliveryEndDate;
	
	@Column(length=2000, nullable=false)
	public String mwsResponse;
	
	@Column(nullable=false)
	public Long orderId;
	
	public boolean isHandle = false;

	@Formats.DateTime(pattern="yyyy-MM-dd HH:mm:ss")
	public Date createdAt = new Date();
	
	@Version
	public Date updatedAt;

	public amazon.erp.models.AmazonOrderItem cp() {
		amazon.erp.models.AmazonOrderItem aoi = new amazon.erp.models.AmazonOrderItem();
		aoi.id = this.id;
		aoi.asin = this.asin;
		aoi.sellerSKU = this.sellerSKU;
		aoi.orderItemId = this.orderItemId;
		aoi.title = this.title;
		aoi.quantityOrdered = this.quantityOrdered;
		aoi.quantityShipped = this.quantityShipped;
		aoi.itemPriceCurrencyCode = this.itemPriceCurrencyCode;
		aoi.itemPriceAmount = this.itemPriceAmount;
		aoi.shippingPriceCurrencyCode = this.shippingPriceCurrencyCode;
		aoi.shippingPriceAmount = this.shippingPriceAmount;
		aoi.giftWrapPriceCurrencyCode = this.giftWrapPriceCurrencyCode;
		aoi.giftWrapPriceAmount = this.giftWrapPriceAmount;
		aoi.itemTaxCurrencyCode = this.itemTaxCurrencyCode;
		aoi.itemTaxAmount = this.itemTaxAmount;
		aoi.shippingTaxCurrencyCode = this.shippingTaxCurrencyCode;
		aoi.shippingTaxAmount = this.shippingTaxAmount;
		aoi.giftWrapTaxCurrencyCode = this.giftWrapTaxCurrencyCode;
		aoi.giftWrapTaxAmount = this.giftWrapTaxAmount;
		aoi.shippingDiscountCurrencyCode = this.shippingDiscountCurrencyCode;
		aoi.shippingDiscountAmount = this.shippingDiscountAmount;
		aoi.promotionDiscountCurrencyCode = this.promotionDiscountCurrencyCode;
		aoi.promotionDiscountAmount = this.promotionDiscountAmount;
		aoi.promotionIds = this.promotionIds;
		aoi.codFeeCurrencyCode = this.codFeeCurrencyCode;
		aoi.codFeeAmount = this.codFeeAmount;
		aoi.codFeeDiscountCurrencyCode = this.codFeeDiscountCurrencyCode;
		aoi.codFeeDiscountAmount = this.codFeeDiscountAmount;
		aoi.giftMessageText = this.giftMessageText;
		aoi.giftWrapLevel = this.giftWrapLevel;
		aoi.invoiceDataInvoiceRequirement = this.invoiceDataInvoiceRequirement;
		aoi.invoiceDataBuyerSelectedInvoiceCategory = this.invoiceDataBuyerSelectedInvoiceCategory;
		aoi.invoiceDataInvoiceTitle = this.invoiceDataInvoiceTitle;
		aoi.invoiceDataInvoiceInformation = this.invoiceDataInvoiceInformation;
		aoi.conditionNote = this.conditionNote;
		aoi.conditionId = this.conditionId;
		aoi.conditionSubtypeId = this.conditionSubtypeId;
		aoi.scheduledDeliveryStartDate = this.scheduledDeliveryStartDate;
		aoi.scheduledDeliveryEndDate = this.scheduledDeliveryEndDate;
		aoi.mwsResponse = this.mwsResponse;
		aoi.orderId = this.orderId;
		return aoi;
	}
	
}
