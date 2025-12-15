package com.uade.back.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.uade.back.dto.catalog.ProductPageResponse;
import com.uade.back.dto.catalog.ProductRequest;
import com.uade.back.dto.catalog.ProductResponse;
import com.uade.back.service.product.ProductService;

import lombok.RequiredArgsConstructor;

/**
 * Controller for managing products.
 */
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService service;

    /**
     * Searches for products.
     *
     * @param categoryId Optional category ID filter.
     * @param q          Optional search query.
     * @param page       Page number (default 0).
     * @param size       Page size (default 20).
     * @return A paginated response of products.
     */
    @GetMapping
    public ResponseEntity<ProductPageResponse> search(
        @RequestParam(required = false) Integer categoryId,
        @RequestParam(required = false) String q,
        @RequestParam(defaultValue = "0") Integer page,
        @RequestParam(defaultValue = "20") Integer size) {
        return ResponseEntity.ok(service.search(categoryId, q, page, size));
    }

    /**
     * Retrieves a product by its ID.
     *
     * @param id The ID of the product.
     * @return The product details.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> get(@PathVariable Integer id) {
    return ResponseEntity.ok(service.getById(id));
    }

    /**
     * Creates a new product.
     *
     * @param request The product creation details.
     * @return The created product.
     */
    @PostMapping
    public ResponseEntity<ProductResponse> create(@RequestBody ProductRequest request) {
    return ResponseEntity.ok(service.create(request));
    }

    /**
     * Updates an existing product.
     *
     * @param id      The ID of the product to update.
     * @param request The product update details.
     * @return The updated product.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> update(@PathVariable Integer id, @RequestBody ProductRequest request) {
    return ResponseEntity.ok(service.update(id, request));
    }

    /**
     * Deletes a product.
     *
     * @param id The ID of the product to delete.
     * @return A response entity with no content.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
    return ResponseEntity.noContent().build();
    }
}
