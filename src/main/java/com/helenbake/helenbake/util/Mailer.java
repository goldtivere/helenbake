package com.helenbake.helenbake.util;



import com.helenbake.helenbake.domain.User;
import com.helenbake.helenbake.dto.MailData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.util.List;

/**
 * Created by Toyin on 5/6/19.
 */
@Service
public class Mailer {
    private JavaMailSender mailSender;
    @Value("${app.send.email}")
    private String fromMail;

    @Autowired
    public Mailer(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    public void sendMailAsync(MailData mailData) throws MessagingException {
        mailData.setFrom(fromMail);
        mailData.sendMessage();
        System.out.println("****Message Sent***");
    }
    @Async
    public void sendMailAsync(MailData mailData,String senderEmail) throws MessagingException {
        mailData.setFrom(senderEmail);
        mailData.sendMessage();
        System.out.println("****Message Sent***");
    }
    @Async
    public void mailUserAsync(User user, String message, String subject) throws MessagingException {
       // mailUserAsync(user.getEmail(), message, subject);
    }

    @Async
    public void mailUserAsync(User user, String message, String subject,String senderEmail) throws MessagingException {
       // mailUserAsync(user.getEmail(), message, subject,senderEmail);
    }

    @Async
    public void mailUserAsyncAttach(User user,String message, String subject, InputStreamSource inputStreamResource) throws MessagingException {
        //mailUserAsync(user.getEmail(), message, subject);
    }

    @Async
    public void mailUserAsync(String to, String message, String subject) throws MessagingException {
        MailData mailData = new MailData(to, subject, message, mailSender);
        sendMailAsync(mailData);
    }

    @Async
    public void mailUserAsync(String to, String message, String subject,String senderEmail) throws MessagingException {
        MailData mailData = new MailData(to, subject, message, mailSender);
        sendMailAsync(mailData,senderEmail);
    }
    @Async
    public void mailUserAsyncAttach(String to, String message, String subject, String fileName, InputStreamSource inputStreamResource, String senderEmail) throws MessagingException {
        MailData mailData = new MailData(to, subject, message, mailSender,fileName,inputStreamResource);
        sendMailAsync(mailData,senderEmail);
    }
    @Async
    public void mailUsersAsync(List<User> users, String message, String subject) {
        users.forEach(user -> {
            String msg = message.replace("{firstName}", user.getFirstName());
            // mailUserAsync(user.getEmail(), msg, subject);
        });
    }

    public void sendMail(MailData mailData) throws MessagingException {
        mailData.setFrom(fromMail);
        mailData.sendMessage();
    }

    public void mailUser(User user, String message, String subject) throws MessagingException {
        //mailUser(user.getEmail(), message, subject);
    }

    public void mailUser(String to, String message, String subject) throws MessagingException {
        MailData mailData = new MailData(to, subject, message, mailSender);
        sendMail(mailData);
    }

    public void mailUsers(List<User> users, String message, String subject) throws MessagingException {
        for (User user: users) {
            String msg = message.replace("{firstName}", user.getFirstName());
           // mailUser(user.getEmail(), msg, subject);
        }
    }
}
