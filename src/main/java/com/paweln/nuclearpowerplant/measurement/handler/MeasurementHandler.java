package com.paweln.nuclearpowerplant.measurement.handler;

import com.paweln.nuclearpowerplant.measurement.Measurement;
import com.paweln.nuclearpowerplant.measurement.model.MeasurementModel;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunctionLagrangeForm;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import javax.swing.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class MeasurementHandler {

    public MeasurementModel initModel(Measurement measurement){
        MeasurementModel measurementModel = new MeasurementModel();
        measurementModel.setQuality(measurement.getQuality());
        measurementModel.setTime(measurement.getTime());
        measurementModel.setValue(measurement.getValue());

        return measurementModel;
    }

    public List<MeasurementModel> initModelList(List<Measurement> measurement){
        List<MeasurementModel> collect = IntStream.rangeClosed(0, measurement.size() - 1)
                .mapToObj((a -> initModel(measurement.get(a))))
                .collect(Collectors.toList());
        return collect;
    }


    public List<Double> getInterpolatedData(List<Measurement> collect, String intervalTime){
        List<Double> interpolatedData = new ArrayList<>();
        Double intervalTimeInMilis = (double) Duration.ofMinutes(Long.valueOf(intervalTime)).toMillis();
        Function<LocalDateTime,Double> convertToMillis = a-> {
            return (double) (a.atZone(ZoneId.of("America/Los_Angeles")).toInstant().toEpochMilli());
        };

        double[] time = collect.stream().map(Measurement::getTime).map(convertToMillis).mapToDouble(Double::doubleValue).toArray();
        double[] values = collect.stream().map(a->a.getValue().doubleValue()).mapToDouble(Double::doubleValue).toArray();

        for(double i = time[0];i<time[time.length-1];i=i+intervalTimeInMilis){
            interpolatedData.add(PolynomialFunctionLagrangeForm.evaluate(time,values,i));
        }
        return interpolatedData;
    }

    public OptionalDouble getAverage(List<Float> collect){
        return collect.stream().map(Float::doubleValue).mapToDouble(Double::doubleValue).average();

    }

    public Pair<LocalDateTime,LocalDateTime> initializeDates(String startDateString, String endDateString){
        LocalDateTime startDate;
        LocalDateTime endDate;
        try{
            startDate = LocalDateTime.parse(startDateString);
            endDate = LocalDateTime.parse(endDateString);

        }catch(DateTimeParseException e){
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Cannot Parse To LocalDateTime, " +
                    "fill date in format YYYY-MM-DDTHH:MM:SS");
        }

        if(startDate.isAfter(endDate)){
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,"Start date is after end date.");
        }
        return Pair.of(startDate,endDate);
    }

}
