package com.uade.back.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uade.back.entity.UserInfo;


/**
 * Repository for accessing User Info data.
 */
@Repository
public interface UserInfoRepository extends JpaRepository<UserInfo, Integer> {

    /**
     * Finds user info by email address.
     *
     * @param mail The email address.
     * @return An Optional containing the user info.
     */
    Optional<UserInfo> findByMail(String mail);

}
