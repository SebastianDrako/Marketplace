package com.uade.back.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uade.back.entity.Address;
import com.uade.back.entity.Delivery;

/**
 * Repository for accessing Delivery data.
 */
@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Integer> {
    /**
     * Finds a delivery associated with a specific address.
     *
     * @param address The address.
     * @return An Optional containing the delivery.
     */
    Optional<Delivery> findByAddress(Address address);
}
