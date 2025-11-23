package com.linka.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class MobileMoneyController {

    // Uganda Mobile Money Providers
    private static final Map<String, Map<String, Object>> MOBILE_PROVIDERS = Map.of(
        "mtn", Map.of(
            "name", "MTN Mobile Money",
            "shortCode", "*165*3#",
            "color", "#FFCC00",
            "charges", 0.01,
            "minAmount", 500,
            "maxAmount", 5000000
        ),
        "airtel", Map.of(
            "name", "Airtel Money", 
            "shortCode", "*185*9#",
            "color", "#E60000",
            "charges", 0.015,
            "minAmount", 500,
            "maxAmount", 3000000
        ),
        "mula", Map.of(
            "name", "Mula by Stanbic",
            "shortCode", "*295*5#",
            "color", "#0066CC",
            "charges", 0.005,
            "minAmount", 1000,
            "maxAmount", 2000000
        )
    );

    @GetMapping("/mobile-money/providers")
    public ResponseEntity<Map<String, Object>> getMobileMoneyProviders() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", MOBILE_PROVIDERS);
        response.put("timestamp", LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/mobile-money/payment")
    public ResponseEntity<Map<String, Object>> initiateMobileMoneyPayment(
            @RequestBody Map<String, Object> paymentRequest) {
        
        String provider = (String) paymentRequest.get("provider");
        String phoneNumber = (String) paymentRequest.get("phoneNumber");
        Integer amount = (Integer) paymentRequest.get("amount");
        String reference = (String) paymentRequest.get("reference");
        
        Map<String, Object> response = new HashMap<>();
        
        // Validate inputs
        if (provider == null || phoneNumber == null || amount == null) {
            response.put("success", false);
            response.put("message", "Missing required fields: provider, phoneNumber, amount");
            response.put("timestamp", LocalDateTime.now());
            return ResponseEntity.badRequest().body(response);
        }
        
        // Validate provider
        if (!MOBILE_PROVIDERS.containsKey(provider)) {
            response.put("success", false);
            response.put("message", "Invalid mobile money provider");
            response.put("timestamp", LocalDateTime.now());
            return ResponseEntity.badRequest().body(response);
        }
        
        // Validate phone number (Uganda format)
        String cleanedPhone = phoneNumber.replaceAll("[\\s\\-\\(\\)]", "");
        if (!cleanedPhone.matches("^(\\+256|0)(7[0-9]{8}|3[0-9]{8})$")) {
            response.put("success", false);
            response.put("message", "Invalid Uganda phone number format");
            response.put("timestamp", LocalDateTime.now());
            return ResponseEntity.badRequest().body(response);
        }
        
        // Validate amount
        Map<String, Object> providerInfo = MOBILE_PROVIDERS.get(provider);
        Integer minAmount = (Integer) providerInfo.get("minAmount");
        Integer maxAmount = (Integer) providerInfo.get("maxAmount");
        
        if (amount < minAmount || amount > maxAmount) {
            response.put("success", false);
            response.put("message", String.format("Amount must be between %d and %d UGX", minAmount, maxAmount));
            response.put("timestamp", LocalDateTime.now());
            return ResponseEntity.badRequest().body(response);
        }
        
        // Calculate transaction fee
        Double charges = (Double) providerInfo.get("charges");
        Integer transactionFee = (int) Math.round(amount * charges);
        Integer totalAmount = amount + transactionFee;
        
        // Generate transaction ID
        long timestamp = System.currentTimeMillis();
        int random = (int) (Math.random() * 1000000);
        String transactionId = "TXN_" + timestamp + "_" + String.format("%06d", random);
        
        response.put("success", true);
        response.put("message", "Payment initiated successfully. Check your phone for the payment prompt.");
        response.put("data", Map.of(
            "transactionId", transactionId,
            "amount", amount,
            "transactionFee", transactionFee,
            "totalAmount", totalAmount,
            "provider", providerInfo.get("name"),
            "phoneNumber", phoneNumber,
            "reference", reference != null ? reference : "LINKA_" + transactionId
        ));
        response.put("timestamp", LocalDateTime.now());
        
        // In a real implementation, you would:
        // 1. Call the mobile money provider's API
        // 2. Store the transaction in your database
        // 3. Send confirmation SMS/notification
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/mobile-money/transaction/{transactionId}")
    public ResponseEntity<Map<String, Object>> getTransactionStatus(@PathVariable String transactionId) {
        Map<String, Object> response = new HashMap<>();
        
        // In a real implementation, query your database for transaction status
        // For demo purposes, return a mock response
        response.put("success", true);
        response.put("data", Map.of(
            "transactionId", transactionId,
            "status", "PENDING",
            "message", "Transaction is being processed",
            "provider", "MTN Mobile Money",
            "amount", 50000,
            "fee", 500,
            "total", 50500,
            "createdAt", LocalDateTime.now().minusMinutes(2)
        ));
        response.put("timestamp", LocalDateTime.now());
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/mobile-money/validate-phone/{phoneNumber}")
    public ResponseEntity<Map<String, Object>> validatePhoneNumber(@PathVariable String phoneNumber) {
        Map<String, Object> response = new HashMap<>();
        
        String cleanedPhone = phoneNumber.replaceAll("[\\s\\-\\(\\)]", "");
        boolean isValid = cleanedPhone.matches("^(\\+256|0)(7[0-9]{8}|3[0-9]{8})$");
        
        response.put("success", true);
        response.put("data", Map.of(
            "phoneNumber", phoneNumber,
            "isValid", isValid,
            "formatted", isValid ? formatUgandaPhone(cleanedPhone) : phoneNumber,
            "operator", isValid ? getOperator(cleanedPhone) : "Unknown"
        ));
        response.put("timestamp", LocalDateTime.now());
        
        return ResponseEntity.ok(response);
    }

    private String formatUgandaPhone(String phone) {
        if (phone.startsWith("256")) {
            return "+" + phone.substring(0, 3) + " " + 
                   phone.substring(3, 6) + " " + 
                   phone.substring(6);
        } else if (phone.startsWith("0")) {
            return "+256 " + phone.substring(1, 4) + " " + phone.substring(4);
        }
        return phone;
    }

    private String getOperator(String phone) {
        if (phone.startsWith("+25670") || phone.startsWith("070") || 
            phone.startsWith("+25675") || phone.startsWith("075")) {
            return "MTN";
        } else if (phone.startsWith("+25671") || phone.startsWith("071") ||
                   phone.startsWith("+25678") || phone.startsWith("078")) {
            return "Airtel";
        } else if (phone.startsWith("+25673") || phone.startsWith("073") ||
                   phone.startsWith("+25674") || phone.startsWith("074")) {
            return "UTL";
        }
        return "Unknown";
    }
}