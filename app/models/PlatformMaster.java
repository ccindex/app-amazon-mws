package models;

import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Version;
import java.util.Date;
import java.util.List;

@Entity
public class PlatformMaster extends Model {

  @Id
  @Constraints.Min(10)
  public Long id;

  @Column(length = 100, unique = true, nullable = false)
  @Constraints.MaxLength(100)
  @Constraints.Required
  public String name;

  @Column(length = 10, unique = true, nullable = false)
  @Constraints.MaxLength(10)
  @Constraints.Required
  public String abbreviation;

  @Formats.DateTime(pattern = "yyyy-MM-dd")
  public Date createdAt = new Date();

  @Version
  public Date version;

  public String channel;
  public static Finder<Long, PlatformMaster> find = new Finder<Long, PlatformMaster>(
    Long.class, PlatformMaster.class
  );

  @Override
  public void save() {
    abbreviation = abbreviation.toUpperCase();
    super.save();
  }

  public static PlatformMaster free() {
    return find.where().eq("abbreviation", "FREE").findUnique();
  }

  public static List<PlatformMaster> exceptFree() {
    return find.where().ne("abbreviation", "FREE").findList();
  }
}
