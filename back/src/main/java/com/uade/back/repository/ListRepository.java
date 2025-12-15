package com.uade.back.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uade.back.entity.Inventario;
import com.uade.back.entity.List;

/**
 * Repository for accessing List (Cart Item) data.
 */
@Repository()
public interface ListRepository extends JpaRepository<List, Integer> {
    /**
     * Finds a list item by list ID and product item.
     *
     * @param listId The list ID (often used as cart item ID).
     * @param item   The product item.
     * @return An Optional containing the list item.
     */
    Optional<List> findByListIdAndItem(Integer listId, Inventario item);

    /**
     * Finds all items associated with a specific list ID.
     * Note: 'listId' here seems to refer to the primary key 'tlistId' based on entity definition or usage context,
     * but standard JPA method names usually map to properties. Assuming 'listId' property exists or is mapped.
     * If 'List' entity has 'listId' field, this is correct.
     *
     * @param listId The list ID.
     * @return A list of items.
     */
    java.util.List<List> findAllByListId(Integer listId);

    /**
     * Deletes all items with a specific list ID.
     *
     * @param listId The list ID.
     */
    void deleteAllByListId(Integer listId);
}
