package com.helenbake.helenbake.util;

import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.File;

/**
 * @author waliu.faleye
 *
 */
@Service
public class MailService {

    private JavaMailSender mailSender;
    private String username;
    private String toEmail;
    private String fromEmail;
    private String ccEmail;
    private String subject;
    private String filePath;
    private String[] attachments;
    private String content;

    public void sendEmail() {
        MimeMessage message = getMailSender().createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setSubject(this.getSubject());
            helper.setFrom(new InternetAddress(getFromEmail()));
            helper.setTo(InternetAddress.parse(getToEmail()));
            if (this.getCcEmail() != null && !this.getCcEmail().isEmpty()) {
                helper.setCc(new InternetAddress(getCcEmail()));
            }
            helper.setText(this.getContent());
            if (getAttachments() != null) {
                for (String file : getAttachments()) {
                    File file3 = new File(this.getFilePath().trim() + file.trim());
                    FileSystemResource fr1 = new FileSystemResource(file3);
                    helper.addAttachment(file3.getName(), fr1);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try {
            getMailSender().send(message);
        } catch (MailException ex) {
            ex.printStackTrace();
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }

    private InternetAddress[] formatAddress(String email) {
        String emailArr[] = email.split(";");
        int length = emailArr.length;
        InternetAddress[] address = new InternetAddress[length];
        System.out.println("Email address lenght == " + length);

        for (int i = 0; i < length; i++) {
            try {
                address[i] = new InternetAddress(emailArr[i]);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return address;
    }

    /**
     * @return the mailSender
     */
    public JavaMailSender getMailSender() {
        return mailSender;
    }

    /**
     * @param mailSender
     *            the mailSender to set
     */
    public void setMailSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username
     *            the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the toEmail
     */
    public String getToEmail() {
        return toEmail;
    }

    /**
     * @param toEmail
     *            the toEmail to set
     */
    public void setToEmail(String toEmail) {
        this.toEmail = toEmail;
    }

    /**
     * @return the fromEmail
     */
    public String getFromEmail() {
        return fromEmail;
    }

    /**
     * @param fromEmail
     *            the fromEmail to set
     */
    public void setFromEmail(String fromEmail) {
        this.fromEmail = fromEmail;
    }

    /**
     * @return the subject
     */
    public String getSubject() {
        return subject;
    }

    /**
     * @param subject
     *            the subject to set
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * @return the attachments
     */
    public String[] getAttachments() {
        return attachments;
    }

    /**
     * @param attachments
     *            the attachments to set
     */
    public void setAttachments(String[] attachments) {
        this.attachments = attachments;
    }

    /**
     * @return the filePath
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * @param filePath
     *            the filePath to set
     */
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    /**
     * @return the ccEmail
     */
    public String getCcEmail() {
        return ccEmail;
    }

    /**
     * @param ccEmail
     *            the ccEmail to set
     */
    public void setCcEmail(String ccEmail) {
        this.ccEmail = ccEmail;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}

