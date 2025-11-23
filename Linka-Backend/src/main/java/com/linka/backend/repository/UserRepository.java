package com.linka.backend.repository;

import com.linka.backend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByPhoneNumber(String phoneNumber);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    List<User> findByStatus(User.UserStatus status);

    List<User> findByUserType(User.UserType userType);

    List<User> findByVerifiedSellerTrueAndStatus(User.UserStatus status);

    List<User> findByLocation(String location);

    List<User> findByCity(String city);

    @Query(value = "SELECT * FROM users WHERE rating_average > 0 ORDER BY rating_average DESC, rating_count DESC LIMIT ?1", nativeQuery = true)
    List<User> findTopRatedUsers(int limit);

    @Query("SELECT u FROM User u ORDER BY u.createdAt DESC")
    Page<User> findAllOrderByCreatedAt(Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.firstName LIKE %:search% OR u.lastName LIKE %:search% OR u.email LIKE %:search%")
    Page<User> searchUsers(@Param("search") String search, Pageable pageable);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.reviewer.id = :userId")
    Long countTotalReviews(@Param("userId") Long userId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.reviewee.id = :userId")
    Double calculateAverageRating(@Param("userId") Long userId);

    @Query("SELECT u FROM User u WHERE u.lastLogin IS NOT NULL ORDER BY u.lastLogin DESC")
    List<User> findRecentlyActiveUsers(Pageable pageable);
}