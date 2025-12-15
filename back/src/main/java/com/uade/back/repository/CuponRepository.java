package com.uade.back.repository;

import com.uade.back.entity.Cupon;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository for accessing Coupon data.
 */
@Repository
public interface CuponRepository extends JpaRepository<Cupon, Integer> {
    /**
     * Finds a coupon by its code.
     *
     * @param codigo The coupon code.
     * @return An Optional containing the coupon.
     */
    Optional<Cupon> findByCodigo(String codigo);

    /**
     * Searches for coupons based on code and active status.
     *
     * @param codigo   The code to search for (partial match).
     * @param activo   The active status to filter by.
     * @param pageable Pagination information.
     * @return A page of coupons.
     */
    @Query(
        "SELECT c FROM Cupon c WHERE " +
            "(:codigo IS NULL OR LOWER(c.codigo) LIKE LOWER(CONCAT('%', :codigo, '%'))) AND " +
            "(:activo IS NULL OR c.activo = :activo)"
    )
    Page<Cupon> search(
        @Param("codigo") String codigo,
        @Param("activo") Boolean activo,
        Pageable pageable
    );
}
