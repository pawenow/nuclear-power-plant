package com.paweln.nuclearpowerplant.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Measurement {

    @Id
    @GeneratedValue
    private Long id;

    private Float value;

    private LocalDateTime time;

    /*@ManyToOne
    @JoinColumn(name = "engineer_units_id")*/
    private String engineerUnit;
    /*
    @ManyToOne
    @JoinColumn(name = "quality_id")*/
    private String quality;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getEngineerUnit() {
        return engineerUnit;
    }

    public void setEngineerUnit(String engineerUnit) {
        this.engineerUnit = engineerUnit;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }
    /*
    public EngineerUnits getEngineerUnit() {
        return engineerUnit;
    }

    public void setEngineerUnit(EngineerUnits engineerUnit) {
        this.engineerUnit = engineerUnit;
    }

    public Quality getQuality() {
        return quality;
    }

    public void setQuality(Quality quality) {
        this.quality = quality;
    }*/
}
