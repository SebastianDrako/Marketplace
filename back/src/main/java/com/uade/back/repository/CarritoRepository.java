package com.uade.back.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uade.back.entity.Carro;
import com.uade.back.entity.Usuario;


/**
 * Repository for accessing Cart data.
 */
@Repository()
public interface CarritoRepository extends JpaRepository<Carro, Integer> {

    /**
     * Finds carts by user.
     *
     * @param user The user.
     * @return A list of carts associated with the user.
     */
    List<Carro> findByUser(Usuario user);

}
