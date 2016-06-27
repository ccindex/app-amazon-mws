package erp.models;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Version;

import models.PlatformAccount;
import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;

@Entity
public class EbayOrderMaster extends Model{
	@Id
	@Constraints.Min(10)
	public Long id;
	public boolean isHandled;
	
	public String orderId;
	public String buyerId;
	public String shippingService;
	@Formats.DateTime(pattern="yyyy-MM-dd")
	public Date shippedTime;
	public Integer salesRecordNumber;
	@Column(precision=15,scale=3)
	public BigDecimal tax;
	public String taxCurrency;
	@Column(precision=15,scale=3)
	public BigDecimal taxPercent;
	public String taxState;
	public Boolean shippingIncludedInTax;
	@Column(precision=15,scale=3)
	public BigDecimal insurance;
	public String insuranceCurrency;
	@Column(precision=15,scale=3)
	public BigDecimal total;
	public String totalCurrency;
	@Column(precision=15,scale=3)
	public BigDecimal subtotal;
	public String subtotalCurrency;
	@Column(precision=15,scale=3)
	public BigDecimal amountAdjust;
	public String amountAdjustCurrency;
	@Column(precision=15,scale=3)
	public BigDecimal amountPaid;
	public String amountPaidCurrency;
	@Column(precision=15,scale=3)
	public BigDecimal amountSaved;
	public String amountSavedCurrency;
	public String paymentMethod;
	public String checkoutStatus;
	@Formats.DateTime(pattern="yyyy-MM-dd")
	public Date lastModifiedTime;
	@Formats.DateTime(pattern="yyyy-MM-dd")
	public Date createDate;
	@Formats.DateTime(pattern="yyyy-MM-dd")
	public Date paypalDate;			//paypal中获取的数据
	public String paypalEmail;		
	public String paypalStatus;
	
	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date paidTime;		//ebay中返回的
	public String orderStatus;
	public String sellerEmail;
	public String sellerUserId;
	public String eiasToken;	//买家唯一识别码
	public Boolean integratedMerchantCreditCardEnabled;  //Indicates whether the item can be paid for through a payment gateway (Payflow) account.
	@Formats.DateTime(pattern="yyyy-MM-dd")
	public Date fireAtDate;
	@Version
	public Date updateAt;
	
	//买家当地地址
	public String buyerName,buyerPhone,buyerStreet1,buyerStreet2,buyerCity,buyerState,buyerZip,buyerCountry;
	public Boolean isMultiLegShipping;	//是否使用了Glogal Shipping
	//global shipping的us地址
	public String shippingRecipientName,shippingRecipientPhone,shippingStreet1,shippingStreet2,shippingCity,shippingState,shippingZip,shippingCountry;
	//global shipping reference id
	public String shippingReferenceId;
	
	public Boolean getItFast;
	@Column(precision=15,scale=3)
	public BigDecimal shippingCost; 	//国内运费
	public String shippingCostCurrency;
	public String globalShippingService;
	@Column(precision=15,scale=3)
	public BigDecimal globalShippingCost; 	//国际运费
	public String globalShippingCostCurrency;
	@Column(precision=15,scale=3)
	public BigDecimal globalShippingImportCharge;	//关税
	public String globalShippingImportChargeCurrency;
//	public boolean contrainsEbayPlusTransaction;	//是否用到了ebay plus
	public int orderDetailNum;
	public int monetaryDetailNum;
	public Long erpPlatformAccountId;
	public String creatingUserRole;
	
	
	public String getCreatingUserRole() {
		return creatingUserRole;
	}
	public void setCreatingUserRole(String creatingUserRole) {
		this.creatingUserRole = creatingUserRole;
	}
	public Long getErpPlatformAccountId() {
		return erpPlatformAccountId;
	}
	public void setErpPlatformAccountId(Long erpPlatformAccountId) {
		this.erpPlatformAccountId = erpPlatformAccountId;
	}
	public Boolean getIsHandled() {
		return isHandled;
	}
	public void setIsHandled(Boolean isHandled) {
		this.isHandled = isHandled;
	}
	public int getOrderDetailNum() {
		return orderDetailNum;
	}
	public int getMonetaryDetailNum() {
		return monetaryDetailNum;
	}
	public void setOrderDetailNum(int orderDetailNum) {
		this.orderDetailNum = orderDetailNum;
	}
	public void setMonetaryDetailNum(int monetaryDetailNum) {
		this.monetaryDetailNum = monetaryDetailNum;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getBuyerId() {
		return buyerId;
	}
	public void setBuyerId(String buyerId) {
		this.buyerId = buyerId;
	}
	public String getShippingService() {
		return shippingService;
	}
	public void setShippingService(String shippingService) {
		this.shippingService = shippingService;
	}
	public Date getShippedTime() {
		return shippedTime;
	}
	public void setShippedTime(Date shippedTime) {
		this.shippedTime = shippedTime;
	}
	public Integer getSalesRecordNumber() {
		return salesRecordNumber;
	}
	public void setSalesRecordNumber(Integer salesRecordNumber) {
		this.salesRecordNumber = salesRecordNumber;
	}
	public BigDecimal getTax() {
		return tax;
	}
	public void setTax(BigDecimal tax) {
		this.tax = tax;
	}
	public String getTaxCurrency() {
		return taxCurrency;
	}
	public void setTaxCurrency(String taxCurrency) {
		this.taxCurrency = taxCurrency;
	}
	public BigDecimal getTaxPercent() {
		return taxPercent;
	}
	public void setTaxPercent(BigDecimal taxPercent) {
		this.taxPercent = taxPercent;
	}
	public String getTaxState() {
		return taxState;
	}
	public void setTaxState(String taxState) {
		this.taxState = taxState;
	}
	public Boolean getShippingIncludedInTax() {
		return shippingIncludedInTax;
	}
	public void setShippingIncludedInTax(Boolean shippingIncludedInTax) {
		this.shippingIncludedInTax = shippingIncludedInTax;
	}
	public BigDecimal getInsurance() {
		return insurance;
	}
	public void setInsurance(BigDecimal insurance) {
		this.insurance = insurance;
	}
	public String getInsuranceCurrency() {
		return insuranceCurrency;
	}
	public void setInsuranceCurrency(String insuranceCurrency) {
		this.insuranceCurrency = insuranceCurrency;
	}
	public BigDecimal getTotal() {
		return total;
	}
	public void setTotal(BigDecimal total) {
		this.total = total;
	}
	public String getTotalCurrency() {
		return totalCurrency;
	}
	public void setTotalCurrency(String totalCurrency) {
		this.totalCurrency = totalCurrency;
	}
	public BigDecimal getSubtotal() {
		return subtotal;
	}
	public void setSubtotal(BigDecimal subtotal) {
		this.subtotal = subtotal;
	}
	public String getSubtotalCurrency() {
		return subtotalCurrency;
	}
	public void setSubtotalCurrency(String subtotalCurrency) {
		this.subtotalCurrency = subtotalCurrency;
	}
	public BigDecimal getAmountAdjust() {
		return amountAdjust;
	}
	public void setAmountAdjust(BigDecimal amountAdjust) {
		this.amountAdjust = amountAdjust;
	}
	public String getAmountAdjustCurrency() {
		return amountAdjustCurrency;
	}
	public void setAmountAdjustCurrency(String amountAdjustCurrency) {
		this.amountAdjustCurrency = amountAdjustCurrency;
	}
	public BigDecimal getAmountPaid() {
		return amountPaid;
	}
	public void setAmountPaid(BigDecimal amountPaid) {
		this.amountPaid = amountPaid;
	}
	public String getAmountPaidCurrency() {
		return amountPaidCurrency;
	}
	public void setAmountPaidCurrency(String amountPaidCurrency) {
		this.amountPaidCurrency = amountPaidCurrency;
	}
	public BigDecimal getAmountSaved() {
		return amountSaved;
	}
	public void setAmountSaved(BigDecimal amountSaved) {
		this.amountSaved = amountSaved;
	}
	public String getAmountSavedCurrency() {
		return amountSavedCurrency;
	}
	public void setAmountSavedCurrency(String amountSavedCurrency) {
		this.amountSavedCurrency = amountSavedCurrency;
	}
	public String getPaymentMethod() {
		return paymentMethod;
	}
	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}
	public String getCheckoutStatus() {
		return checkoutStatus;
	}
	public void setCheckoutStatus(String checkoutStatus) {
		this.checkoutStatus = checkoutStatus;
	}
	public Date getLastModifiedTime() {
		return lastModifiedTime;
	}
	public void setLastModifiedTime(Date lastModifiedTime) {
		this.lastModifiedTime = lastModifiedTime;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public Date getPaypalDate() {
		return paypalDate;
	}
	public void setPaypalDate(Date paypalDate) {
		this.paypalDate = paypalDate;
	}
	public String getPaypalEmail() {
		return paypalEmail;
	}
	public void setPaypalEmail(String paypalEmail) {
		this.paypalEmail = paypalEmail;
	}
	public String getPaypalStatus() {
		return paypalStatus;
	}
	public void setPaypalStatus(String paypalStatus) {
		this.paypalStatus = paypalStatus;
	}
	public Date getPaidTime() {
		return paidTime;
	}
	public void setPaidTime(Date paidTime) {
		this.paidTime = paidTime;
	}
	public String getOrderStatus() {
		return orderStatus;
	}
	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}
	public String getSellerEmail() {
		return sellerEmail;
	}
	public void setSellerEmail(String sellerEmail) {
		this.sellerEmail = sellerEmail;
	}
	public String getSellerUserId() {
		return sellerUserId;
	}
	public void setSellerUserId(String sellerUserId) {
		this.sellerUserId = sellerUserId;
	}
	public String getEiasToken() {
		return eiasToken;
	}
	public void setEiasToken(String eiasToken) {
		this.eiasToken = eiasToken;
	}
	public Boolean getIntegratedMerchantCreditCardEnabled() {
		return integratedMerchantCreditCardEnabled;
	}
	public void setIntegratedMerchantCreditCardEnabled(Boolean integratedMerchantCreditCardEnabled) {
		this.integratedMerchantCreditCardEnabled = integratedMerchantCreditCardEnabled;
	}
	public Date getFireAtDate() {
		return fireAtDate;
	}
	public void setFireAtDate(Date fireAtDate) {
		this.fireAtDate = fireAtDate;
	}
	public Date getUpdateAt() {
		return updateAt;
	}
	public void setUpdateAt(Date updateAt) {
		this.updateAt = updateAt;
	}
	public String getBuyerName() {
		return buyerName;
	}
	public void setBuyerName(String buyerName) {
		this.buyerName = buyerName;
	}
	public String getBuyerPhone() {
		return buyerPhone;
	}
	public void setBuyerPhone(String buyerPhone) {
		this.buyerPhone = buyerPhone;
	}
	public String getBuyerStreet1() {
		return buyerStreet1;
	}
	public void setBuyerStreet1(String buyerStreet1) {
		this.buyerStreet1 = buyerStreet1;
	}
	public String getBuyerStreet2() {
		return buyerStreet2;
	}
	public void setBuyerStreet2(String buyerStreet2) {
		this.buyerStreet2 = buyerStreet2;
	}
	public String getBuyerCity() {
		return buyerCity;
	}
	public void setBuyerCity(String buyerCity) {
		this.buyerCity = buyerCity;
	}
	public String getBuyerState() {
		return buyerState;
	}
	public void setBuyerState(String buyerState) {
		this.buyerState = buyerState;
	}
	public String getBuyerZip() {
		return buyerZip;
	}
	public void setBuyerZip(String buyerZip) {
		this.buyerZip = buyerZip;
	}
	public String getBuyerCountry() {
		return buyerCountry;
	}
	public void setBuyerCountry(String buyerCountry) {
		this.buyerCountry = buyerCountry;
	}
	public Boolean getIsMultiLegShipping() {
		return isMultiLegShipping;
	}
	public void setIsMultiLegShipping(Boolean isMultiLegShipping) {
		this.isMultiLegShipping = isMultiLegShipping;
	}
	public String getShippingRecipientName() {
		return shippingRecipientName;
	}
	public void setShippingRecipientName(String shippingRecipientName) {
		this.shippingRecipientName = shippingRecipientName;
	}
	public String getShippingRecipientPhone() {
		return shippingRecipientPhone;
	}
	public void setShippingRecipientPhone(String shippingRecipientPhone) {
		this.shippingRecipientPhone = shippingRecipientPhone;
	}
	public String getShippingStreet1() {
		return shippingStreet1;
	}
	public void setShippingStreet1(String shippingStreet1) {
		this.shippingStreet1 = shippingStreet1;
	}
	public String getShippingStreet2() {
		return shippingStreet2;
	}
	public void setShippingStreet2(String shippingStreet2) {
		this.shippingStreet2 = shippingStreet2;
	}
	public String getShippingCity() {
		return shippingCity;
	}
	public void setShippingCity(String shippingCity) {
		this.shippingCity = shippingCity;
	}
	public String getShippingState() {
		return shippingState;
	}
	public void setShippingState(String shippingState) {
		this.shippingState = shippingState;
	}
	public String getShippingZip() {
		return shippingZip;
	}
	public void setShippingZip(String shippingZip) {
		this.shippingZip = shippingZip;
	}
	public String getShippingCountry() {
		return shippingCountry;
	}
	public void setShippingCountry(String shippingCountry) {
		this.shippingCountry = shippingCountry;
	}
	public String getShippingReferenceId() {
		return shippingReferenceId;
	}
	public void setShippingReferenceId(String shippingReferenceId) {
		this.shippingReferenceId = shippingReferenceId;
	}
	public Boolean getGetItFast() {
		return getItFast;
	}
	public void setGetItFast(Boolean getItFast) {
		this.getItFast = getItFast;
	}
	public BigDecimal getShippingCost() {
		return shippingCost;
	}
	public void setShippingCost(BigDecimal shippingCost) {
		this.shippingCost = shippingCost;
	}
	public String getShippingCostCurrency() {
		return shippingCostCurrency;
	}
	public void setShippingCostCurrency(String shippingCostCurrency) {
		this.shippingCostCurrency = shippingCostCurrency;
	}
	public String getGlobalShippingService() {
		return globalShippingService;
	}
	public void setGlobalShippingService(String globalShippingService) {
		this.globalShippingService = globalShippingService;
	}
	public BigDecimal getGlobalShippingCost() {
		return globalShippingCost;
	}
	public void setGlobalShippingCost(BigDecimal globalShippingCost) {
		this.globalShippingCost = globalShippingCost;
	}
	public String getGlobalShippingCostCurrency() {
		return globalShippingCostCurrency;
	}
	public void setGlobalShippingCostCurrency(String globalShippingCostCurrency) {
		this.globalShippingCostCurrency = globalShippingCostCurrency;
	}
	public BigDecimal getGlobalShippingImportCharge() {
		return globalShippingImportCharge;
	}
	public void setGlobalShippingImportCharge(BigDecimal globalShippingImportCharge) {
		this.globalShippingImportCharge = globalShippingImportCharge;
	}
	public String getGlobalShippingImportChargeCurrency() {
		return globalShippingImportChargeCurrency;
	}
	public void setGlobalShippingImportChargeCurrency(String globalShippingImportChargeCurrency) {
		this.globalShippingImportChargeCurrency = globalShippingImportChargeCurrency;
	}
	
}
