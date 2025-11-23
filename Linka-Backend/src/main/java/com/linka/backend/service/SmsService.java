package com.linka.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SmsService {
    
    @Value("${app.sms.provider:twilio}")
    private String smsProvider;
    
    @Value("${app.sms.api.key:}")
    private String apiKey;
    
    public void sendVerificationSms(String phoneNumber, String message) {
        // In a real implementation, integrate with SMS provider like Twilio
        System.out.println("SMS to " + phoneNumber + ": " + message);
    }
    
    public void sendTransactionSms(String phoneNumber, String transactionDetails) {
        System.out.println("Transaction SMS to " + phoneNumber + ": " + transactionDetails);
    }
    
    public void sendPromotionalSms(String phoneNumber, String promotionMessage) {
        System.out.println("Promotion SMS to " + phoneNumber + ": " + promotionMessage);
    }
}