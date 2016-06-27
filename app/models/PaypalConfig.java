package models;

import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.*;
import java.util.Date;

/**
 *paypal账号管理
 */
@Entity
public class PaypalConfig extends Model {

  @Id
  public long id;
  public String account;         //账号
  public String username;

  public String password;     //账号
  public String signature ;     //accsessId
  public String appid ;     //appid
  public String subject ;     //代理账号

  public static Finder<Long, PaypalConfig> find = new Finder<>(Long.class, PaypalConfig.class);

}
