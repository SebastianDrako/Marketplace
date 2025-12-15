package com.uade.back.controller;

import java.util.List;

import com.uade.back.dto.catalog.CategoryTreeDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uade.back.dto.catalog.CategoryListRequest;
import com.uade.back.dto.catalog.CategoryRequest;
import com.uade.back.dto.catalog.CategoryResponse;
import com.uade.back.dto.catalog.CategoryUpdateRequest;
import com.uade.back.service.catalog.CategoryService;

import lombok.RequiredArgsConstructor;

/**
 * Controller for managing product categories.
 */
@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

  private final CategoryService service;

  /**
   * Lists categories based on a search request.
   *
   * @param request The search criteria.
   * @return A list of categories matching the criteria.
   */
  @PostMapping("/search")
  public ResponseEntity<List<CategoryResponse>> list(@RequestBody CategoryListRequest request) {
    return ResponseEntity.ok(service.findAll(request));
  }

  /**
   * Retrieves a category by its ID.
   *
   * @param id The ID of the category.
   * @return The category details.
   */
  @GetMapping("/byid/{id}")
  public ResponseEntity<CategoryResponse> get(@PathVariable Integer id) {
    return ResponseEntity.ok(service.findById(id));
  }

  /**
   * Creates a new category.
   *
   * @param request The category creation details.
   * @return The created category.
   */
  @PostMapping
  public ResponseEntity<CategoryResponse> create(@RequestBody CategoryRequest request) {
    return ResponseEntity.ok(service.create(request));
  }

  /**
   * Updates an existing category.
   *
   * @param request The category update details.
   * @return The updated category.
   */
  @PutMapping
  public ResponseEntity<CategoryResponse> update(@RequestBody CategoryUpdateRequest request) {
    return ResponseEntity.ok(service.update(request));
  }

  /**
   * Deletes a category.
   *
   * @param id The ID of the category to delete.
   * @return A response entity with no content.
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Integer id) {
    service.delete(id);
    return ResponseEntity.noContent().build();
  }

  /**
   * Retrieves the category tree structure.
   *
   * @return A list representing the category tree.
   */
  @GetMapping("/tree")
  public ResponseEntity<List<CategoryTreeDTO>> getCategoryTree() {
    return ResponseEntity.ok(service.getCategoryTree());
  }
}
