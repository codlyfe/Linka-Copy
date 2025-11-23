package com.linka.backend.service;

import com.linka.backend.config.JwtUtil;
import com.linka.backend.dto.AuthDtos.*;
import com.linka.backend.dto.ProfileUpdateRequest;
import com.linka.backend.entity.User;
import com.linka.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class AuthService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    @Lazy
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private SmsService smsService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }
        
        User user = userOpt.get();
        
        return new org.springframework.security.core.userdetails.User(
            user.getEmail(),
            user.getPassword(),
            user.getStatus() == User.UserStatus.ACTIVE && !user.isAccountLocked(),
            true, true, true,
            java.util.Collections.emptyList()
        );
    }

    public AuthResponse register(RegisterRequest request) {
        // Validate passwords match
        if (!request.getPassword().equals(request.getPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        // Check if user already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + request.getEmail());
        }

        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new IllegalArgumentException("Phone number already exists: " + request.getPhoneNumber());
        }

        // Create new user
        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setLocation(request.getLocation());
        user.setCity(request.getCity());
        user.setDistrict(request.getDistrict());
        user.setCountry(request.getCountry());
        user.setStatus(User.UserStatus.PENDING_VERIFICATION);
        user.setUserType(User.UserType.BUYER);

        User savedUser = userRepository.save(user);

        // Send verification notifications
        sendVerificationNotifications(savedUser);

        // Generate JWT token
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", savedUser.getId());
        claims.put("userType", savedUser.getUserType().name());
        String token = jwtUtil.generateToken(savedUser.getEmail(), claims);

        // Create response
        AuthResponse response = new AuthResponse(
            token,
            savedUser.getId(),
            savedUser.getEmail(),
            savedUser.getFirstName(),
            savedUser.getLastName(),
            savedUser.getPhoneNumber(),
            savedUser.getUserType().name(),
            savedUser.getStatus().name(),
            savedUser.isEmailVerified(),
            savedUser.isPhoneVerified()
        );
        response.setMessage("Registration successful. Please verify your email and phone number.");

        return response;
    }

    public AuthResponse login(LoginRequest request) {
        try {
            // Find user by email
            User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + request.getEmail()));

            // Check account status
            if (user.getStatus() != User.UserStatus.ACTIVE) {
                throw new IllegalArgumentException("Account is not active. Status: " + user.getStatus());
            }

            // Verify password manually
            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                throw new IllegalArgumentException("Invalid email or password");
            }

            // Update login statistics
            user.incrementLoginCount();
            userRepository.save(user);

            // Generate JWT token with additional claims
            Map<String, Object> claims = new HashMap<>();
            claims.put("userId", user.getId());
            claims.put("userType", user.getUserType().name());
            claims.put("fullName", user.getFullName());
            
            String token = jwtUtil.generateToken(user.getEmail(), claims);

            // Create response
            AuthResponse response = new AuthResponse(
                token,
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhoneNumber(),
                user.getUserType().name(),
                user.getStatus().name(),
                user.isEmailVerified(),
                user.isPhoneVerified()
            );
            response.setMessage("Login successful");

            return response;
        } catch (UsernameNotFoundException e) {
            throw new IllegalArgumentException("Invalid email or password", e);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid email or password", e);
        }
    }

    public void logout() {
        // In a stateless JWT implementation, logout is handled client-side
        // by removing the token from storage
        // This method can be used for logging purposes or token blacklisting
        SecurityContextHolder.clearContext();
    }

    public void verifyEmail(EmailVerificationRequest request) {
        try {
            userService.verifyEmail(request.getUserId(), request.getToken());
        } catch (Exception e) {
            throw new IllegalArgumentException("Email verification failed: " + e.getMessage(), e);
        }
    }

    public void changePassword(String email, PasswordChangeRequest request) {
        // Validate current password
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        // Validate new passwords match
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("New passwords do not match");
        }

        // Validate password strength (basic validation)
        if (request.getNewPassword().length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long");
        }

        // Update password
        userService.updatePassword(user.getId(), request.getNewPassword());
    }

    public void resetPassword(PasswordResetRequest request) {
        try {
            userService.resetPassword(request.getEmail());
        } catch (Exception e) {
            // For security, don't reveal if email exists or not
            // Just return success response
        }
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            String email = ((UserDetails) authentication.getPrincipal()).getUsername();
            return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        }
        throw new IllegalStateException("No authenticated user found");
    }

    public User getUserByToken(String token) {
        try {
            String email = jwtUtil.extractUsername(token);
            return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid or expired token", e);
        }
    }

    public User updateProfile(Long userId, ProfileUpdateRequest request) {
        try {
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
            
            // Check if email is already taken by another user
            if (!user.getEmail().equals(request.getEmail()) && 
                userRepository.existsByEmail(request.getEmail())) {
                throw new IllegalArgumentException("Email already exists: " + request.getEmail());
            }
            
            // Check if phone number is already taken by another user
            if (request.getPhoneNumber() != null && 
                !request.getPhoneNumber().equals(user.getPhoneNumber()) && 
                userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
                throw new IllegalArgumentException("Phone number already exists: " + request.getPhoneNumber());
            }
            
            // Update user fields
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            user.setEmail(request.getEmail());
            user.setPhoneNumber(request.getPhoneNumber());
            user.setLocation(request.getLocation());
            user.setCity(request.getCity());
            user.setDistrict(request.getDistrict());
            
            // Save updated user
            User updatedUser = userRepository.save(user);
            
            return updatedUser;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to update profile: " + e.getMessage(), e);
        }
    }

    private void sendVerificationNotifications(User user) {
        try {
            String verificationLink = "http://localhost:3000/verify/email/" + user.getId();
            emailService.sendVerificationEmail(user.getEmail(), user.getFirstName(), verificationLink);
            smsService.sendVerificationSms(user.getPhoneNumber(), 
                "Welcome to LinkA! Please verify your account using this link: " + verificationLink);
        } catch (Exception e) {
            // Log error but don't fail registration
            System.err.println("Failed to send verification notifications: " + e.getMessage());
        }
    }
}