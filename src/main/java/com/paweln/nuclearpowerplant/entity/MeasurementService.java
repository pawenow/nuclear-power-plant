package com.paweln.nuclearpowerplant.entity;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;

@Service
public class MeasurementService {
    private final MeasurementRepository measurementRepository;
    private final MeasurementModel measurementModel;

    @Autowired
    public MeasurementService(MeasurementRepository measurementRepository, MeasurementModel measurementModel) {
        this.measurementRepository = measurementRepository;
        this.measurementModel = measurementModel;
    }

    public Optional<Measurement> getLastValue(){
        return measurementRepository.findTopByOrderByIdDesc();
    }

    public List<Measurement> findObjectBetweenTwoDates(LocalDateTime startDate, LocalDateTime endDate){
        return measurementRepository.findObjectsBetweenTwoDates(startDate, endDate);

    }

}
