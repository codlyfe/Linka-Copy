package com.linka.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "listing_attributes")
public class ListingAttribute {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Attribute name is required")
    @Size(max = 100, message = "Attribute name cannot exceed 100 characters")
    @Column(name = "attribute_name", nullable = false)
    private String attributeName;
    
    @NotBlank(message = "Attribute value is required")
    @Size(max = 500, message = "Attribute value cannot exceed 500 characters")
    @Column(name = "attribute_value", nullable = false)
    private String attributeValue;
    
    @Column(name = "display_order", nullable = false)
    private int displayOrder = 0;
    
    @Column(name = "is_searchable", nullable = false)
    private boolean searchable = false;
    
    @Column(name = "attribute_type")
    @Enumerated(EnumType.STRING)
    private AttributeType attributeType = AttributeType.TEXT;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "listing_id", nullable = false)
    private Listing listing;
    
    // Constructors
    public ListingAttribute() {}
    
    public ListingAttribute(String attributeName, String attributeValue, Listing listing) {
        this.attributeName = attributeName;
        this.attributeValue = attributeValue;
        this.listing = listing;
    }
    
    public enum AttributeType {
        TEXT,
        NUMBER,
        BOOLEAN,
        SELECT,
        MULTISELECT,
        COLOR,
        SIZE,
        WEIGHT,
        DIMENSION,
        DATE
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getAttributeName() { return attributeName; }
    public void setAttributeName(String attributeName) { this.attributeName = attributeName; }
    
    public String getAttributeValue() { return attributeValue; }
    public void setAttributeValue(String attributeValue) { this.attributeValue = attributeValue; }
    
    public int getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(int displayOrder) { this.displayOrder = displayOrder; }
    
    public boolean isSearchable() { return searchable; }
    public void setSearchable(boolean searchable) { this.searchable = searchable; }
    
    public AttributeType getAttributeType() { return attributeType; }
    public void setAttributeType(AttributeType attributeType) { this.attributeType = attributeType; }
    
    public Listing getListing() { return listing; }
    public void setListing(Listing listing) { this.listing = listing; }
    
    // Helper methods
    public String getFormattedValue() {
        switch (attributeType) {
            case COLOR:
                return "🎨 " + attributeValue;
            case SIZE:
                return "📏 " + attributeValue;
            case WEIGHT:
                return "⚖️ " + attributeValue + " kg";
            case DIMENSION:
                return "📐 " + attributeValue;
            case DATE:
                return "📅 " + attributeValue;
            case BOOLEAN:
                return Boolean.parseBoolean(attributeValue) ? "✅ Yes" : "❌ No";
            default:
                return attributeValue;
        }
    }
}