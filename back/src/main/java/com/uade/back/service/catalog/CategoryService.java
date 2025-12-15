package com.uade.back.service.catalog;

import java.util.List;
import com.uade.back.dto.catalog.*;

/**
 * Service interface for managing product categories.
 */
public interface CategoryService {
  /**
   * Retrieves a list of categories based on the request.
   *
   * @param request The category list request.
   * @return A list of category responses.
   */
  List<CategoryResponse> findAll(CategoryListRequest request);

  /**
   * Retrieves a category by its ID.
   *
   * @param id The ID of the category.
   * @return The category response.
   */
  CategoryResponse findById(Integer id);

  /**
   * Creates a new category.
   *
   * @param request The category creation request.
   * @return The created category response.
   */
  CategoryResponse create(CategoryRequest request);

  /**
   * Updates an existing category.
   *
   * @param request The category update request.
   * @return The updated category response.
   */
  CategoryResponse update(CategoryUpdateRequest request);

  /**
   * Deletes a category by its ID.
   *
   * @param id The ID of the category to delete.
   */
  void delete(Integer id);

  /**
   * Retrieves the category tree structure.
   *
   * @return A list of root categories with their children.
   */
  List<CategoryTreeDTO> getCategoryTree();
}
