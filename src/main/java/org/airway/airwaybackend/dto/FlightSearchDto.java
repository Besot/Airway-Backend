package org.airway.airwaybackend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.airway.airwaybackend.enums.FlightDirection;
import org.airway.airwaybackend.enums.FlightStatus;
import org.airway.airwaybackend.model.Airline;
import org.airway.airwaybackend.model.Airport;
import org.airway.airwaybackend.model.Classes;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FlightSearchDto {
    private Long id;
    private FlightDirection flightDirection;
    private FlightStatus flightStatus;
    private String flightNo;
    @JsonProperty("airline")
    private Airline airline;
    private LocalDate arrivalDate;
    private LocalDate departureDate;
    private LocalTime arrivalTime;
    private LocalTime departureTime;
    private LocalDate returnDate;
    private LocalTime returnTime;
    private int totalSeat;
    private int availableSeat;
    private int noOfChildren;
    private int noOfAdult;
    private int noOfInfant;
    private long duration;

    private Airport  arrivalPortName;
    private Airport departurePortName;
    private List<ClassDto> classes;
}
