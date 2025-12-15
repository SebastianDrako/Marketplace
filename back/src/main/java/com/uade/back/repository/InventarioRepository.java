package com.uade.back.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.uade.back.entity.Inventario;

/**
 * Repository for accessing Inventory (Product) data.
 */
@Repository
public interface InventarioRepository extends JpaRepository<Inventario, Integer> {
    /**
     * Finds an inventory item by its ID.
     *
     * @param itemId The item ID.
     * @return An Optional containing the inventory item.
     */
    Optional<Inventario>findByItemId(Integer itemId);

    /**
     * Searches for active products based on category and query string.
     *
     * @param categoryIds List of category IDs to include.
     * @param q           Query string for name or description.
     * @param pageable    Pagination information.
     * @return A page of inventory items.
     */
    @Query("SELECT i FROM Inventario i WHERE " +
           "(i.active = true) AND " +
           "(:categoryIds IS NULL OR i.categoria.catId IN :categoryIds) AND " +
           "(:q IS NULL OR LOWER(i.name) LIKE LOWER(CONCAT('%', :q, '%')) OR LOWER(i.description) LIKE LOWER(CONCAT('%', :q, '%')))")
    Page<Inventario> search(@Param("categoryIds") java.util.List<Integer> categoryIds, @Param("q") String q, Pageable pageable);

    /**
     * Searches for products with administrative filters (active status optional).
     *
     * @param categoryId Category ID filter.
     * @param q          Query string for name or description.
     * @param active     Active status filter.
     * @param pageable   Pagination information.
     * @return A page of inventory items.
     */
    @Query("SELECT i FROM Inventario i WHERE " +
            "(:categoryId IS NULL OR i.categoria.catId = :categoryId) AND " +
            "(:q IS NULL OR LOWER(i.name) LIKE LOWER(CONCAT('%', :q, '%')) OR LOWER(i.description) LIKE LOWER(CONCAT('%', :q, '%'))) AND " +
            "(:active IS NULL OR i.active = :active)")
    Page<Inventario> searchAdmin(@Param("categoryId") Integer categoryId, @Param("q") String q, @Param("active") Boolean active, Pageable pageable);
}
