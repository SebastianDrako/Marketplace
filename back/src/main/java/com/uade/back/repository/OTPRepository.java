package com.uade.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uade.back.entity.Otp;

/**
 * Repository for accessing OTP data.
 */
@Repository
public interface OTPRepository extends JpaRepository<Otp, Integer> {

}
