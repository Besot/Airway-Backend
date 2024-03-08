package org.airway.airwaybackend.service;

import org.airway.airwaybackend.dto.FlightSearchDto;
import org.airway.airwaybackend.dto.FlightSearchResponse;
import org.airway.airwaybackend.enums.FlightDirection;
import org.airway.airwaybackend.model.Airport;
import org.airway.airwaybackend.model.Flight;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface FlightService {
    String deleteFlight(Long Id);
    Map<String, FlightSearchResponse> searchAvailableFlight(Airport departurePort, Airport arrivalPort, LocalDate departureDate, FlightDirection flightDirection, LocalDate returnDate, int noOfAdult, int noOfChildren, int noOfInfant) ;
     FlightSearchResponse getReturningFlights(Airport departurePort, Airport arrivalPort, LocalDate returnDate, int noOfAdult, int noOfChildren, int noOfInfant);
     FlightSearchResponse getAllReturningFlights(Airport departurePort, Airport arrivalPort);
     FlightSearchResponse getAllDepartingFlights(Airport departurePort, Airport arrivalPort) ;
     FlightSearchResponse getDepartingFlights(Airport departurePort, Airport arrivalPort, LocalDate departureDate, int noOfAdult, int noOfChildren, int noOfInfant);

    Page<Flight> getAllFlights(int pageNo, int pageSize);
    int getTotalNumberOfFlights();

}
