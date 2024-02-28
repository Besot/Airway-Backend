package org.airway.airwaybackend.service;

import org.airway.airwaybackend.dto.FlightSearchDto;
import org.airway.airwaybackend.model.Airport;

import java.time.LocalDate;
import java.util.List;

public interface FlightService {
    String deleteFlight(Long Id);
    List<FlightSearchDto> searchAvailableFlight(Airport departurePort, Airport arrivalPort, LocalDate departureDate, LocalDate returnDate, int noOfAdult, int noOfChildren, int noOfInfant);

}
