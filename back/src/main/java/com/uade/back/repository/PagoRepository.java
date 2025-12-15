package com.uade.back.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uade.back.entity.Pago;
import com.uade.back.entity.Pedido;

/**
 * Repository for accessing Payment (Pago) data.
 */
@Repository
public interface PagoRepository extends JpaRepository<Pago, Integer> {
    /**
     * Finds a payment associated with a specific order.
     *
     * @param pedido The order.
     * @return An Optional containing the payment.
     */
    Optional<Pago> findByPedido(Pedido pedido);
}
