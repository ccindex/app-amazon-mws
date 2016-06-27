package models;

import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/*
用户组
* */
@Entity
public class Groups extends Model {

  @Id
  @Constraints.Min(10)
  public Long id;

  public String name;
  
  @Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
  @Column(nullable =false )
  public Date createdAt = new Date();      //创建时间

  @Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
  public Date updatedAt = new Date();      //修改时间

  public String permissions;

  @Version
  public Date version;

  @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "roles", fetch = FetchType.LAZY)
  private Set<User> user = new HashSet<User>();        //用户集合

  public static Finder<Long, Groups> find = new Finder<Long, Groups>(
    Long.class, Groups.class
  );

  @Override
  public void save() {
    super.save();
  }

  public Groups() {
  }

}