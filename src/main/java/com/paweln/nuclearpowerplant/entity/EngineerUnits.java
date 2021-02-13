package com.paweln.nuclearpowerplant.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EngineerUnits {
    @Id
    @GeneratedValue
    private Long id;

    private String description;

    private String quantity;
}
