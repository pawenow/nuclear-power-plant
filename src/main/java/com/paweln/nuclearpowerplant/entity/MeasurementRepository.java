package com.paweln.nuclearpowerplant.entity;


import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MeasurementRepository extends PagingAndSortingRepository<Measurement, Long> {
    Optional<Measurement> findTopByOrderByIdDesc();

    @Query("Select npp from Measurement npp WHERE npp.time>=:startDate and npp.time<=:endDate")
    List<Measurement> findObjectsBetweenTwoDates(@Param("startDate") LocalDateTime startDate,
                                                 @Param("endDate") LocalDateTime endDate);




}
