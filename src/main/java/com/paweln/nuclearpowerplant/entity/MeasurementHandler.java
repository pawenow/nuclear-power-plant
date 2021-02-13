package com.paweln.nuclearpowerplant.entity;

import org.apache.commons.math3.analysis.polynomials.PolynomialFunctionLagrangeForm;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Component
public class MeasurementHandler {

    public MeasurementModel initModel(Measurement measurement){
        MeasurementModel measurementModel = new MeasurementModel();
        measurementModel.setQuality(measurement.getQuality());
        measurementModel.setTime(measurement.getTime());
        measurementModel.setValue(measurement.getValue());

        return measurementModel;
    }

    public List<Double> getInterpolatedData(List<Measurement> collect, String intervalTime){
        List<Double> interpolatedData = new ArrayList<>();
        Double intervalTimeInMilis = Double.valueOf(Duration.ofMinutes(Long.valueOf(intervalTime)).toMillis());
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

}
