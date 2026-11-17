package com.youcode.covoiturage.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.youcode.covoiturage.entity.StopPoint;

/**
 * StopPointRepository
 */
public interface StopPointRepository extends JpaRepository<StopPoint, Long> {

}
