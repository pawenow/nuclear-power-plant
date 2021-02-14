package com.paweln.nuclearpowerplant.measurement.model;


import java.time.LocalDateTime;

public class MeasurementModel {
    private Float value;
    private LocalDateTime time;
    private String quality;

    public MeasurementModel() {
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

    public MeasurementModel(Float value, LocalDateTime time, String quality) {
        this.value = value;
        this.time = time;
        this.quality = quality;
    }


}
