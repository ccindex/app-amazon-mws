package models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Version;

import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;

@Entity
public class EbayPaypal extends Model {
	@Id
	@Constraints.Min(10)
	public Long id;

	public String transactionId;
	public Integer retryTimes;
	@Formats.DateTime(pattern="yyyy-MM-dd")
	@Version
	public Date updateAt;
	public Long paypalConfigId;
	public String sellerEmail;

	public static Finder<Long, EbayPaypal> finder = new Finder<>(Long.class, EbayPaypal.class);
}
