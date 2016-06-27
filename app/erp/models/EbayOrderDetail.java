package erp.models;

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

	public Long masterId;
	public boolean isHandled;
	public String itemId;
	public String sku;
	public Integer quantity;
	@Column(precision = 15, scale = 3)
	public BigDecimal price;
	public String priceCurrency;
	public String transactionId;
	public String shippingCarrier;
	public String shippingTrackingNum;
	public Integer salesRecordNumber;
	@Column(precision = 15, scale = 3)
	public BigDecimal totalTaxAmount;
	public String totalTaxAmountCurrency;
	@Formats.DateTime(pattern = "yyyy-MM-dd")
	@Version
	public Date updateAt;
	@Formats.DateTime(pattern = "yyyy-MM-dd")
	public Date createAt;

	@Column(precision = 15, scale = 3)
	public BigDecimal actualShippingCost;
	public String actualShippingCostCurrency;
	@Column(precision = 15, scale = 3)
	public BigDecimal actualHandlingCost;
	public String actualHandlingCostCurrency;
	public String site;
	@Column(precision = 15, scale = 3)
	public BigDecimal taxAmount;
	@Column(precision = 15, scale = 3)
	public BigDecimal taxOnSubtotal;
	@Column(precision = 15, scale = 3)
	public BigDecimal taxOnShipping;
	@Column(precision = 15, scale = 3)
	public BigDecimal taxOnHandling;
	@Column(precision = 15, scale = 3)
	public BigDecimal wasteRecyclingFeeTaxAmount;
	@Column(length = 2000)
	public String title;
	@Formats.DateTime(pattern = "yyyy-MM-dd")
	public Date estimatedDeliveryTimeMin;
	@Formats.DateTime(pattern = "yyyy-MM-dd")
	public Date estimatedDeliveryTimeMax;
	// public boolean ebayPlusTransaction;
	public String buyerEmail;
	public String staticAlias;

	
	public Integer getSalesRecordNumber() {
		return salesRecordNumber;
	}

	public void setSalesRecordNumber(Integer salesRecordNumber) {
		this.salesRecordNumber = salesRecordNumber;
	}

	public Long getMasterId() {
		return masterId;
	}

	public void setMasterId(Long masterId) {
		this.masterId = masterId;
	}
	
	public String getStaticAlias() {
		return staticAlias;
	}

	public void setStaticAlias(String staticAlias) {
		this.staticAlias = staticAlias;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Boolean getIsHandled() {
		return isHandled;
	}

	public void setIsHandled(Boolean isHandled) {
		this.isHandled = isHandled;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public String getPriceCurrency() {
		return priceCurrency;
	}

	public void setPriceCurrency(String priceCurrency) {
		this.priceCurrency = priceCurrency;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public String getShippingCarrier() {
		return shippingCarrier;
	}

	public void setShippingCarrier(String shippingCarrier) {
		this.shippingCarrier = shippingCarrier;
	}

	public String getShippingTrackingNum() {
		return shippingTrackingNum;
	}

	public void setShippingTrackingNum(String shippingTrackingNum) {
		this.shippingTrackingNum = shippingTrackingNum;
	}

	public BigDecimal getTotalTaxAmount() {
		return totalTaxAmount;
	}

	public void setTotalTaxAmount(BigDecimal totalTaxAmount) {
		this.totalTaxAmount = totalTaxAmount;
	}

	public String getTotalTaxAmountCurrency() {
		return totalTaxAmountCurrency;
	}

	public void setTotalTaxAmountCurrency(String totalTaxAmountCurrency) {
		this.totalTaxAmountCurrency = totalTaxAmountCurrency;
	}

	public Date getUpdateAt() {
		return updateAt;
	}

	public void setUpdateAt(Date updateAt) {
		this.updateAt = updateAt;
	}

	public Date getCreateAt() {
		return createAt;
	}

	public void setCreateAt(Date createAt) {
		this.createAt = createAt;
	}

	public BigDecimal getActualShippingCost() {
		return actualShippingCost;
	}

	public void setActualShippingCost(BigDecimal actualShippingCost) {
		this.actualShippingCost = actualShippingCost;
	}

	public String getActualShippingCostCurrency() {
		return actualShippingCostCurrency;
	}

	public void setActualShippingCostCurrency(String actualShippingCostCurrency) {
		this.actualShippingCostCurrency = actualShippingCostCurrency;
	}

	public BigDecimal getActualHandlingCost() {
		return actualHandlingCost;
	}

	public void setActualHandlingCost(BigDecimal actualHandlingCost) {
		this.actualHandlingCost = actualHandlingCost;
	}

	public String getActualHandlingCostCurrency() {
		return actualHandlingCostCurrency;
	}

	public void setActualHandlingCostCurrency(String actualHandlingCostCurrency) {
		this.actualHandlingCostCurrency = actualHandlingCostCurrency;
	}

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public BigDecimal getTaxAmount() {
		return taxAmount;
	}

	public void setTaxAmount(BigDecimal taxAmount) {
		this.taxAmount = taxAmount;
	}

	public BigDecimal getTaxOnSubtotal() {
		return taxOnSubtotal;
	}

	public void setTaxOnSubtotal(BigDecimal taxOnSubtotal) {
		this.taxOnSubtotal = taxOnSubtotal;
	}

	public BigDecimal getTaxOnShipping() {
		return taxOnShipping;
	}

	public void setTaxOnShipping(BigDecimal taxOnShipping) {
		this.taxOnShipping = taxOnShipping;
	}

	public BigDecimal getTaxOnHandling() {
		return taxOnHandling;
	}

	public void setTaxOnHandling(BigDecimal taxOnHandling) {
		this.taxOnHandling = taxOnHandling;
	}

	public BigDecimal getWasteRecyclingFeeTaxAmount() {
		return wasteRecyclingFeeTaxAmount;
	}

	public void setWasteRecyclingFeeTaxAmount(BigDecimal wasteRecyclingFeeTaxAmount) {
		this.wasteRecyclingFeeTaxAmount = wasteRecyclingFeeTaxAmount;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Date getEstimatedDeliveryTimeMin() {
		return estimatedDeliveryTimeMin;
	}

	public void setEstimatedDeliveryTimeMin(Date estimatedDeliveryTimeMin) {
		this.estimatedDeliveryTimeMin = estimatedDeliveryTimeMin;
	}

	public Date getEstimatedDeliveryTimeMax() {
		return estimatedDeliveryTimeMax;
	}

	public void setEstimatedDeliveryTimeMax(Date estimatedDeliveryTimeMax) {
		this.estimatedDeliveryTimeMax = estimatedDeliveryTimeMax;
	}

	public String getBuyerEmail() {
		return buyerEmail;
	}

	public void setBuyerEmail(String buyerEmail) {
		this.buyerEmail = buyerEmail;
	}

}
