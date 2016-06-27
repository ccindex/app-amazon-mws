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
public class EbayMonetaryDetail extends Model {
	public static enum TYPE{
		UNKNOWN,PAYMENT,REFUND
	}
	
	@Id
	 @Constraints.Min(10)
	public Long id;
	
	public boolean isHandled;
	@ManyToOne
	public EbayOrderMaster master;
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
}
