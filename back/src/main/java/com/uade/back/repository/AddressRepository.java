package com.uade.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uade.back.entity.Address;

/**
 * Repository for accessing Address data.
 */
@Repository
public interface AddressRepository extends JpaRepository<Address, Integer> {
}
