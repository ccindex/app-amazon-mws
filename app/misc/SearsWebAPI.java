package misc;


import actors.FileUploadActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.pattern.Patterns;
import com.fasterxml.jackson.databind.JsonNode;
import models.*;
import org.w3c.dom.Document;
import play.Play;
import play.libs.Akka;
import play.libs.F.Promise;
import play.libs.Json;
import play.libs.ws.WS;
import play.libs.ws.WSRequestHolder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

public class SearsWebAPI {
  private String apiUrl = "https://seller.marketplace.sears.com/SellerPortal/api/oms/purchaseorder/v6";
  private String email = "";
  private String password = "";
  private Map<String, String> parameter = new Hashtable<>();

  public WSRequestHolder sendRequest(String url) {
    return WS.url(url).setQueryParameter("email", this.email).setQueryParameter("password", this.password);
  }

  public WSRequestHolder sendRequest(String url, Map parameter) {
    WSRequestHolder requestHolder = this.sendRequest(url);

    Iterator iterator = parameter.entrySet().iterator();
    Map.Entry<String, String> entry;
    while (iterator.hasNext()) {
      entry = (Map.Entry<String, String>)iterator.next();
      requestHolder.setQueryParameter(entry.getKey(), entry.getValue());
    }

    return requestHolder;
  }

  public Map<String, String> setParameter(String field, String value) {
    this.parameter.put(field, value);
    return this.parameter;
  }

  public Map<String, String> setDefaultParameter() {
    this.setParameter("status", "New");
    return this.parameter;
  }

  public Promise<Document> getXml() {
    return this.sendRequest(this.apiUrl, this.parameter).get().map(response -> {
      return response.asXml();
    });
  }

  public String generateFilePath(LocalDate now, String prePath,CronOrder cronOrder) {
    String path =  "" +cronOrder.accountName + "/"+ now.getYear() + "/" + now.getMonth().getValue() + "/"+now.getDayOfMonth()+"/";
    File dir = new File(prePath + path);

    if (!dir.exists()) {
      dir.mkdirs();
    }

    return path;
  }

  public String generateFileName(long uniqueKey, String format) {
    return uniqueKey + "." + format;
  }

  public Promise<File> save(String targetName) {
    return this.sendRequest(this.apiUrl, this.parameter).get().map(response -> {
      InputStream inputStream = null;
      OutputStream outputStream = null;

      try {
        inputStream = response.getBodyAsStream();
        File file = new File(targetName);
        outputStream = new FileOutputStream(file);

        int read = 0;
        byte[] buffer = new byte[1024];

        while ((read = inputStream.read(buffer)) != -1) {
          outputStream.write(buffer, 0, read);
        }

        return file;
      } catch (Exception e) {
        throw e;
      } finally {
        inputStream.close();
        outputStream.close();
      }
    });
  }

  public void handle(CronOrder cronOrder) {
    LocalDate now = LocalDate.now();
    long startTime = System.currentTimeMillis();
    String preFilePath = Play.application().configuration().getString("s3.file.path");
    String filePath = this.generateFilePath(now, preFilePath,cronOrder);
    String fileName = this.generateFileName(startTime, "xml");
    this.setDefaultParameter();
    JsonNode config = Json.parse(cronOrder.config);
    this.email = config.get("email").asText();
    this.password = config.get("password").asText();

    this.save(preFilePath + filePath + fileName).map(response -> {
      User me = User.find.where().eq("email", "cron@cron.com").findUnique();
      S3File s3File = new S3File(fileName, "text/plain", me, response.length(), filePath + fileName,null,fileName);
      s3File.save();

      QueueMaster queueMaster = new QueueMaster(fileName, "order-sears-xml", "{\"accountId\": " + cronOrder.account.id + "}", me);
      queueMaster.attachments.add(s3File);
      queueMaster.save();

      OrderLog orderLog = new OrderLog(response.length(), System.currentTimeMillis() - startTime, cronOrder);
      orderLog.save();

      // @todo Akka
      ActorRef fileUploadActor = Akka.system().actorOf(Props.create(FileUploadActor.class));
      Promise.wrap(Patterns.ask(fileUploadActor, queueMaster, 1000));

      return fileName;
    });
  }
}
