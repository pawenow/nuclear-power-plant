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
public class MeasurementController {

    @Autowired
    MeasurementRepository measurementRepository;

    @Autowired
    MeasurementDTO measurementDTO;

    @GetMapping("/getLastValue")
    public ResponseEntity<MeasurementDTO> getLastValue(){
        Measurement lastValue = measurementRepository.findTopByOrderByIdDesc();
        measurementDTO.init(lastValue);
        return new ResponseEntity<>(measurementDTO, HttpStatus.OK);
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
        measurementRepository.findObjectsBetweenTwoDates(startDate, endDate).stream().forEach(System.out::println);
            OptionalDouble average = measurementRepository.findObjectsBetweenTwoDates(startDate, endDate)
                .stream()
                .filter(a-> "good".equalsIgnoreCase(a.getQuality()) || includeBadValue&&"bad".equalsIgnoreCase(a.getQuality()))
                .mapToDouble(Measurement::getValue)
                .average();
        if(average.isPresent()){
            return new ResponseEntity<>(average.getAsDouble(),HttpStatus.OK);
        }else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
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

        List<Float> collect = measurementRepository.findObjectsBetweenTwoDates(startDate, endDate)
                .stream()
                .filter(a -> "good".equalsIgnoreCase(a.getQuality()))
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


        List<Double> interpolatedData = new ArrayList<>();
        Double intervalTimeInMilis = Double.valueOf(Duration.ofMinutes(Long.valueOf(intervalTime)).toMillis());
        Function<LocalDateTime,Double> convertToMilis = a-> {
            return (double) (a.atZone(ZoneId.of("America/Los_Angeles")).toInstant().toEpochMilli());
        };

        List<Measurement> collect = measurementRepository.findObjectsBetweenTwoDates(startDate, endDate).stream()
                .sorted(Comparator.comparing(Measurement::getTime, Comparator.nullsLast(Comparator.naturalOrder()))).collect(Collectors.toList());


        double[] time = collect.stream().map(Measurement::getTime).map(convertToMilis).mapToDouble(Double::doubleValue).toArray();
        double[] values = collect.stream().map(a->a.getValue().doubleValue()).mapToDouble(Double::doubleValue).toArray();

        for(double i = time[0];i<time[time.length-1];i=i+intervalTimeInMilis){
            interpolatedData.add(PolynomialFunctionLagrangeForm.evaluate(time,values,i));
        }
        return new ResponseEntity<>(interpolatedData,HttpStatus.OK);


    }



}
