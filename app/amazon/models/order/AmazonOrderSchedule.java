package amazon.models.order;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Version;

import amazon.enums.order.ScheduleStatus;
import play.data.format.Formats;
import play.db.ebean.Model;

/**
 * Title: AmazonOrderSchedule.java
 *
 * @author Lc
 *
 * @date 2016年5月5日 上午10:07:40
 */
@Entity
public class AmazonOrderSchedule extends Model {

	@Id
	public Long id;
	
	@Column(length=1000, nullable=false)
	public String config;
	
	public ScheduleStatus status = ScheduleStatus.GREEN;
	
	@Column(nullable=false)
	public String ipAddress;
	
	@Column(nullable=false)
	@Formats.DateTime(pattern="yyyy-MM-dd HH:mm:ss")
	public Date lastTime;

	@Formats.DateTime(pattern="yyyy-MM-dd HH:mm:ss")
	public Date createdAt = new Date();
	
	@Version
	public Date updatedAt;
	
}
