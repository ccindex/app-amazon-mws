package models;

import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 任务实体：
 */
@Entity
public class QueueMaster extends Model {

  @Id
  @Constraints.Min(10)
  public Long id;

  @Column(nullable = false)
  public String action;          //任务

  public String params;          //参数

  @ManyToOne
  public User agent;                   //操作人

  public String fileName;             //文件名

  public Status status = Status.PENDING;      //状态

  @ManyToMany
  public List<S3File> attachments = new ArrayList<S3File>();       //文件路径

  public int total;             //请求总数

  public int approved;          //处理数量

  public int pending;           //保存数量

  public int reject;            //拒绝数量

  public int error;             //错误数量

  @ManyToMany
  public List<QueueError> errors = new ArrayList<QueueError>();

  @Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
  public Date createdAt = new Date();

  @Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
  public Date updateAt = new Date();

  @Version
  public Date version;

  public QueueMaster(String fileName, String action, String params, User agent) {
    this.fileName = fileName;
    this.action = action;
    this.params = params;
    this.agent = agent;
  }
  public QueueMaster() {
  }
  public static Finder<Long, QueueMaster> find = new Finder<Long, QueueMaster>(
    Long.class, QueueMaster.class
  );

  public static enum Status {
    PENDING, RUNNING, SUCCEED, FAILED
  }

}
