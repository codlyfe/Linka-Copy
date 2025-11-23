package com.linka.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categories")
public class Category {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Category name is required")
    @Size(max = 100, message = "Category name cannot exceed 100 characters")
    @Column(nullable = false, unique = true)
    private String name;
    
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @NotBlank(message = "Category slug is required")
    @Column(name = "slug", nullable = false, unique = true)
    private String slug;
    
    @Column(name = "icon_name")
    private String iconName;
    
    @Column(name = "emoji_symbol")
    private String emojiSymbol;
    
    @Column(name = "gradient_class")
    private String gradientClass;
    
    @Column(name = "color_code")
    private String colorCode;
    
    @Column(name = "parent_category_id")
    private Long parentCategoryId;
    
    @Column(name = "sort_order", nullable = false)
    private int sortOrder = 0;
    
    @Column(name = "is_active", nullable = false)
    private boolean active = true;
    
    @Column(name = "is_featured", nullable = false)
    private boolean featured = false;
    
    @Column(name = "item_count", nullable = false)
    private int itemCount = 0;
    
    @Column(name = "meta_title")
    @Size(max = 60, message = "Meta title cannot exceed 60 characters")
    private String metaTitle;
    
    @Column(name = "meta_description")
    @Size(max = 160, message = "Meta description cannot exceed 160 characters")
    private String metaDescription;
    
    @Column(name = "seo_keywords")
    private String seoKeywords;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private java.time.LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private java.time.LocalDateTime updatedAt;
    
    @JsonIgnore
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Listing> listings = new ArrayList<>();
    
    @JsonIgnore
    @OneToMany(mappedBy = "parentCategory", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Category> subcategories = new ArrayList<>();
    
    @JsonIgnoreProperties({"subcategories", "parentCategory", "listings"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_category_id", insertable = false, updatable = false)
    private Category parentCategory;
    
    // Constructors
    public Category() {}
    
    public Category(String name, String description, String slug) {
        this.name = name;
        this.description = description;
        this.slug = slug;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }
    
    public String getIconName() { return iconName; }
    public void setIconName(String iconName) { this.iconName = iconName; }
    
    public String getEmojiSymbol() { return emojiSymbol; }
    public void setEmojiSymbol(String emojiSymbol) { this.emojiSymbol = emojiSymbol; }
    
    public String getGradientClass() { return gradientClass; }
    public void setGradientClass(String gradientClass) { this.gradientClass = gradientClass; }
    
    public String getColorCode() { return colorCode; }
    public void setColorCode(String colorCode) { this.colorCode = colorCode; }
    
    public Long getParentCategoryId() { return parentCategoryId; }
    public void setParentCategoryId(Long parentCategoryId) { this.parentCategoryId = parentCategoryId; }
    
    public int getSortOrder() { return sortOrder; }
    public void setSortOrder(int sortOrder) { this.sortOrder = sortOrder; }
    
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    
    public boolean isFeatured() { return featured; }
    public void setFeatured(boolean featured) { this.featured = featured; }
    
    public int getItemCount() { return itemCount; }
    public void setItemCount(int itemCount) { this.itemCount = itemCount; }
    
    public String getMetaTitle() { return metaTitle; }
    public void setMetaTitle(String metaTitle) { this.metaTitle = metaTitle; }
    
    public String getMetaDescription() { return metaDescription; }
    public void setMetaDescription(String metaDescription) { this.metaDescription = metaDescription; }
    
    public String getSeoKeywords() { return seoKeywords; }
    public void setSeoKeywords(String seoKeywords) { this.seoKeywords = seoKeywords; }
    
    public java.time.LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(java.time.LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public java.time.LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(java.time.LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public List<Listing> getListings() { return listings; }
    public void setListings(List<Listing> listings) { this.listings = listings; }
    
    public List<Category> getSubcategories() { return subcategories; }
    public void setSubcategories(List<Category> subcategories) { this.subcategories = subcategories; }
    
    public Category getParentCategory() { return parentCategory; }
    public void setParentCategory(Category parentCategory) { this.parentCategory = parentCategory; }
    
    // Helper methods
    public boolean isParent() {
        return parentCategoryId == null;
    }
    
    public boolean isLeaf() {
        return subcategories.isEmpty();
    }
    
    public String getFullPath() {
        if (isParent()) {
            return name;
        }
        // Avoid infinite recursion by limiting the depth
        return getFullPathWithDepthLimit(5);
    }
    
    private String getFullPathWithDepthLimit(int maxDepth) {
        if (maxDepth <= 0 || isParent()) {
            return name;
        }
        return (parentCategory != null ? parentCategory.getFullPathWithDepthLimit(maxDepth - 1) + " > " : "") + name;
    }
    
    public void incrementItemCount() {
        this.itemCount++;
    }
    
    public void decrementItemCount() {
        if (this.itemCount > 0) {
            this.itemCount--;
        }
    }
}