package com.example.otp.notify;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class EmailSender {
    private final JavaMailSender mailSender;

    @Value("${otp.mail.from:noreply@example.com}")
    private String from;

    public EmailSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendOtp(String toEmail, String code) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(from);
        msg.setTo(toEmail);
        msg.setSubject("Your OTP Code");
        msg.setText("Your OTP code is: " + code + " . It expires in 5 minutes.");
        try {
            mailSender.send(msg);
        } catch (Exception ex) {
            System.out.println("Email sending failed: " + ex.getMessage());
        }
    }
}
