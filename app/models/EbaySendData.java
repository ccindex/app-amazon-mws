package models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Version;

import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;

@Entity
public class EbaySendData extends Model {
	@Id
	@Constraints.Min(10)
	public Long id;
	public Boolean runnable;
	@Formats.DateTime(pattern="yyyy-MM-dd")
	@Version
	public Date updateAt;
	public String ip;
}
