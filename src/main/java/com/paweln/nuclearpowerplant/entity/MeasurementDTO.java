package com.paweln.nuclearpowerplant.entity;


import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
@Component
public class MeasurementDTO {
    private Float value;
    private LocalDateTime time;
    private String quality;

    public MeasurementDTO() {
    }

    public void init(Measurement measurement){
        this.value = measurement.getValue();
        this.time = measurement.getTime();
        this.quality = measurement.getQuality();
    }

    public Float getValue() {
        return value;
    }

    public void setValue(Float value) {
        this.value = value;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public MeasurementDTO(Float value, LocalDateTime time, String quality) {
        this.value = value;
        this.time = time;
        this.quality = quality;
    }


}
