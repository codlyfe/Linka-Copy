package com.linka.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
public class Review {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating cannot exceed 5")
    @Column(nullable = false)
    private Integer rating;
    
    @Column(columnDefinition = "TEXT")
    @Size(max = 1000, message = "Review content cannot exceed 1000 characters")
    private String comment;
    
    @Column(name = "pros")
    private String pros; // comma-separated pros
    
    @Column(name = "cons")
    private String cons; // comma-separated cons
    
    @Column(name = "helpful_count", nullable = false)
    private int helpfulCount = 0;
    
    @Column(name = "not_helpful_count", nullable = false)
    private int notHelpfulCount = 0;
    
    @Column(name = "is_verified_purchase", nullable = false)
    private boolean verifiedPurchase = false;
    
    @Column(name = "is_public", nullable = false)
    private boolean isPublic = true;
    
    @Column(name = "is_featured", nullable = false)
    private boolean featured = false;
    
    @Column(name = "admin_response")
    private String adminResponse;
    
    @Column(name = "admin_response_date")
    private LocalDateTime adminResponseDate;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "listing_id", nullable = false)
    private Listing listing;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_id", nullable = false)
    private User reviewer;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewee_id")
    private User reviewee; // The person being reviewed (seller or buyer)
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id")
    private Transaction transaction;
    
    // Constructors
    public Review() {}
    
    public Review(Integer rating, String comment, Listing listing, User reviewer) {
        this.rating = rating;
        this.comment = comment;
        this.listing = listing;
        this.reviewer = reviewer;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }
    
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    
    public String getPros() { return pros; }
    public void setPros(String pros) { this.pros = pros; }
    
    public String getCons() { return cons; }
    public void setCons(String cons) { this.cons = cons; }
    
    public int getHelpfulCount() { return helpfulCount; }
    public void setHelpfulCount(int helpfulCount) { this.helpfulCount = helpfulCount; }
    
    public int getNotHelpfulCount() { return notHelpfulCount; }
    public void setNotHelpfulCount(int notHelpfulCount) { this.notHelpfulCount = notHelpfulCount; }
    
    public boolean isVerifiedPurchase() { return verifiedPurchase; }
    public void setVerifiedPurchase(boolean verifiedPurchase) { this.verifiedPurchase = verifiedPurchase; }
    
    public boolean isPublic() { return isPublic; }
    public void setPublic(boolean isPublic) { this.isPublic = isPublic; }
    
    public boolean isFeatured() { return featured; }
    public void setFeatured(boolean featured) { this.featured = featured; }
    
    public String getAdminResponse() { return adminResponse; }
    public void setAdminResponse(String adminResponse) { this.adminResponse = adminResponse; }
    
    public LocalDateTime getAdminResponseDate() { return adminResponseDate; }
    public void setAdminResponseDate(LocalDateTime adminResponseDate) { this.adminResponseDate = adminResponseDate; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public Listing getListing() { return listing; }
    public void setListing(Listing listing) { this.listing = listing; }
    
    public User getReviewer() { return reviewer; }
    public void setReviewer(User reviewer) { this.reviewer = reviewer; }
    
    public User getReviewee() { return reviewee; }
    public void setReviewee(User reviewee) { this.reviewee = reviewee; }
    
    public Transaction getTransaction() { return transaction; }
    public void setTransaction(Transaction transaction) { this.transaction = transaction; }
    
    // Helper methods
    public String getStarDisplay() {
        StringBuilder stars = new StringBuilder();
        for (int i = 1; i <= 5; i++) {
            if (i <= rating) {
                stars.append("★");
            } else {
                stars.append("☆");
            }
        }
        return stars.toString();
    }
    
    public double getHelpfulPercentage() {
        int total = helpfulCount + notHelpfulCount;
        if (total == 0) return 0.0;
        return (double) helpfulCount / total * 100;
    }
    
    public void markAsVerifiedPurchase() {
        this.verifiedPurchase = true;
    }
    
    public void addHelpfulVote() {
        this.helpfulCount++;
    }
    
    public void addNotHelpfulVote() {
        this.notHelpfulCount++;
    }
    
    public void respondToReview(String response) {
        this.adminResponse = response;
        this.adminResponseDate = LocalDateTime.now();
    }
    
    public java.util.List<String> getProsList() {
        if (pros == null || pros.trim().isEmpty()) {
            return new java.util.ArrayList<>();
        }
        String[] prosArray = pros.split(",");
        java.util.List<String> prosList = new java.util.ArrayList<>();
        for (String pro : prosArray) {
            String trimmed = pro.trim();
            if (!trimmed.isEmpty()) {
                prosList.add(trimmed);
            }
        }
        return prosList;
    }
    
    public java.util.List<String> getConsList() {
        if (cons == null || cons.trim().isEmpty()) {
            return new java.util.ArrayList<>();
        }
        String[] consArray = cons.split(",");
        java.util.List<String> consList = new java.util.ArrayList<>();
        for (String con : consArray) {
            String trimmed = con.trim();
            if (!trimmed.isEmpty()) {
                consList.add(trimmed);
            }
        }
        return consList;
    }
    
    public void setProsFromList(java.util.List<String> prosList) {
        if (prosList == null || prosList.isEmpty()) {
            this.pros = null;
        } else {
            this.pros = String.join(",", prosList);
        }
    }
    
    public void setConsFromList(java.util.List<String> consList) {
        if (consList == null || consList.isEmpty()) {
            this.cons = null;
        } else {
            this.cons = String.join(",", consList);
        }
    }
}