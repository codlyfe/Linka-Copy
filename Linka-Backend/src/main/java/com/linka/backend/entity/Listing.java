package com.linka.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "listings")
public class Listing {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title cannot exceed 200 characters")
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    @Size(max = 5000, message = "Description cannot exceed 5000 characters")
    private String description;
    
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    @Column(name = "price", nullable = false, precision = 15, scale = 2)
    private BigDecimal price;
    
    @Column(name = "original_price", precision = 15, scale = 2)
    private BigDecimal originalPrice;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "listing_type", nullable = false)
    private ListingType listingType = ListingType.SELL;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "condition_type", nullable = false)
    private ConditionType conditionType = ConditionType.NEW;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status = Status.DRAFT;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;
    
    @Column(name = "location")
    private String location;
    
    @Column(name = "city")
    private String city;
    
    @Column(name = "district")
    private String district;
    
    @Column(name = "latitude", precision = 10, scale = 8)
    private BigDecimal latitude;
    
    @Column(name = "longitude", precision = 11, scale = 8)
    private BigDecimal longitude;
    
    @Column(name = "main_image")
    private String mainImage;
    
    @ElementCollection
    @CollectionTable(name = "listing_images", joinColumns = @JoinColumn(name = "listing_id"))
    @Column(name = "image_url")
    private List<String> imageUrls = new ArrayList<>();
    
    @Column(name = "featured_image_index", nullable = false)
    private int featuredImageIndex = 0;
    
    @Column(name = "quantity_available")
    private Integer quantityAvailable = 1;
    
    @Column(name = "min_order_quantity")
    private Integer minOrderQuantity = 1;
    
    @Column(name = "weight_kg", precision = 8, scale = 3)
    private BigDecimal weightKg;
    
    @Column(name = "dimensions")
    private String dimensions; // e.g., "30x20x10 cm"
    
    @Column(name = "brand")
    private String brand;
    
    @Column(name = "model")
    private String model;
    
    @Column(name = "color")
    private String color;
    
    @Column(name = "size")
    private String size;
    
    @Column(name = "material")
    private String material;
    
    @Column(name = "sku")
    private String sku;
    
    @Column(name = "barcode")
    private String barcode;
    
    @Column(name = "tags")
    private String tags; // comma-separated tags
    
    @Column(name = "seo_title")
    @Size(max = 60, message = "SEO title cannot exceed 60 characters")
    private String seoTitle;
    
    @Column(name = "seo_description")
    @Size(max = 160, message = "SEO description cannot exceed 160 characters")
    private String seoDescription;
    
    @Column(name = "view_count", nullable = false)
    private int viewCount = 0;
    
    @Column(name = "favorite_count", nullable = false)
    private int favoriteCount = 0;
    
    @Column(name = "contact_count", nullable = false)
    private int contactCount = 0;
    
    @Column(name = "is_negotiable", nullable = false)
    private boolean negotiable = false;
    
    @Column(name = "is_featured", nullable = false)
    private boolean featured = false;
    
    @Column(name = "is_premium", nullable = false)
    private boolean premium = false;
    
    @Column(name = "allow_offers", nullable = false)
    private boolean allowOffers = true;
    
    @Column(name = "minimum_offer", precision = 15, scale = 2)
    private BigDecimal minimumOffer;
    
    @Column(name = "auto_approve_offers", nullable = false)
    private boolean autoApproveOffers = false;
    
    @Column(name = "featured_until")
    private LocalDateTime featuredUntil;
    
    @Column(name = "premium_until")
    private LocalDateTime premiumUntil;
    
    @Column(name = "expires_at")
    private LocalDateTime expiresAt;
    
    @Column(name = "published_at")
    private LocalDateTime publishedAt;
    
    @Column(name = "archived_at")
    private LocalDateTime archivedAt;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "listing", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Transaction> transactions = new ArrayList<>();
    
    @OneToMany(mappedBy = "listing", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Review> reviews = new ArrayList<>();
    
    @OneToMany(mappedBy = "listing", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ListingAttribute> attributes = new ArrayList<>();
    
    // Constructors
    public Listing() {}
    
    public Listing(String title, String description, BigDecimal price, Category category, User seller) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.category = category;
        this.seller = seller;
    }
    
    // Enum definitions
    public enum ListingType {
        SELL,
        RENT,
        BUY,
        SERVICE,
        JOB,
        EVENT
    }
    
    public enum ConditionType {
        NEW,
        LIKE_NEW,
        GOOD,
        FAIR,
        POOR,
        REFURBISHED
    }
    
    public enum Status {
        DRAFT,
        PENDING_APPROVAL,
        ACTIVE,
        PAUSED,
        SOLD,
        EXPIRED,
        REJECTED,
        ARCHIVED,
        DELETED
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    
    public BigDecimal getOriginalPrice() { return originalPrice; }
    public void setOriginalPrice(BigDecimal originalPrice) { this.originalPrice = originalPrice; }
    
    public ListingType getListingType() { return listingType; }
    public void setListingType(ListingType listingType) { this.listingType = listingType; }
    
    public ConditionType getConditionType() { return conditionType; }
    public void setConditionType(ConditionType conditionType) { this.conditionType = conditionType; }
    
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
    
    public User getSeller() { return seller; }
    public void setSeller(User seller) { this.seller = seller; }
    
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    
    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }
    
    public BigDecimal getLatitude() { return latitude; }
    public void setLatitude(BigDecimal latitude) { this.latitude = latitude; }
    
    public BigDecimal getLongitude() { return longitude; }
    public void setLongitude(BigDecimal longitude) { this.longitude = longitude; }
    
    public String getMainImage() { return mainImage; }
    public void setMainImage(String mainImage) { this.mainImage = mainImage; }
    
    public List<String> getImageUrls() { return imageUrls; }
    public void setImageUrls(List<String> imageUrls) { this.imageUrls = imageUrls; }
    
    public int getFeaturedImageIndex() { return featuredImageIndex; }
    public void setFeaturedImageIndex(int featuredImageIndex) { this.featuredImageIndex = featuredImageIndex; }
    
    public Integer getQuantityAvailable() { return quantityAvailable; }
    public void setQuantityAvailable(Integer quantityAvailable) { this.quantityAvailable = quantityAvailable; }
    
    public Integer getMinOrderQuantity() { return minOrderQuantity; }
    public void setMinOrderQuantity(Integer minOrderQuantity) { this.minOrderQuantity = minOrderQuantity; }
    
    public BigDecimal getWeightKg() { return weightKg; }
    public void setWeightKg(BigDecimal weightKg) { this.weightKg = weightKg; }
    
    public String getDimensions() { return dimensions; }
    public void setDimensions(String dimensions) { this.dimensions = dimensions; }
    
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    
    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }
    
    public String getMaterial() { return material; }
    public void setMaterial(String material) { this.material = material; }
    
    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
    
    public String getBarcode() { return barcode; }
    public void setBarcode(String barcode) { this.barcode = barcode; }
    
    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }
    
    public String getSeoTitle() { return seoTitle; }
    public void setSeoTitle(String seoTitle) { this.seoTitle = seoTitle; }
    
    public String getSeoDescription() { return seoDescription; }
    public void setSeoDescription(String seoDescription) { this.seoDescription = seoDescription; }
    
    public int getViewCount() { return viewCount; }
    public void setViewCount(int viewCount) { this.viewCount = viewCount; }
    
    public int getFavoriteCount() { return favoriteCount; }
    public void setFavoriteCount(int favoriteCount) { this.favoriteCount = favoriteCount; }
    
    public int getContactCount() { return contactCount; }
    public void setContactCount(int contactCount) { this.contactCount = contactCount; }
    
    public boolean isNegotiable() { return negotiable; }
    public void setNegotiable(boolean negotiable) { this.negotiable = negotiable; }
    
    public boolean isFeatured() { return featured; }
    public void setFeatured(boolean featured) { this.featured = featured; }
    
    public boolean isPremium() { return premium; }
    public void setPremium(boolean premium) { this.premium = premium; }
    
    public boolean isAllowOffers() { return allowOffers; }
    public void setAllowOffers(boolean allowOffers) { this.allowOffers = allowOffers; }
    
    public BigDecimal getMinimumOffer() { return minimumOffer; }
    public void setMinimumOffer(BigDecimal minimumOffer) { this.minimumOffer = minimumOffer; }
    
    public boolean isAutoApproveOffers() { return autoApproveOffers; }
    public void setAutoApproveOffers(boolean autoApproveOffers) { this.autoApproveOffers = autoApproveOffers; }
    
    public LocalDateTime getFeaturedUntil() { return featuredUntil; }
    public void setFeaturedUntil(LocalDateTime featuredUntil) { this.featuredUntil = featuredUntil; }
    
    public LocalDateTime getPremiumUntil() { return premiumUntil; }
    public void setPremiumUntil(LocalDateTime premiumUntil) { this.premiumUntil = premiumUntil; }
    
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
    
    public LocalDateTime getPublishedAt() { return publishedAt; }
    public void setPublishedAt(LocalDateTime publishedAt) { this.publishedAt = publishedAt; }
    
    public LocalDateTime getArchivedAt() { return archivedAt; }
    public void setArchivedAt(LocalDateTime archivedAt) { this.archivedAt = archivedAt; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public List<Transaction> getTransactions() { return transactions; }
    public void setTransactions(List<Transaction> transactions) { this.transactions = transactions; }
    
    public List<Review> getReviews() { return reviews; }
    public void setReviews(List<Review> reviews) { this.reviews = reviews; }
    
    public List<ListingAttribute> getAttributes() { return attributes; }
    public void setAttributes(List<ListingAttribute> attributes) { this.attributes = attributes; }
    
    // Helper methods
    public boolean isActive() {
        return status == Status.ACTIVE && (expiresAt == null || expiresAt.isAfter(LocalDateTime.now()));
    }
    
    public boolean isExpired() {
        return expiresAt != null && expiresAt.isBefore(LocalDateTime.now());
    }
    
    public boolean isFeaturedExpired() {
        return featuredUntil != null && featuredUntil.isBefore(LocalDateTime.now());
    }
    
    public boolean isPremiumExpired() {
        return premiumUntil != null && premiumUntil.isBefore(LocalDateTime.now());
    }
    
    public String getDiscountPercentage() {
        if (originalPrice != null && price != null && originalPrice.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal discount = originalPrice.subtract(price)
                .divide(originalPrice, 2, RoundingMode.HALF_UP)
                .setScale(2, RoundingMode.HALF_UP);
            return discount.multiply(BigDecimal.valueOf(100)).toString() + "%";
        }
        return "0%";
    }
    
    public boolean hasDiscount() {
        return originalPrice != null && price != null && price.compareTo(originalPrice) < 0;
    }
    
    public void incrementViewCount() {
        this.viewCount++;
    }
    
    public void incrementFavoriteCount() {
        this.favoriteCount++;
    }
    
    public void incrementContactCount() {
        this.contactCount++;
    }
    
    public List<String> getTagList() {
        if (tags == null || tags.trim().isEmpty()) {
            return new ArrayList<>();
        }
        String[] tagArray = tags.split(",");
        List<String> tagList = new ArrayList<>();
        for (String tag : tagArray) {
            String trimmed = tag.trim();
            if (!trimmed.isEmpty()) {
                tagList.add(trimmed);
            }
        }
        return tagList;
    }
    
    public void setTagsFromList(List<String> tagList) {
        if (tagList == null || tagList.isEmpty()) {
            this.tags = null;
        } else {
            this.tags = String.join(",", tagList);
        }
    }
}