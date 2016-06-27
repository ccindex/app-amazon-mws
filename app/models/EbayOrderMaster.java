package models;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Version;

import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;

@Entity
public class EbayOrderMaster extends Model {
	@Id
	@Constraints.Min(10)
	public Long id;
	
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
	//This value indicates the shipping discount experienced by the buyer as a result of creating a Combined Invoice order. 
	//This value is returned as 0.00 for single line item orders.
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
	
	public Long erpPlatformAccountId;
	public int orderDetailNum;
	public int monetaryDetailNum;
	//调用paypal接口失败
	public boolean paypalFailed;
	public String creatingUserRole;			//合并订单才会有值，标识是buyer或者seller合并的
	public boolean isHandled;
	
}
