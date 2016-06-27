package models;

import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.*;
import java.util.Date;

@Entity
public class PlatformAccount extends Model {

  @Id
  @Constraints.Min(10)
  public Long id;

  @ManyToOne
  public PlatformMaster platform;

  @Column(length = 100, unique = true, nullable = false)
  @Constraints.MaxLength(100)
  @Constraints.Required
  public String name;

  @Column(length = 10, unique = true, nullable = false)
  @Constraints.MaxLength(10)
  @Constraints.Required
  public String abbreviation;

  @Constraints.Email
  public String serviceEmail;

  @Constraints.Email
  public String billEmail;

//  @Column(length = 512)
//  public String apiConfiguration;

  @Formats.DateTime(pattern = "yyyy-MM-dd")
  public Date createdAt = new Date();

  @Version
  public Date version;

  @Override
  public void save() {
    abbreviation = abbreviation.toUpperCase();
    super.save();
  }

  public static Finder<Long, PlatformAccount> find = new Finder<>(Long.class, PlatformAccount.class);
}
