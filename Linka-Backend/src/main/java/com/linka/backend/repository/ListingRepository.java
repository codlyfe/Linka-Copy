package com.linka.backend.repository;

import com.linka.backend.entity.Category;
import com.linka.backend.entity.Listing;
import com.linka.backend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ListingRepository extends JpaRepository<Listing, Long> {

    List<Listing> findByStatus(Listing.Status status);

    List<Listing> findBySeller(User seller);

    List<Listing> findByCategory(Category category);

    List<Listing> findByStatusAndFeaturedTrue(Listing.Status status);

    @Query("SELECT l FROM Listing l WHERE l.status = :status ORDER BY l.createdAt DESC")
    Page<Listing> findByStatusOrderByCreatedAt(@Param("status") Listing.Status status, Pageable pageable);

    @Query("SELECT l FROM Listing l WHERE l.seller.id = :sellerId AND l.status = :status ORDER BY l.createdAt DESC")
    Page<Listing> findBySellerAndStatus(@Param("sellerId") Long sellerId, @Param("status") Listing.Status status, Pageable pageable);

    @Query("SELECT l FROM Listing l WHERE l.category.id = :categoryId AND l.status = :status ORDER BY l.createdAt DESC")
    Page<Listing> findByCategoryAndStatus(@Param("categoryId") Long categoryId, @Param("status") Listing.Status status, Pageable pageable);

    @Query("SELECT l FROM Listing l WHERE l.status = :status AND l.featured = true ORDER BY l.featuredUntil DESC NULLS LAST")
    List<Listing> findFeaturedListings(@Param("status") Listing.Status status);

    @Query("SELECT l FROM Listing l WHERE l.title LIKE %:search% OR l.description LIKE %:search% OR l.tags LIKE %:search% AND l.status = :status ORDER BY l.createdAt DESC")
    Page<Listing> searchListings(@Param("search") String search, @Param("status") Listing.Status status, Pageable pageable);

    @Query("SELECT l FROM Listing l WHERE l.price BETWEEN :minPrice AND :maxPrice AND l.status = :status ORDER BY l.price ASC")
    Page<Listing> findByPriceRange(@Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice, @Param("status") Listing.Status status, Pageable pageable);

    @Query("SELECT l FROM Listing l WHERE l.location LIKE %:location% AND l.status = :status ORDER BY l.createdAt DESC")
    Page<Listing> findByLocation(@Param("location") String location, @Param("status") Listing.Status status, Pageable pageable);

    @Query("SELECT l FROM Listing l WHERE l.createdAt >= :since AND l.status = :status ORDER BY l.viewCount DESC")
    List<Listing> findTrendingListings(@Param("since") LocalDateTime since, @Param("status") Listing.Status status);

    @Query("SELECT l FROM Listing l WHERE l.status = :status ORDER BY l.viewCount DESC, l.favoriteCount DESC")
    List<Listing> findPopularListings(@Param("status") Listing.Status status, Pageable pageable);

    @Query("SELECT l FROM Listing l WHERE l.expiresAt <= :now AND l.status = :status")
    List<Listing> findExpiredListings(@Param("now") LocalDateTime now, @Param("status") Listing.Status status);

    @Query("SELECT l FROM Listing l WHERE l.featuredUntil <= :now AND l.status = :status")
    List<Listing> findExpiredFeaturedListings(@Param("now") LocalDateTime now, @Param("status") Listing.Status status);

    @Query("SELECT COUNT(l) FROM Listing l WHERE l.seller.id = :sellerId AND l.status = :status")
    Long countBySellerAndStatus(@Param("sellerId") Long sellerId, @Param("status") Listing.Status status);

    @Query("SELECT l FROM Listing l WHERE l.status = :status ORDER BY l.createdAt DESC")
    List<Listing> findLatestListings(@Param("status") Listing.Status status, Pageable pageable);
}