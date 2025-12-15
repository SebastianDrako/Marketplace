package com.uade.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uade.back.entity.Config;

/**
 * Repository for accessing Configuration data.
 */
@Repository
public interface ConfigRepository extends JpaRepository<Config, String> {
}
