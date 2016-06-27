package amazon.models.order;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Version;

import amazon.enums.order.OrderStatus;
import play.data.format.Formats;
import play.db.ebean.Model;

/**
 * Title: 订单详情
 *
 * @author Lc
 *
 * @date 2016年4月26日 下午5:56:35
 */
@Entity
public class AmazonOrder extends Model {

	@Id
	public Long id;
	
	@Column(nullable=false)
	public String amazonOrderId;
	public String sellerOrderId;
	public String purchaseDate;
	@Column(nullable=false)
	public String lastUpdateDate;
	public String orderStatus;
	public String fulfillmentChannel;
	public String salesChannel;
	public String orderChannel;
	public String shipServiceLevel;
	
	// 类型：Address（ShippingAddress）
	public String name;         // 名称
	public String addressLine1; // 街道地址
	public String addressLine2; // 其他街道地址信息（如果需要）
	public String addressLine3; // 其他街道地址信息（如果需要）
	public String city;         // 城市
	public String county;       // 区县
	public String district;     // 区
	public String stateOrRegion;// 省/自治区/直辖市或地区
	public String postalCode;   // 邮政编码
	public String countryCode;  // 两位数国家/地区代码。格式为 ISO 3166-1-alpha 2 
	public String phone;        // 电话号码
	
	// 类型：Money（OrderTotal）
	public String currencyCode; // 三位数的货币代码。格式为 ISO 4217
	public String amount;       // 货币金额
	
	public String numberOfItemsShipped;
	public String numberOfItemsUnshipped;
	
	// 类型： PaymentExecutionDetailItem 列表	
	public String paymentExecutionDetail;
	
	public String paymentMethod;
	public String marketplaceId;
	public String buyerEmail;
	public String buyerName;
	public String shipmentServiceLevelCategory;
	public String shippedByAmazonTFM;
	public String tfmShipmentStatus;
	public String cbaDisplayableShippingLabel;
	public String orderType;
	public String earliestShipDate;
	public String latestShipDate;
	public String earliestDeliveryDate;
	public String latestDeliveryDate;
	public String isBusinessOrder;
	public String isPremiumOrder;
	public String isPrime;
	
	@Column(length=2000, nullable=false)
	public String mwsResponse;
	
	@Column(nullable=false)
	public String appName;
	
	public OrderStatus status = OrderStatus.订单信息;
	
	public boolean isHandle = false;
	
	@Formats.DateTime(pattern="yyyy-MM-dd HH:mm:ss")
	public Date createdAt = new Date();
	
	@Version
	public Date updatedAt;
	
	public amazon.erp.models.AmazonOrder cp() {
		amazon.erp.models.AmazonOrder ao = new amazon.erp.models.AmazonOrder();
		ao.id = this.id;
		ao.amazonOrderId = this.amazonOrderId;
		ao.sellerOrderId = this.sellerOrderId;
		ao.purchaseDate = this.purchaseDate;
		ao.lastUpdateDate = this.lastUpdateDate;
		ao.orderStatus = this.orderStatus;
		ao.fulfillmentChannel = this.fulfillmentChannel;
		ao.salesChannel = this.salesChannel;
		ao.orderChannel = this.orderChannel;
		ao.shipServiceLevel = this.shipServiceLevel;
		ao.name = this.name;
		ao.addressLine1 = this.addressLine1;
		ao.addressLine2 = this.addressLine2;
		ao.addressLine3 = this.addressLine3;
		ao.city = this.city;
		ao.county = this.county;
		ao.district = this.district;
		ao.stateOrRegion = this.stateOrRegion;
		ao.postalCode = this.postalCode;
		ao.countryCode = this.countryCode;
		ao.phone = this.phone;
		ao.currencyCode = this.currencyCode;
		ao.amount = this.amount;
		ao.numberOfItemsShipped = this.numberOfItemsShipped;
		ao.numberOfItemsUnshipped = this.numberOfItemsUnshipped;
		ao.paymentExecutionDetail = this.paymentExecutionDetail;
		ao.paymentMethod = this.paymentMethod;
		ao.marketplaceId = this.marketplaceId;
		ao.buyerEmail = this.buyerEmail;
		ao.buyerName = this.buyerName;
		ao.shipmentServiceLevelCategory = this.shipmentServiceLevelCategory;
		ao.shippedByAmazonTFM = this.shippedByAmazonTFM;
		ao.tfmShipmentStatus = this.tfmShipmentStatus;
		ao.cbaDisplayableShippingLabel = this.cbaDisplayableShippingLabel;
		ao.orderType = this.orderType;
		ao.earliestShipDate = this.earliestShipDate;
		ao.latestShipDate = this.latestShipDate;
		ao.earliestDeliveryDate = this.earliestDeliveryDate;
		ao.latestDeliveryDate = this.latestDeliveryDate;
		ao.isBusinessOrder = this.isBusinessOrder;
		ao.isPremiumOrder = this.isPremiumOrder;
		ao.isPrime = this.isPrime;
		ao.mwsResponse = this.mwsResponse;
		ao.appName = this.appName;
		ao.status = this.status;
		return ao;
	}

}
