package models;

import play.db.ebean.Model;

import java.util.List;
import java.util.Map;

/**
 * ERP定时获取AP 数据实体
 */
public class TrackingNumber extends Model {

  public long id;
  public String platform;      //渠道
  public String accountName;   //账号
  public String config;      //参数
  public List<Map> data;
  public Integer total;  //data的数量
}
