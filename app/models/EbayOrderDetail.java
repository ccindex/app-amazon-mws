package models;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Version;

import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;

@Entity
public class EbayOrderDetail extends Model {
	@Id
	 @Constraints.Min(10)
	public Long id;
	
	@ManyToOne
	public EbayOrderMaster master;
	public boolean isHandled;
	public String itemId;
	public String sku;
	public Integer quantity;
	@Column(precision=15, scale=3)
	public BigDecimal price;
	public String priceCurrency;
	public String transactionId;
	public String shippingCarrier;
	public String shippingTrackingNum;
	//有可能和主表里面不一样，例如TE的208605690017，144456和144457被合并成144458
	public Integer salesRecordNumber;
	@Column(precision=15, scale=3)
	public BigDecimal totalTaxAmount;
	public String totalTaxAmountCurrency;
	@Formats.DateTime(pattern="yyyy-MM-dd")
	@Version
	public Date updateAt;
	@Formats.DateTime(pattern="yyyy-MM-dd")
	public Date createAt;
	@Column(precision=15, scale=3)
	public BigDecimal actualShippingCost;
	public String actualShippingCostCurrency;
	@Column(precision=15, scale=3)
	public BigDecimal actualHandlingCost;
	public String actualHandlingCostCurrency;
	public String site;
	@Column(precision=15, scale=3)
	public BigDecimal taxAmount;
	@Column(precision=15, scale=3)
	public BigDecimal taxOnSubtotal;
	@Column(precision=15, scale=3)
	public BigDecimal taxOnShipping;
	@Column(precision=15, scale=3)
	public BigDecimal taxOnHandling;
	@Column(precision=15, scale=3)
	public BigDecimal wasteRecyclingFeeTaxAmount;
	@Column(length=2000)
	public String title;
	@Formats.DateTime(pattern="yyyy-MM-dd")
	public Date estimatedDeliveryTimeMin;
	@Formats.DateTime(pattern="yyyy-MM-dd")
	public Date estimatedDeliveryTimeMax;
//	public boolean ebayPlusTransaction;
	public String buyerEmail;
	public String staticAlias;
}
