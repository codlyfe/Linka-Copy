package com.linka.backend.dto;

import com.linka.backend.entity.Listing;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public class ListingCreateRequest {
    
    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title cannot exceed 200 characters")
    private String title;
    
    @Size(max = 5000, message = "Description cannot exceed 5000 characters")
    private String description;
    
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private BigDecimal price;
    
    @DecimalMin(value = "0.01", message = "Original price must be greater than 0")
    private BigDecimal originalPrice;
    
    @NotNull(message = "Category is required")
    private Long categoryId;
    
    @NotNull(message = "Listing type is required")
    private Listing.ListingType listingType;
    
    @NotNull(message = "Condition is required")
    private Listing.ConditionType conditionType;
    
    @NotBlank(message = "Location is required")
    private String location;
    
    private String city;
    private String district;
    
    private BigDecimal latitude;
    private BigDecimal longitude;
    
    private Integer quantityAvailable = 1;
    private Integer minOrderQuantity = 1;
    private BigDecimal weightKg;
    private String dimensions;
    private String brand;
    private String model;
    private String color;
    private String size;
    private String material;
    private String sku;
    private String barcode;
    private String tags;
    
    @Size(max = 60, message = "SEO title cannot exceed 60 characters")
    private String seoTitle;
    
    @Size(max = 160, message = "SEO description cannot exceed 160 characters")
    private String seoDescription;
    
    private boolean negotiable = false;
    private boolean allowOffers = true;
    private BigDecimal minimumOffer;
    private boolean autoApproveOffers = false;
    
    private java.time.LocalDateTime expiresAt;
    
    // Contact information (separate from user profile)
    @NotBlank(message = "Contact name is required")
    private String contactName;
    
    @NotBlank(message = "Contact phone is required")
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format")
    private String contactPhone;
    
    private String contactEmail;
    
    // Image handling - images are passed as separate @RequestPart in controller
    private int featuredImageIndex = 0;
    
    // Default constructor
    public ListingCreateRequest() {}
    
    // Getters and Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    
    public BigDecimal getOriginalPrice() { return originalPrice; }
    public void setOriginalPrice(BigDecimal originalPrice) { this.originalPrice = originalPrice; }
    
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    
    public Listing.ListingType getListingType() { return listingType; }
    public void setListingType(Listing.ListingType listingType) { this.listingType = listingType; }
    
    public Listing.ConditionType getConditionType() { return conditionType; }
    public void setConditionType(Listing.ConditionType conditionType) { this.conditionType = conditionType; }
    
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
    
    public boolean isNegotiable() { return negotiable; }
    public void setNegotiable(boolean negotiable) { this.negotiable = negotiable; }
    
    public boolean isAllowOffers() { return allowOffers; }
    public void setAllowOffers(boolean allowOffers) { this.allowOffers = allowOffers; }
    
    public BigDecimal getMinimumOffer() { return minimumOffer; }
    public void setMinimumOffer(BigDecimal minimumOffer) { this.minimumOffer = minimumOffer; }
    
    public boolean isAutoApproveOffers() { return autoApproveOffers; }
    public void setAutoApproveOffers(boolean autoApproveOffers) { this.autoApproveOffers = autoApproveOffers; }
    
    public java.time.LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(java.time.LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
    
    public String getContactName() { return contactName; }
    public void setContactName(String contactName) { this.contactName = contactName; }
    
    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }
    
    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }
    
    public int getFeaturedImageIndex() { return featuredImageIndex; }
    public void setFeaturedImageIndex(int featuredImageIndex) { this.featuredImageIndex = featuredImageIndex; }
}