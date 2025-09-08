package com.uade.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uade.back.entity.Image;

@Repository
public interface ImageRepository extends JpaRepository<Image, Integer> {
}
