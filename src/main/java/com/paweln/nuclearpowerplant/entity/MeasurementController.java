package com.paweln.nuclearpowerplant.entity;


import org.apache.commons.math3.analysis.polynomials.PolynomialFunctionLagrangeForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.*;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/measurements")
public class MeasurementController {


    private final MeasurementRepository measurementRepository;
    private final MeasurementService measurementService;
    private final MeasurementHandler measurementHandler;

    private static final String GOOD = "good";
    private static final String BAD = "bad";

    @Autowired
    public MeasurementController(MeasurementRepository measurementRepository, MeasurementService measurementService, MeasurementHandler measurementHandler) {
        this.measurementRepository = measurementRepository;
        this.measurementService = measurementService;
        this.measurementHandler = measurementHandler;
    }

    @GetMapping("")
    public ResponseEntity getLastValue(){
        Optional<Measurement> lastValue = measurementService.getLastValue();
        if(lastValue.isPresent()){
            return new ResponseEntity<>(measurementHandler.initModel(lastValue.get()), HttpStatus.OK);
        }else{
            return ResponseEntity.notFound().build();
        }

    }

    @GetMapping("/getAvgValueBetweenTwoDates")
    @ResponseBody
    public ResponseEntity getAvgValueBetweenTwoDates(
            @RequestParam(name="startDate",defaultValue = "")String startDateString,
            @RequestParam(name="endDate",defaultValue = "")String endDateString,
            @RequestParam(name="includeBad",defaultValue = "false",required = false)String includeBadValueString ){

        LocalDateTime startDate;
        LocalDateTime endDate;
        Boolean includeBadValue;
        try{
            startDate = LocalDateTime.parse(startDateString);
            endDate = LocalDateTime.parse(endDateString);
            includeBadValue = Boolean.valueOf(includeBadValueString);

        }catch(DateTimeParseException e){
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Cannot Parse To LocalDateTime, " +
                            "fill date in format YYYY-MM-DD HH:MM:SS");
        }
        List<Measurement> objectBetweenTwoDates = measurementService.findObjectBetweenTwoDates(startDate, endDate);
        OptionalDouble average = objectBetweenTwoDates
                .stream()
                .filter(a-> GOOD.equalsIgnoreCase(a.getQuality()) || includeBadValue&&BAD.equalsIgnoreCase(a.getQuality()))
                .mapToDouble(Measurement::getValue)
                .average();

        if(average.isPresent()){
            return new ResponseEntity<>((float)average.getAsDouble(),HttpStatus.OK);
        }else{
            return ResponseEntity.notFound().build();
        }


    }

    @RequestMapping(value="getGoodValuesBetweenTwoDates",method = RequestMethod.GET)
    public ResponseEntity getGoodValuesBetweenTwoDates(
            @RequestParam(name="startDate",defaultValue = "")String startDateString,
            @RequestParam(name="endDate",defaultValue = "")String endDateString){
        LocalDateTime startDate;
        LocalDateTime endDate;
        try{
            startDate = LocalDateTime.parse(startDateString);
            endDate = LocalDateTime.parse(endDateString);

        }catch(DateTimeParseException e){
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Cannot Parse To LocalDateTime, " +
                            "fill date in format YYYY-MM-DD HH:MM:SS");
        }
        List<Measurement> objectBetweenTwoDates = measurementService.findObjectBetweenTwoDates(startDate, endDate);
        List<Float> collect = objectBetweenTwoDates
                .stream()
                .filter(a -> GOOD.equalsIgnoreCase(a.getQuality()))
                .map(Measurement::getValue)
                .collect(Collectors.toList());

        if(collect!= null && collect.size()!=0){
            return new ResponseEntity<>(collect,HttpStatus.OK);
        }else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }

    @RequestMapping(value = "getInterpolatedValuesInIntervals",method = RequestMethod.GET)
    public ResponseEntity getInterpolatedValuesInIntervals(
            @RequestParam(name="startDate",defaultValue = "")String startDateString,
            @RequestParam(name="endDate",defaultValue = "")String endDateString,
            @RequestParam(name = "interval",defaultValue = "",required = true)String intervalTime){

        LocalDateTime startDate;
        LocalDateTime endDate;
        try{
            startDate = LocalDateTime.parse(startDateString);
            endDate = LocalDateTime.parse(endDateString);

        }catch(DateTimeParseException e){
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Cannot Parse To LocalDateTime, " +
                            "fill date in format YYYY-MM-DD HH:MM:SS");
        }
        if(startDate.isAfter(endDate)){
            return ResponseEntity
                    .badRequest()
                    .body("Start date is after end date.");
        }

        List<Measurement> collect = measurementRepository.findObjectsBetweenTwoDates(startDate, endDate).stream()
                .sorted(Comparator.comparing(Measurement::getTime, Comparator.nullsLast(Comparator.naturalOrder()))).collect(Collectors.toList());

        List<Double> interpolatedData = measurementHandler.getInterpolatedData(collect, intervalTime);

        if(interpolatedData!=null && interpolatedData.size()!=0){
            return new ResponseEntity<>(interpolatedData,HttpStatus.OK);
        }else{
            return ResponseEntity.notFound().build();
        }
    }



}
