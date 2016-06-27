package amazon.models.report;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Version;

import play.data.format.Formats;
import play.db.ebean.Model;

/**
 * Title: AmazonReport.java
 *
 * @author Lc
 *
 * @date 2016年5月6日 下午10:33:10
 */
@Entity
public class AmazonReport extends Model {

	@Id
	public Long id;

	@Column(unique=true, nullable=false)
	public String reportRequestId;

	public String reportType;

	public String startDate;

	public String endDate;
	
	public String submittedDate;

	public String reportProcessingStatus;

	public String generatedReportId;

	public String startedProcessingDate;

	public String completedDate;
		
	@Column(nullable=false)
	public String url;

	@Column(nullable=false)
	public String appName;

	public Status status = Status.报告记录;
	
	public String path;

	public String md5Checksum;

	@Formats.DateTime(pattern="yyyy-MM-dd HH:mm:ss")
	public Date createdAt = new Date();
	
	@Version
	public Date updatedAt;

	public static enum Status {
		报告记录,
		订单文件,
		订单记录,
		数据异常
	}
	
}
