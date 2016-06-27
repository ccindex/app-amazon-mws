package models;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import play.data.format.Formats;
import play.data.validation.Constraints;
import play.data.validation.Constraints.MaxLength;
import play.db.ebean.Model;

@Entity
public class EbayLog extends Model {
	@Id
	@Constraints.Min(10)
	public long id;
	@Column(length=1000)
	public String content;
	@Formats.DateTime(pattern="yyyy-MM-dd")
	public Date time;
	public LogType type;
	
	public static Finder<Long, EbayLog> finder=new Finder<>(Long.class, EbayLog.class);
	
	public static enum LogType{
		NORMAL,WARN,ERROR
	}
}
