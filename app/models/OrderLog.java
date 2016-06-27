package models;


import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.Date;

@Entity
public class OrderLog extends Model {
  public OrderLog(long size, long time, CronOrder cronOrder) {
    this.size = size;
    this.time = time;
    this.cronOrder = cronOrder;
  }

  @Id
  @Constraints.Min(10)
  public Long id;

  @Formats.DateTime(pattern = "yyyy-MM-dd")
  public Date createdAt = new Date();

  public long size;

  public boolean status = true;

  public long time;

  @ManyToOne
  public CronOrder cronOrder;

  public static Finder<Long, OrderLog> find = new Finder<>(Long.class, OrderLog.class);
}
