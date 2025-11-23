package com.linka.backend.controller;

import com.linka.backend.entity.Category;
import com.linka.backend.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping
    public ResponseEntity<?> getAllCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(required = false) String search) {
        
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Category> categories;
            
            if (search != null && !search.trim().isEmpty()) {
                categories = categoryRepository.searchActiveCategories(search, pageable);
            } else {
                categories = categoryRepository.findAll(pageable);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("categories", categories.getContent());
            response.put("currentPage", categories.getNumber());
            response.put("totalPages", categories.getTotalPages());
            response.put("totalElements", categories.getTotalElements());
            response.put("hasNext", categories.hasNext());
            response.put("hasPrevious", categories.hasPrevious());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/active")
    public ResponseEntity<?> getActiveCategories() {
        try {
            List<Category> categories = categoryRepository.findByActiveTrueOrderBySortOrderAsc();
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/featured")
    public ResponseEntity<?> getFeaturedCategories() {
        try {
            List<Category> categories = categoryRepository.findFeaturedCategories();
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/popular")
    public ResponseEntity<?> getPopularCategories(
            @RequestParam(defaultValue = "10") int limit) {
        try {
            Pageable pageable = PageRequest.of(0, limit);
            List<Category> categories = categoryRepository.findPopularCategories(pageable);
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/parents")
    public ResponseEntity<?> getParentCategories() {
        try {
            List<Category> categories = categoryRepository.findParentCategories();
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}/subcategories")
    public ResponseEntity<?> getSubcategories(@PathVariable Long id) {
        try {
            List<Category> subcategories = categoryRepository.findSubcategories(id);
            return ResponseEntity.ok(subcategories);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{slug}")
    public ResponseEntity<?> getCategoryBySlug(@PathVariable String slug) {
        try {
            Optional<Category> category = categoryRepository.findBySlug(slug);
            if (category.isPresent()) {
                return ResponseEntity.ok(category.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCategoryById(@PathVariable Long id) {
        try {
            Optional<Category> category = categoryRepository.findById(id);
            if (category.isPresent()) {
                return ResponseEntity.ok(category.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/with-items")
    public ResponseEntity<?> getCategoriesWithItems(
            @RequestParam(defaultValue = "20") int limit) {
        try {
            Pageable pageable = PageRequest.of(0, limit);
            List<Category> categories = categoryRepository.findCategoriesWithItems(pageable);
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchCategories(
            @RequestParam String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Category> categories = categoryRepository.searchActiveCategories(search, pageable);
            
            Map<String, Object> response = new HashMap<>();
            response.put("categories", categories.getContent());
            response.put("currentPage", categories.getNumber());
            response.put("totalPages", categories.getTotalPages());
            response.put("totalElements", categories.getTotalElements());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
}