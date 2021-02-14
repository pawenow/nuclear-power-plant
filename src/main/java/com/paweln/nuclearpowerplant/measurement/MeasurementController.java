package com.paweln.nuclearpowerplant.measurement;


import com.paweln.nuclearpowerplant.measurement.handler.MeasurementHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;

import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


import java.time.*;
import java.util.*;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/measurements")
public class MeasurementController {

    private final MeasurementService measurementService;
    private final MeasurementHandler measurementHandler;

    public enum QualityVal{
        GOOD("good"),
        BAD("bad");
        String val;
        QualityVal(String val) {
        this.val = val;
        }
    };

    @Autowired
    public MeasurementController(MeasurementService measurementService, MeasurementHandler measurementHandler) {
        this.measurementService = measurementService;
        this.measurementHandler = measurementHandler;
    }

    @GetMapping(value = "",params ={"limit","sort"} )
    public ResponseEntity getValuesByLimitAndSort(
            @RequestParam(name="limit",defaultValue = "1")String limit,
            @RequestParam(name="sort",defaultValue = "asc")String sort
        ){
            List<Measurement> values = measurementService.findObjectsByLimitAndSort(Integer.valueOf(limit),sort);
            if(values!=null && values.size()!=0){
                return new ResponseEntity<>(measurementHandler.initModelList(values), HttpStatus.OK);
            }else{
                return ResponseEntity.notFound().build();
            }
    }

    @GetMapping(value = "/average",params = {"startDate","endDate","quality"})
    public ResponseEntity getAvgBetweenTwoDatesByQuality(
            @RequestParam(name="startDate",defaultValue = "")String startDateString,
            @RequestParam(name="endDate",defaultValue = "")String endDateString,
            @RequestParam(name="quality",defaultValue = "",required = false) String quality){

        Pair<LocalDateTime, LocalDateTime> startEndDate = measurementHandler.initializeDates(startDateString, endDateString);

        List<Float> valuesBetweenTwoDatesByQuality = measurementService
                .findValuesBetweenTwoDatesByQuality(startEndDate.getFirst(),startEndDate.getSecond(), quality);
        OptionalDouble average = measurementHandler.getAverage(valuesBetweenTwoDatesByQuality);

        if(average.isPresent()){
            return new ResponseEntity<>((float)average.getAsDouble(),HttpStatus.OK);
        }else{
            return ResponseEntity.notFound().build();
        }
    }

    @RequestMapping(value="",method = RequestMethod.GET)
    public ResponseEntity getGoodValuesBetweenTwoDates(
            @RequestParam(name="startDate",defaultValue = "")String startDateString,
            @RequestParam(name="endDate",defaultValue = "")String endDateString){

        Pair<LocalDateTime, LocalDateTime> startEndDate = measurementHandler.initializeDates(startDateString, endDateString);
        List<Float> collect = measurementService.
                findValuesBetweenTwoDatesByQuality(startEndDate.getFirst(), startEndDate.getSecond(),QualityVal.GOOD.val);

        if(collect!= null && collect.size()!=0){
            return new ResponseEntity<>(collect,HttpStatus.OK);
        }else{
            return new ResponseEntity<>(Collections.emptyList(),HttpStatus.NOT_FOUND);
        }

    }

    @RequestMapping(value = "/interpolated",method = RequestMethod.GET)
    public ResponseEntity getInterpolatedValuesInIntervals(
            @RequestParam(name="startDate",defaultValue = "")String startDateString,
            @RequestParam(name="endDate",defaultValue = "")String endDateString,
            @RequestParam(name = "interval",defaultValue = "")String intervalTime){

        Pair<LocalDateTime, LocalDateTime> startEndDate = measurementHandler.initializeDates(startDateString, endDateString);

        List<Measurement> collect = measurementService.findObjectsBetweenTwoDates(startEndDate.getFirst(), startEndDate.getSecond())
                .stream()
                .sorted(Comparator.comparing(Measurement::getTime, Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());

        List<Double> interpolatedData = measurementHandler.getInterpolatedData(collect, intervalTime);

        PagedListHolder page = new PagedListHolder(interpolatedData);
        page.setPage(0);
        page.setPageSize(100);

        if(interpolatedData!=null && interpolatedData.size()!=0){
            return new ResponseEntity<>(page,HttpStatus.OK);
        }else{
            return ResponseEntity.notFound().build();
        }
    }



}
