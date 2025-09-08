package com.uade.back.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uade.back.entity.Pedido;
import com.uade.back.entity.Usuario;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Integer> {
    List<Pedido> findByUsuario(Usuario usuario);
}
