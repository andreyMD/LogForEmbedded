package dbstatextract;

import java.io.Writer;
import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Email {

    public static void send(Writer message) {
        try {

            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.mail.yahoo.com"); // for gmail use smtp.gmail.com
            props.put("mail.smtp.auth", "true");
            props.put("mail.debug", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.port", "465");
            props.put("mail.smtp.socketFactory.port", "465");
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.socketFactory.fallback", "false");

            Session mailSession = Session.getInstance(props, new javax.mail.Authenticator() {

                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication("andrey1@yahoo.com", "123");
                }
            });
           
            Message msg = new MimeMessage(mailSession);
            // TODO  property file
            msg.setFrom(new InternetAddress("andrey1@yahoo.com"));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse("andrey1@tline.ru"));
                      
            msg.setSentDate(new Date());
            msg.setSubject("Анализ лога за " + new Date());          

            msg.setContent(message.toString(),"text/html; charset=utf-8");
                                                                  
            Transport.send(msg);

        } catch (Exception E) {
            System.out.println("Oops something has gone pearshaped!");
            System.out.println(E);
        }
    }
}
