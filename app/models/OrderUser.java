package models;

import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.*;
import java.util.Date;

/**
 * 订单用户表
 */
@Entity
public class OrderUser extends Model {
  public OrderUser(PlatformAccount account, String config, short minute) {
    this.account = account;
    this.config = config;
    this.minute = minute;
  }

  @Id
  @Constraints.Min(10)
  public long id;

  @ManyToOne
  @Constraints.Required
  public PlatformAccount account;             //渠道

  @Column(columnDefinition = "TEXT")
  public String config;            //参数

  @Constraints.Required
  public short minute;              //间隔时间

  public Status status = Status.运行;

  public String ip;          //ip

  @Formats.DateTime(pattern = "dd/MM/yyyy")
  public Date fireAt = new Date();

  @Formats.DateTime(pattern = "dd/MM/yyyy")
  public Date createdAt = new Date();

  @Version
  public Date updatedAt;

  public static Finder<Long, OrderUser> find = new Finder<>(Long.class, OrderUser.class);


  public static enum Status {
    停止, 运行
  }

}
