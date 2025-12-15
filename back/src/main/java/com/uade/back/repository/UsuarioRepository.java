package com.uade.back.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uade.back.entity.Usuario;


/**
 * Repository for accessing User (Usuario) data.
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
 
    /**
     * Finds a user by their UserInfo ID.
     *
     * @param userInfoId The UserInfo ID.
     * @return An Optional containing the user.
     */
    Optional<Usuario> findByUserInfo_UserInfoId(Integer userInfoId);

    /**
     * Finds a user by their email address (via UserInfo).
     *
     * @param mail The email address.
     * @return An Optional containing the user.
     */
    Optional<Usuario> findByUserInfo_Mail(String mail);

}
