package com.uade.back.service.catalog;

import com.uade.back.dto.catalog.*;
import com.uade.back.entity.Categoria;
import com.uade.back.repository.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementation of the CategoryService.
 */
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoriaRepository categoriaRepository;

    /**
     * Retrieves categories, optionally filtering by parent ID.
     *
     * @param request The request containing the parent ID (optional).
     * @return A list of category responses.
     */
    @Override
    public List<CategoryResponse> findAll(CategoryListRequest request) {
        List<Categoria> categorias;
        if (request.parentId() == null) {
            categorias = categoriaRepository.findByParentIsNull();
        } else {
            
            
            Categoria parent = categoriaRepository.findById(request.parentId().intValue())
                    .orElseThrow(() -> new RuntimeException("Parent category not found with id: " + request.parentId()));
            categorias = categoriaRepository.findByParent(parent);
        }

        return categorias.stream()
                .map(this::toCategoryResponse)
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Retrieves a category by its ID.
     *
     * @param id The ID of the category.
     * @return The category response.
     */
    @Override
    public CategoryResponse findById(Integer id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
        return toCategoryResponse(categoria);
    }

    /**
     * Creates a new category.
     *
     * @param request The category creation details.
     * @return The created category response.
     */
    @Override
    public CategoryResponse create(CategoryRequest request) {
        Categoria parent = null;
        if (request.getParentId() != 0) {
            parent = categoriaRepository.findById(request.getParentId().intValue())
                    .orElseThrow(() -> new RuntimeException("Parent category not found with id: " + request.getParentId()));
        }


        Categoria newCategoria = Categoria.builder()
                .name(request.getName())
                .parent(parent)
                .build();

        Categoria savedCategoria = categoriaRepository.save(newCategoria);
        return toCategoryResponse(savedCategoria);
    }

    /**
     * Helper method to convert a Categoria entity to a CategoryResponse DTO.
     *
     * @param categoria The category entity.
     * @return The CategoryResponse DTO.
     */
    private CategoryResponse toCategoryResponse(Categoria categoria) {
        if (categoria == null) {
            return null;
        }
        return new CategoryResponse(
                categoria.getCatId(),
                categoria.getName(),
                categoria.getParent() != null ? categoria.getParent().getCatId() : null
        );
    }

    /**
     * Updates an existing category.
     *
     * @param request The category update details.
     * @return The updated category response.
     */
    @Override
    @Transactional
    public CategoryResponse update(CategoryUpdateRequest request) {
        Categoria category = categoriaRepository.findById(request.id())
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + request.id()));

        if (request.name() != null) {
            category.setName(request.name());
        }

        if (request.parentId() != null) {
            if (request.parentId() == 0) {
                category.setParent(null);
            } else {
                Categoria parent = categoriaRepository.findById(request.parentId())
                        .orElseThrow(() -> new RuntimeException("Parent category not found with id: " + request.parentId()));
                category.setParent(parent);
            }
        }

        Categoria savedCategory = categoriaRepository.save(category);
        return toCategoryResponse(savedCategory);
    }

    /**
     * Deletes a category by its ID.
     *
     * @param id The ID of the category to delete.
     */
    @Override
    @Transactional
    public void delete(Integer id) {
        if (!categoriaRepository.existsById(id)) {
            throw new RuntimeException("Category not found with id: " + id);
        }
        categoriaRepository.deleteById(id);
    }

    /**
     * Retrieves the entire category tree.
     *
     * @return A list of root CategoryTreeDTOs, each containing their children.
     */
    @Override
    public List<CategoryTreeDTO> getCategoryTree() {
        List<Categoria> allCategories = categoriaRepository.findAll();
        Map<Integer, CategoryTreeDTO> categoryMap = allCategories.stream()
                .map(cat -> CategoryTreeDTO.builder()
                        .id(cat.getCatId())
                        .name(cat.getName())
                        .children(new ArrayList<>())
                        .build())
                .collect(Collectors.toMap(CategoryTreeDTO::getId, cat -> cat));

        List<CategoryTreeDTO> rootCategories = new ArrayList<>();

        allCategories.forEach(cat -> {
            CategoryTreeDTO node = categoryMap.get(cat.getCatId());
            if (cat.getParent() == null) {
                rootCategories.add(node);
            } else {
                CategoryTreeDTO parentNode = categoryMap.get(cat.getParent().getCatId());
                if (parentNode != null) {
                    parentNode.getChildren().add(node);
                }
            }
        });

        return rootCategories;
    }
}
