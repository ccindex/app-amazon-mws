package amazon.models.report;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Version;

import amazon.enums.order.ScheduleStatus;
import play.data.format.Formats;
import play.db.ebean.Model;

/**
 * Title: AmazonReportSchedule.java
 *
 * @author Lc
 *
 * @date 2016年4月29日 下午3:14:58
 */
@Entity
public class AmazonReportSchedule extends Model {

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
