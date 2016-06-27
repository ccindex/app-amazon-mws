package models;

import play.Play;
import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;

@Entity
public class S3File extends Model {

  @Id
  @Constraints.Min(10)
  public Long id;

  @Column(nullable = false)
  public String name;

  public String mime;

  public long size;

  /*Actually Saved path*/
//  @Column(nullable = false)
//  public String path;

  public String path;

  @ManyToOne
  public User agent;

  @ManyToOne
  public CronOrder cronOrder;

  @Formats.DateTime(pattern = "yyyy-MM-dd")
  public Date createdAt = new Date();
  @Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
  public Date updatedAt;

  public String reportId;  //amazon  reportId
  public String type;  //amazon  reportId
  public boolean isHandle=false;  //数据是否处理
  public String action;  //请求参数
  public String fileName;

  /*@Lob
  @JsonIgnore
  public byte[] file;*/

  public S3File(String name, String mime, User agent, Long size, String path,String reportId,String fileName) {
    this.name = name;
    this.mime = mime;
    this.agent = agent;
    this.size = size;
    this.path = path;
    this.reportId=reportId;
    this.fileName=fileName;
  }

  public byte[] file() {
    byte[] fileContent = null;
    FileInputStream fin = null;

    try {
      fin = new FileInputStream(Play.application().configuration().getString("s3.file.path") + this.path);
      fileContent = new byte[(int)this.size];
      fin.read(fileContent);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        if (fin != null) {
          fin.close();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    return fileContent;
  }

  private byte[] getByteFromFile(File file) {

    byte[] fileContent = null;
    FileInputStream fin = null;

    try {
      fin = new FileInputStream(file);
      fileContent = new byte[(int) file.length()];

      fin.read(fileContent);
      String s = new String(fileContent);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        if (fin != null) {
          fin.close();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    return fileContent;
  }

  @Override
  public void save() {

    /*TODO:目前先保存在数据库中，以后要想办法放到S3或者本地文件系统*/
    super.save();
  }

  @Override
  public void delete() {
    super.delete();
  }

  public static Finder<Long, S3File> find = new Finder<>(Long.class, S3File.class);
}
