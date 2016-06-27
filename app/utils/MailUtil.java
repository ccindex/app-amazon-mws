package utils;

import models.Mail;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import play.Logger;

/**
 * 邮件发送工具实现类
 *
 * @author shadow
 * @create 2013/07/12
 */
public class MailUtil {

  public boolean send(Mail mail) {
    // 发送email
    HtmlEmail email = new HtmlEmail();
    try {
      // 这里是SMTP发送服务器的名字：163的如下："smtp.163.com"
      email.setHostName(mail.getHost());
      // 字符编码集的设置
      email.setCharset(Mail.ENCODEING);
      // 收件人的邮箱
      email.addTo(mail.getReceiver());
      // 发送人的邮箱
      email.setFrom(mail.getSender(), mail.getName());
      // 如果需要认证信息的话，设置认证：用户名-密码。分别为发件人在邮件服务器上的注册名称和密码
      email.setAuthentication(mail.getUsername(), mail.getPassword());
      // 要发送的邮件主题
      email.setSubject(mail.getSubject());
      // 要发送的信息，由于使用了HtmlEmail，可以在邮件内容中使用HTML标签
      email.setMsg(mail.getMessage());
      // 发送
      email.send();
      return true;
    } catch (EmailException e) {
      e.printStackTrace();
      return false;
    }
  }

}
