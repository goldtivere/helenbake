package com.helenbake.helenbake.dto;

import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * Created by Toyin on 5/6/19.
 */
public class MailData {
    private String from;
    private String to;
    private String subject;
    private String text;
    private String fileName;
    private InputStreamSource inputStreamResource;
    private JavaMailSender mailSender;

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public InputStreamSource getInputStreamResource() {
        return inputStreamResource;
    }

    public void setInputStreamResource(InputStreamSource inputStreamResource) {
        this.inputStreamResource = inputStreamResource;
    }

    public MailData(String to, String subject, String text, JavaMailSender mailSender) {
        this.to = to;
        this.subject = subject;
        this.text = text;
        this.mailSender = mailSender;
    }

    public MailData(String to, String subject, String text, JavaMailSender mailSender,
                    String fileName, InputStreamSource inputStreamResource) {
        this.to = to;
        this.subject = subject;
        this.text = text;
        this.mailSender = mailSender;
        this.fileName = fileName;
        this.inputStreamResource = inputStreamResource;
    }

    public MimeMessage toMimeMessage() throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper =
                new MimeMessageHelper(message, true);
        helper.setFrom(from);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text, true);
//        try {
//            InputStreamSource imageSource =
//                    new ByteArrayResource(IOUtils.toByteArray(getClass().getResourceAsStream("/images/logo.png")));
//            helper.addInline("logo.png", imageSource, "image/png");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        if (inputStreamResource != null) {
            helper.addAttachment(fileName, inputStreamResource);
        }

        return message;
    }

    public void sendMessage() throws MessagingException {
        mailSender.send(toMimeMessage());
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

}
