package models;


import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class ReportList extends Model {
  public ReportList(String requestId, String type) {
    this.requestId = requestId;
    this.type = type;
  }

  @Id
  @Constraints.Min(10)
  public long id;

  @Column(unique = true, nullable = false)
  @Constraints.Required
  public String requestId;        //amazon 返回的reportrequestid

  public String type;

  public boolean status = false;

  public static Finder<Long, ReportList> find = new Finder<>(Long.class, ReportList.class);

  public static ReportList findByRequestId(String requestId) {
    return find.where().eq("requestId", requestId).findUnique();
  }
}
