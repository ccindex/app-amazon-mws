package models;

import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class QueueError extends Model {

  @Id
  @Constraints.Min(10)
  public Long id;

  public int line;

  public String content;

  @Column(columnDefinition = "TEXT")
  public String description;

  public QueueError(int line, String content, String description) {
    this.line = line;
    this.content = content;
    this.description = description;
  }
}
