package org.airway.airwaybackend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Airport {

    @Id
    private String iataCode;
    private String name;
    private String icaoCode;
    private String city;
    private String operationalHrs;
    private String state;



}