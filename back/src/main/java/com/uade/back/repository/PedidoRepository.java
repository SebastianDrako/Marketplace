package com.uade.back.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uade.back.entity.Pedido;
import com.uade.back.entity.Usuario;

/**
 * Repository for accessing Order (Pedido) data.
 */
@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Integer> {
    /**
     * Finds orders placed by a specific user.
     *
     * @param usuario The user.
     * @return A list of orders.
     */
    List<Pedido> findByUsuario(Usuario usuario);
}
