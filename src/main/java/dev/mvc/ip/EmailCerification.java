package dev.mvc.ip;

import java.util.Properties;
import java.util.Random;

import javax.mail.*;
import javax.mail.internet.*;

import org.springframework.stereotype.Service;

@Service
public class EmailCerification {

  // 인증 코드 생성기
  public String generateAuthCode() {
    Random rnd = new Random();
    int number = rnd.nextInt(999999); // 6자리
    return String.format("%06d", number);
  }

  // 이메일 전송 메서드
  public void sendVerificationEmail(String receiver, String authCode) {
    Properties props = new Properties();
    props.put("mail.smtp.host", "smtp.gmail.com");
    props.put("mail.smtp.port", "587");
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.starttls.enable", "true");
    props.put("mail.smtp.ssl.trust", "smtp.gmail.com");

    // 3. SMTP 서버정보와 사용자 정보를 기반으로 Session 클래스의 인스턴스 생성
    Session session = Session.getInstance(props, new javax.mail.Authenticator() {
      protected PasswordAuthentication getPasswordAuthentication() {
        String user = "friziae2000@gmail.com";
        String password = "yvop wrpt ytzz ixei";
        return new PasswordAuthentication(user, password);
      }
    });

    Message message = new MimeMessage(session);
    
    String htmlContent = "<h2>이메일 인증 코드</h2>"
        + "<p>아래 코드를 회원가입 페이지에 입력해주세요.</p>"
        + "<h3 style='color:blue;'>" + authCode + "</h3>";
    
    try {
      message.setFrom(new InternetAddress("friziae2000@gmail.com"));
      message.addRecipient(Message.RecipientType.TO, new InternetAddress(receiver));
      message.setSubject("이메일 인증");
      message.setContent(htmlContent, "text/html; charset=utf-8");

      Transport.send(message);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
