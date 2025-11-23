package com.linka.backend.repository;

import com.linka.backend.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findBySlug(String slug);

    boolean existsBySlug(String slug);

    boolean existsByName(String name);

    List<Category> findByActiveTrueOrderBySortOrderAsc();

    List<Category> findByFeaturedTrueAndActiveTrueOrderBySortOrderAsc();

    List<Category> findByParentCategoryIdIsNullOrderBySortOrderAsc();

    List<Category> findByParentCategoryIdOrderBySortOrderAsc(Long parentCategoryId);

    @Query("SELECT c FROM Category c WHERE c.active = true AND (c.name LIKE %:search% OR c.description LIKE %:search%) ORDER BY c.sortOrder ASC")
    Page<Category> searchActiveCategories(@Param("search") String search, Pageable pageable);

    @Query("SELECT c FROM Category c WHERE c.active = true ORDER BY c.itemCount DESC, c.name ASC")
    List<Category> findPopularCategories(Pageable pageable);

    @Query("SELECT c FROM Category c WHERE c.parentCategoryId IS NULL AND c.active = true ORDER BY c.sortOrder ASC")
    List<Category> findParentCategories();

    @Query("SELECT c FROM Category c WHERE c.parentCategoryId = :parentId AND c.active = true ORDER BY c.sortOrder ASC")
    List<Category> findSubcategories(@Param("parentId") Long parentId);

    @Query("SELECT c FROM Category c WHERE c.featured = true AND c.active = true ORDER BY c.sortOrder ASC")
    List<Category> findFeaturedCategories();

    @Query("SELECT c FROM Category c WHERE c.itemCount > 0 AND c.active = true ORDER BY c.itemCount DESC")
    List<Category> findCategoriesWithItems(Pageable pageable);
}