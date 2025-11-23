package com.linka.backend.controller;

import com.linka.backend.dto.AuthDtos.*;
import com.linka.backend.dto.ProfileUpdateRequest;
import com.linka.backend.entity.User;
import com.linka.backend.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            AuthResponse response = authService.register(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(java.util.Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(java.util.Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        try {
            authService.logout();
            return ResponseEntity.ok(java.util.Map.of("message", "Logged out successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(java.util.Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@Valid @RequestBody EmailVerificationRequest request) {
        try {
            authService.verifyEmail(request);
            return ResponseEntity.ok(java.util.Map.of("message", "Email verified successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(java.util.Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody PasswordChangeRequest request) {
        try {
            // In a real implementation, get email from JWT token
            String email = "user@example.com"; // Placeholder - should extract from token
            authService.changePassword(email, request);
            return ResponseEntity.ok(java.util.Map.of("message", "Password changed successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(java.util.Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody PasswordResetRequest request) {
        try {
            authService.resetPassword(request);
            return ResponseEntity.ok(java.util.Map.of("message", "Password reset instructions sent to your email"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(java.util.Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile() {
        try {
            var user = authService.getCurrentUser();
            java.util.Map<String, Object> profile = new java.util.HashMap<>();
            profile.put("id", user.getId());
            profile.put("firstName", user.getFirstName());
            profile.put("lastName", user.getLastName());
            profile.put("email", user.getEmail());
            profile.put("phoneNumber", user.getPhoneNumber());
            profile.put("location", user.getLocation());
            profile.put("city", user.getCity());
            profile.put("district", user.getDistrict());
            profile.put("userType", user.getUserType().name());
            profile.put("status", user.getStatus().name());
            profile.put("emailVerified", user.isEmailVerified());
            profile.put("phoneVerified", user.isPhoneVerified());
            profile.put("createdAt", user.getCreatedAt());
            profile.put("lastLogin", user.getLastLogin());
            
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(java.util.Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/check")
    public ResponseEntity<?> checkAuth() {
        try {
            var user = authService.getCurrentUser();
            java.util.Map<String, Object> response = new java.util.HashMap<>();
            response.put("authenticated", true);
            
            java.util.Map<String, Object> userMap = new java.util.HashMap<>();
            userMap.put("id", user.getId());
            userMap.put("email", user.getEmail());
            userMap.put("firstName", user.getFirstName());
            userMap.put("lastName", user.getLastName());
            userMap.put("userType", user.getUserType().name());
            
            response.put("user", userMap);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.ok(java.util.Map.of("authenticated", false));
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@Valid @RequestBody ProfileUpdateRequest request) {
        try {
            var user = authService.getCurrentUser();
            var updatedUser = authService.updateProfile(user.getId(), request);
            
            java.util.Map<String, Object> response = new java.util.HashMap<>();
            response.put("id", updatedUser.getId());
            response.put("firstName", updatedUser.getFirstName());
            response.put("lastName", updatedUser.getLastName());
            response.put("email", updatedUser.getEmail());
            response.put("phoneNumber", updatedUser.getPhoneNumber());
            response.put("location", updatedUser.getLocation());
            response.put("city", updatedUser.getCity());
            response.put("district", updatedUser.getDistrict());
            response.put("userType", updatedUser.getUserType().name());
            response.put("status", updatedUser.getStatus().name());
            response.put("emailVerified", updatedUser.isEmailVerified());
            response.put("phoneVerified", updatedUser.isPhoneVerified());
            response.put("createdAt", updatedUser.getCreatedAt());
            response.put("updatedAt", updatedUser.getUpdatedAt());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(java.util.Map.of("error", e.getMessage()));
        }
    }
}