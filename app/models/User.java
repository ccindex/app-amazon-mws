package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.*;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
public class User extends Model  {
  @Id
  @Constraints.Min(10)
  public Long id;

  @Column(length = 255, unique = true, nullable = false)
  @Constraints.MaxLength(255)
  @Constraints.Required
  @Constraints.Email
  public String email;

  private String authToken;

  @Column(length = 64, nullable = false)
  private byte[] shaPassword;

  @Transient
  @Constraints.Required
  @Constraints.MinLength(6)
  @Constraints.MaxLength(255)
  @JsonIgnore
  private String password;

  @Column(length = 255, nullable = false)
  @Constraints.Required
  @Constraints.MinLength(2)
  @Constraints.MaxLength(255)
  public String fullName;

  @Formats.DateTime(pattern = "yyyy-MM-dd")
  public Date createdAt = new Date();

  @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  @JoinTable(name = "groups_users",joinColumns = { @JoinColumn(name ="user_id",referencedColumnName="id" )}, inverseJoinColumns = { @JoinColumn(name = "groups_id",referencedColumnName="id") })
  private Set<Groups> roles = new HashSet<Groups>();    //角色集合

  @Version
  public Date version;

  public String createToken() {
    authToken = UUID.randomUUID().toString();
    save();
    return authToken;
  }

  public void deleteAuthToken() {
    authToken = null;
    save();
  }

  public static byte[] getSha512(String value) {
    try {
      return MessageDigest.getInstance("SHA-512").digest(value.getBytes("UTF-8"));
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
  }

  public String validate() {
    if (false) {
      return "错误的用户名或密码。";
    }
    return null;
  }

  public User(String email, String password, String fullName) {
    this.email = email;
    this.password = password;
    shaPassword = getSha512(password);
    this.fullName = fullName;
  }

  public static Finder<Long, User> find = new Finder<Long, User> (
    Long.class, User.class
  );

  public static User findByEmailAndPassword(String email, String password) {
    return find.where().eq("email", email.toLowerCase()).eq("shaPassword", getSha512(password)).findUnique();
  }

  public static User findByToken(String token) {
    return find.where().eq("authToken", token).findUnique();
  }

}