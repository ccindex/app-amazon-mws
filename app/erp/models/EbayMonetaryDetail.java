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
public class EbayMonetaryDetail extends Model {
	public static enum TYPE{
		UNKNOWN,PAYMENT,REFUND
	}
	
	@Id
	 @Constraints.Min(10)
	public Long id;
	
	public Long masterId;
	public boolean isHandled;
	public TYPE type;
	public String status;
	public String fromType,fromName; 	//payer
	public String toType,toName;		//payee
	@Formats.DateTime(pattern="yyyy-MM-dd")
	public Date time;
	@Column(precision=15, scale=3)
	public BigDecimal amount;
	public String amountCurrency;
	public String referenceIdType;
	public String referenceId;
	@Column(precision=15, scale=3)
	public BigDecimal fee; 
	public String feeCurrency;
	public String refundType;
	@Formats.DateTime(pattern="yyyy-MM-dd")
	@Version
	public Date updateAt;
	
	
	public Long getMasterId() {
		return masterId;
	}
	public void setMasterId(Long masterId) {
		this.masterId = masterId;
	}
	public Boolean getIsHandled() {
		return isHandled;
	}
	public void setIsHandled(Boolean isHandled) {
		this.isHandled = isHandled;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public TYPE getType() {
		return type;
	}
	public void setType(TYPE type) {
		this.type = type;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getFromType() {
		return fromType;
	}
	public void setFromType(String fromType) {
		this.fromType = fromType;
	}
	public String getFromName() {
		return fromName;
	}
	public void setFromName(String fromName) {
		this.fromName = fromName;
	}
	public String getToType() {
		return toType;
	}
	public void setToType(String toType) {
		this.toType = toType;
	}
	public String getToName() {
		return toName;
	}
	public void setToName(String toName) {
		this.toName = toName;
	}
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public String getAmountCurrency() {
		return amountCurrency;
	}
	public void setAmountCurrency(String amountCurrency) {
		this.amountCurrency = amountCurrency;
	}
	public String getReferenceIdType() {
		return referenceIdType;
	}
	public void setReferenceIdType(String referenceIdType) {
		this.referenceIdType = referenceIdType;
	}
	public String getReferenceId() {
		return referenceId;
	}
	public void setReferenceId(String referenceId) {
		this.referenceId = referenceId;
	}
	public BigDecimal getFee() {
		return fee;
	}
	public void setFee(BigDecimal fee) {
		this.fee = fee;
	}
	public String getFeeCurrency() {
		return feeCurrency;
	}
	public void setFeeCurrency(String feeCurrency) {
		this.feeCurrency = feeCurrency;
	}
	public String getRefundType() {
		return refundType;
	}
	public void setRefundType(String refundType) {
		this.refundType = refundType;
	}
	public Date getUpdateAt() {
		return updateAt;
	}
	public void setUpdateAt(Date updateAt) {
		this.updateAt = updateAt;
	}
	
	
	
	
}
