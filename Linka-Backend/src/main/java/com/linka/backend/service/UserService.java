package com.linka.backend.service;

import com.linka.backend.entity.User;
import com.linka.backend.entity.NotificationPreferences;
import com.linka.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private SmsService smsService;
    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }
    
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    public Optional<User> getUserByPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber);
    }
    
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    public boolean existsByPhoneNumber(String phoneNumber) {
        return userRepository.existsByPhoneNumber(phoneNumber);
    }
    
    public User createUser(User user) {
        // Validate email uniqueness
        if (existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + user.getEmail());
        }
        
        // Validate phone number uniqueness
        if (existsByPhoneNumber(user.getPhoneNumber())) {
            throw new IllegalArgumentException("Phone number already exists: " + user.getPhoneNumber());
        }
        
        // Encode password
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // Set default values
        user.setStatus(User.UserStatus.PENDING_VERIFICATION);
        user.setPreferredLanguage("en");
        user.setNotificationPreferences(new NotificationPreferences());
        
        User savedUser = userRepository.save(user);
        
        // Send verification email and SMS
        sendVerificationNotifications(savedUser);
        
        return savedUser;
    }
    
    public User updateUser(Long id, User userDetails) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
        
        // Update allowed fields
        if (userDetails.getFirstName() != null) {
            existingUser.setFirstName(userDetails.getFirstName());
        }
        if (userDetails.getLastName() != null) {
            existingUser.setLastName(userDetails.getLastName());
        }
        if (userDetails.getLocation() != null) {
            existingUser.setLocation(userDetails.getLocation());
        }
        if (userDetails.getCity() != null) {
            existingUser.setCity(userDetails.getCity());
        }
        if (userDetails.getDistrict() != null) {
            existingUser.setDistrict(userDetails.getDistrict());
        }
        if (userDetails.getProfileImage() != null) {
            existingUser.setProfileImage(userDetails.getProfileImage());
        }
        if (userDetails.getDateOfBirth() != null) {
            existingUser.setDateOfBirth(userDetails.getDateOfBirth());
        }
        if (userDetails.getGender() != null) {
            existingUser.setGender(userDetails.getGender());
        }
        if (userDetails.getPreferredLanguage() != null) {
            existingUser.setPreferredLanguage(userDetails.getPreferredLanguage());
        }
        if (userDetails.getNotificationPreferences() != null) {
            existingUser.setNotificationPreferences(userDetails.getNotificationPreferences());
        }
        
        return userRepository.save(existingUser);
    }
    
    public void updatePassword(Long id, String newPassword) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
        
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
    
    public void verifyEmail(Long userId, String verificationToken) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        
        if (user.isEmailVerified()) {
            throw new IllegalArgumentException("Email already verified");
        }
        
        // In a real implementation, verify the token matches
        user.setEmailVerified(true);
        
        // If phone is also verified, activate account
        if (user.isPhoneVerified()) {
            user.setStatus(User.UserStatus.ACTIVE);
        }
        
        userRepository.save(user);
    }
    
    public void verifyPhone(Long userId, String verificationCode) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        
        if (user.isPhoneVerified()) {
            throw new IllegalArgumentException("Phone already verified");
        }
        
        // In a real implementation, verify the code
        user.setPhoneVerified(true);
        
        // If email is also verified, activate account
        if (user.isEmailVerified()) {
            user.setStatus(User.UserStatus.ACTIVE);
        }
        
        userRepository.save(user);
    }
    
    public User authenticateUser(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));
        
        // Check if account is locked
        if (user.isAccountLocked()) {
            throw new IllegalArgumentException("Account is temporarily locked due to too many failed login attempts");
        }
        
        // Check account status
        if (user.getStatus() != User.UserStatus.ACTIVE) {
            throw new IllegalArgumentException("Account is not active: " + user.getStatus());
        }
        
        // Verify password
        if (!passwordEncoder.matches(password, user.getPassword())) {
            user.incrementFailedLoginAttempts();
            userRepository.save(user);
            throw new IllegalArgumentException("Invalid email or password");
        }
        
        // Successful login
        user.incrementLoginCount();
        userRepository.save(user);
        
        return user;
    }
    
    public void deactivateUser(Long id, String reason) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
        
        user.setStatus(User.UserStatus.DEACTIVATED);
        userRepository.save(user);
    }
    
    public void suspendUser(Long id, String reason) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
        
        user.setStatus(User.UserStatus.SUSPENDED);
        userRepository.save(user);
    }
    
    public void banUser(Long id, String reason) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
        
        user.setStatus(User.UserStatus.BANNED);
        userRepository.save(user);
    }
    
    public void activateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
        
        user.setStatus(User.UserStatus.ACTIVE);
        user.setAccountLockedUntil(null);
        user.setFailedLoginAttempts(0);
        userRepository.save(user);
    }
    
    public void resetPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));
        
        String resetToken = UUID.randomUUID().toString();
        String newPassword = generateRandomPassword();
        
        // In a real implementation, store the reset token with expiry
        // For now, directly reset password (not secure, but for demo)
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        
        // Send new password via email/SMS
        emailService.sendPasswordResetEmail(user.getEmail(), newPassword);
    }
    
    public List<User> getVerifiedSellers() {
        return userRepository.findByVerifiedSellerTrueAndStatus(User.UserStatus.ACTIVE);
    }
    
    public List<User> getTopRatedUsers(int limit) {
        return userRepository.findTopRatedUsers(limit);
    }
    
    public void updateUserRating(Long userId, Double newRating) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        
        // Recalculate average rating based on all reviews
        Double avgRating = userRepository.calculateAverageRating(userId);
        long totalReviews = userRepository.countTotalReviews(userId);
        
        user.setRatingAverage(avgRating);
        user.setRatingCount((int) totalReviews);
        
        userRepository.save(user);
    }
    
    public void incrementUserSales(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        
        user.setTotalSales(user.getTotalSales() + 1);
        userRepository.save(user);
    }
    
    public void incrementUserPurchases(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        
        user.setTotalPurchases(user.getTotalPurchases() + 1);
        userRepository.save(user);
    }
    
    private void sendVerificationNotifications(User user) {
        // Send email verification
        String verificationLink = "http://localhost:3000/verify/email/" + user.getId();
        emailService.sendVerificationEmail(user.getEmail(), user.getFirstName(), verificationLink);
        
        // Send SMS verification
        smsService.sendVerificationSms(user.getPhoneNumber(), "Welcome to LinkA! Please verify your account using this link: " + verificationLink);
    }
    
    private String generateRandomPassword() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
}