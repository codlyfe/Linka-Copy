package com.linka.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    
    @Autowired
    private JavaMailSender emailSender;
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    public void sendVerificationEmail(String to, String firstName, String verificationLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject("Verify your LinkA Account");
        message.setText("Hello " + firstName + ",\n\n" +
                "Welcome to LinkA! Please verify your account by clicking the link below:\n" +
                verificationLink + "\n\n" +
                "If you didn't create this account, please ignore this email.\n\n" +
                "Best regards,\nLinkA Team");
        
        emailSender.send(message);
    }
    
    public void sendPasswordResetEmail(String to, String newPassword) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject("LinkA Password Reset");
        message.setText("Hello,\n\n" +
                "Your password has been reset. Here is your new password:\n" +
                newPassword + "\n\n" +
                "Please log in and change your password immediately.\n\n" +
                "Best regards,\nLinkA Team");
        
        emailSender.send(message);
    }
    
    public void sendTransactionConfirmation(String to, String itemName, String amount) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject("LinkA Transaction Confirmation");
        message.setText("Hello,\n\n" +
                "Your transaction has been processed successfully.\n\n" +
                "Item: " + itemName + "\n" +
                "Amount: " + amount + "\n\n" +
                "Thank you for using LinkA!\n\n" +
                "Best regards,\nLinkA Team");
        
        emailSender.send(message);
    }
}