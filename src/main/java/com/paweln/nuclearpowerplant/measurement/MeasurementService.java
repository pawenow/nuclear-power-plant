package com.paweln.nuclearpowerplant.measurement;


import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MeasurementService {
    private final MeasurementRepository measurementRepository;

    @Autowired
    public MeasurementService(MeasurementRepository measurementRepository) {
        this.measurementRepository = measurementRepository;
    }

    public Optional<Measurement> getLastValue(){
        return measurementRepository.findTopByOrderByTimeAsc();
    }

    public List<Measurement> findObjectsBetweenTwoDates(LocalDateTime startDate, LocalDateTime endDate){
        return measurementRepository.findObjectsBetweenTwoDates(startDate, endDate);

    }
    public List<Float> findValuesBetweenTwoDatesByQuality(LocalDateTime startDate, LocalDateTime endDate, String quality){
        List<Float> collect = findObjectsBetweenTwoDates(startDate, endDate)
                .stream()
                .filter(a -> quality.equalsIgnoreCase(a.getQuality()) || quality.equals(""))
                .map(Measurement::getValue)
                .collect(Collectors.toList());
        return collect;
    }

    public List<Measurement> findObjectsByLimitAndSort(Integer limit, String sort){
        Sort time = Sort.by("time");
        PageRequest sortAndLimit;
        if("desc".equals(sort)) {
             sortAndLimit = PageRequest.of(0, limit, time.ascending());
        }else{
             sortAndLimit =  PageRequest.of(0,limit,time.descending());
        }
        return measurementRepository.findObjectsByLimitAndSort(sortAndLimit).getContent();
    }

}
