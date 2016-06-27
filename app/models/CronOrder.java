package models;

import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 获取订单任务表（cronorderActor调度）
 */
@Entity
public class CronOrder extends Model {
	public CronOrder(PlatformAccount account, String config, short minute) {
		this.account = account;
		this.config = config;
		this.minute = minute;
	}

	@Id
	@Constraints.Min(10)
	public long id;

	@ManyToOne
	@Constraints.Required
	public PlatformAccount account;

	@Column(columnDefinition = "TEXT")
	public String config;

	@Constraints.Required
	public short minute;

	public Status status = Status.运行;

	public boolean runnable = true;

	public String apAddress;

	public String accountName; // 账号
	public Long paypalId;
	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date fireAt;

	public Long erpAccountId;

	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date createdAt = new Date();

	@Version
	public Date updatedAt;

	public static Finder<Long, CronOrder> find = new Finder<>(Long.class, CronOrder.class);

	public static enum Status {
		停止, 运行, 错误
	}

}
