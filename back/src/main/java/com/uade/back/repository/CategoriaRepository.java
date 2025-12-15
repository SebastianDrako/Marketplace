package com.uade.back.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uade.back.entity.Categoria;


/**
 * Repository for accessing Category data.
 */
@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {
    /**
     * Finds a category by its ID.
     *
     * @param catId The category ID.
     * @return An Optional containing the category.
     */
    Optional<Categoria> findByCatId(Integer catId);

    /**
     * Finds categories by their parent category.
     *
     * @param parent The parent category.
     * @return A list of subcategories.
     */
    java.util.List<Categoria> findByParent(Categoria parent);

    /**
     * Finds top-level categories (no parent).
     *
     * @return A list of root categories.
     */
    java.util.List<Categoria> findByParentIsNull();
}
