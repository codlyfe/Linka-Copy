package com.linka.backend.controller;

import com.linka.backend.entity.Category;
import com.linka.backend.entity.Listing;
import com.linka.backend.entity.User;
import com.linka.backend.repository.CategoryRepository;
import com.linka.backend.repository.ListingRepository;
import com.linka.backend.repository.UserRepository;
import com.linka.backend.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api/listings")
public class ListingController {

    @Autowired
    private ListingRepository listingRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private com.linka.backend.service.ImageUploadService imageUploadService;

    @GetMapping
    public ResponseEntity<?> getAllListings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        try {
            Sort sort = sortDir.equals("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            
            Page<Listing> listings = listingRepository.findByStatusOrderByCreatedAt(Listing.Status.ACTIVE, pageable);
            
            Map<String, Object> response = new HashMap<>();
            response.put("listings", listings.getContent());
            response.put("currentPage", listings.getNumber());
            response.put("totalPages", listings.getTotalPages());
            response.put("totalElements", listings.getTotalElements());
            response.put("hasNext", listings.hasNext());
            response.put("hasPrevious", listings.hasPrevious());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/featured")
    public ResponseEntity<?> getFeaturedListings() {
        try {
            List<Listing> listings = listingRepository.findFeaturedListings(Listing.Status.ACTIVE);
            return ResponseEntity.ok(listings);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/popular")
    public ResponseEntity<?> getPopularListings(
            @RequestParam(defaultValue = "10") int limit) {
        try {
            Pageable pageable = PageRequest.of(0, limit);
            List<Listing> listings = listingRepository.findPopularListings(Listing.Status.ACTIVE, pageable);
            return ResponseEntity.ok(listings);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/latest")
    public ResponseEntity<?> getLatestListings(
            @RequestParam(defaultValue = "10") int limit) {
        try {
            Pageable pageable = PageRequest.of(0, limit);
            List<Listing> listings = listingRepository.findLatestListings(Listing.Status.ACTIVE, pageable);
            return ResponseEntity.ok(listings);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/trending")
    public ResponseEntity<?> getTrendingListings() {
        try {
            LocalDateTime since = LocalDateTime.now().minusDays(7);
            List<Listing> listings = listingRepository.findTrendingListings(since, Listing.Status.ACTIVE);
            return ResponseEntity.ok(listings);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getListingById(@PathVariable Long id) {
        try {
            Optional<Listing> listing = listingRepository.findById(id);
            if (listing.isPresent()) {
                // Increment view count
                listing.get().incrementViewCount();
                listingRepository.save(listing.get());
                
                return ResponseEntity.ok(listing.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<?> getListingsByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Listing> listings = listingRepository.findByCategoryAndStatus(
                categoryId, Listing.Status.ACTIVE, pageable);
            
            Map<String, Object> response = new HashMap<>();
            response.put("listings", listings.getContent());
            response.put("currentPage", listings.getNumber());
            response.put("totalPages", listings.getTotalPages());
            response.put("totalElements", listings.getTotalElements());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchListings(
            @RequestParam String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Listing> listings = listingRepository.searchListings(search, Listing.Status.ACTIVE, pageable);
            
            Map<String, Object> response = new HashMap<>();
            response.put("listings", listings.getContent());
            response.put("currentPage", listings.getNumber());
            response.put("totalPages", listings.getTotalPages());
            response.put("totalElements", listings.getTotalElements());
            response.put("search", search);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/price-range")
    public ResponseEntity<?> getListingsByPriceRange(
            @RequestParam BigDecimal minPrice,
            @RequestParam BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Listing> listings = listingRepository.findByPriceRange(
                minPrice, maxPrice, Listing.Status.ACTIVE, pageable);
            
            Map<String, Object> response = new HashMap<>();
            response.put("listings", listings.getContent());
            response.put("currentPage", listings.getNumber());
            response.put("totalPages", listings.getTotalPages());
            response.put("totalElements", listings.getTotalElements());
            response.put("minPrice", minPrice);
            response.put("maxPrice", maxPrice);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/location")
    public ResponseEntity<?> getListingsByLocation(
            @RequestParam String location,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Listing> listings = listingRepository.findByLocation(location, Listing.Status.ACTIVE, pageable);
            
            Map<String, Object> response = new HashMap<>();
            response.put("listings", listings.getContent());
            response.put("currentPage", listings.getNumber());
            response.put("totalPages", listings.getTotalPages());
            response.put("totalElements", listings.getTotalElements());
            response.put("location", location);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getListingsByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Listing> listings = listingRepository.findBySellerAndStatus(
                userId, Listing.Status.ACTIVE, pageable);
            
            Map<String, Object> response = new HashMap<>();
            response.put("listings", listings.getContent());
            response.put("currentPage", listings.getNumber());
            response.put("totalPages", listings.getTotalPages());
            response.put("totalElements", listings.getTotalElements());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{id}/favorite")
    public ResponseEntity<?> toggleFavorite(@PathVariable Long id) {
        try {
            Optional<Listing> listingOpt = listingRepository.findById(id);
            if (listingOpt.isPresent()) {
                Listing listing = listingOpt.get();
                listing.incrementFavoriteCount();
                listingRepository.save(listing);
                
                return ResponseEntity.ok(Map.of(
                    "message", "Added to favorites",
                    "favoriteCount", listing.getFavoriteCount()
                ));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{id}/contact")
    public ResponseEntity<?> incrementContactCount(@PathVariable Long id) {
        try {
            Optional<Listing> listingOpt = listingRepository.findById(id);
            if (listingOpt.isPresent()) {
                Listing listing = listingOpt.get();
                listing.incrementContactCount();
                listingRepository.save(listing);
                
                return ResponseEntity.ok(Map.of(
                    "message", "Contact recorded",
                    "contactCount", listing.getContactCount()
                ));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> createListing(
            @RequestPart("data") String requestJson,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        
        try {
            // Parse the JSON string to ListingCreateRequest
            com.linka.backend.dto.ListingCreateRequest request;
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                request = objectMapper.readValue(requestJson, com.linka.backend.dto.ListingCreateRequest.class);
            } catch (Exception e) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid JSON format: " + e.getMessage()));
            }
            
            // Get authenticated user from SecurityContext
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "User not authenticated"));
            }
            
            String userEmail = authentication.getName();
            Optional<User> userOpt = userRepository.findByEmail(userEmail);
            
            if (!userOpt.isPresent()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "User not authenticated"));
            }
            
            User user = userOpt.get();
            
            // Validate category
            Optional<Category> categoryOpt = categoryRepository.findById(request.getCategoryId());
            if (!categoryOpt.isPresent()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid category"));
            }
            
            Category category = categoryOpt.get();
            
            // Create listing entity
            Listing listing = new Listing();
            listing.setTitle(request.getTitle());
            listing.setDescription(request.getDescription());
            listing.setPrice(request.getPrice());
            listing.setOriginalPrice(request.getOriginalPrice());
            listing.setCategory(category);
            listing.setSeller(user);
            listing.setListingType(request.getListingType());
            listing.setConditionType(request.getConditionType());
            listing.setLocation(request.getLocation());
            listing.setCity(request.getCity());
            listing.setDistrict(request.getDistrict());
            listing.setLatitude(request.getLatitude());
            listing.setLongitude(request.getLongitude());
            listing.setQuantityAvailable(request.getQuantityAvailable());
            listing.setMinOrderQuantity(request.getMinOrderQuantity());
            listing.setWeightKg(request.getWeightKg());
            listing.setDimensions(request.getDimensions());
            listing.setBrand(request.getBrand());
            listing.setModel(request.getModel());
            listing.setColor(request.getColor());
            listing.setSize(request.getSize());
            listing.setMaterial(request.getMaterial());
            listing.setSku(request.getSku());
            listing.setBarcode(request.getBarcode());
            listing.setTags(request.getTags());
            listing.setSeoTitle(request.getSeoTitle());
            listing.setSeoDescription(request.getSeoDescription());
            listing.setNegotiable(request.isNegotiable());
            listing.setAllowOffers(request.isAllowOffers());
            listing.setMinimumOffer(request.getMinimumOffer());
            listing.setAutoApproveOffers(request.isAutoApproveOffers());
            listing.setExpiresAt(request.getExpiresAt());
            listing.setStatus(Listing.Status.DRAFT);
            listing.setCreatedAt(java.time.LocalDateTime.now());
            listing.setUpdatedAt(java.time.LocalDateTime.now());
            
            // Save listing first to get ID
            Listing savedListing = listingRepository.save(listing);
            
            // Handle image uploads
            if (images != null && !images.isEmpty()) {
                try {
                    List<String> imageUrls = imageUploadService.uploadImages(images, savedListing.getId().toString());
                    if (!imageUrls.isEmpty()) {
                        savedListing.setImageUrls(imageUrls);
                        savedListing.setFeaturedImageIndex(request.getFeaturedImageIndex());
                        // Set main image based on featured image index
                        int featuredIndex = Math.min(request.getFeaturedImageIndex(), imageUrls.size() - 1);
                        savedListing.setMainImage(imageUrls.get(featuredIndex));
                        savedListing = listingRepository.save(savedListing);
                    }
                } catch (Exception e) {
                    return ResponseEntity.badRequest()
                        .body(Map.of("error", "Failed to upload images: " + e.getMessage()));
                }
            }
            
            // Update category item count
            category.incrementItemCount();
            categoryRepository.save(category);
            
            return ResponseEntity.ok(Map.of(
                "message", "Listing created successfully",
                "listing", savedListing
            ));
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Failed to create listing: " + e.getMessage()));
        }
    }
}