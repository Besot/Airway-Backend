package org.airway.airwaybackend.repository;

import org.airway.airwaybackend.model.Airport;
import org.airway.airwaybackend.model.Flight;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface FlightRepository extends JpaRepository<Flight, Long> {
    List<Flight> findByDeparturePortAndArrivalPortAndDepartureDateAndNoOfAdultGreaterThanEqualAndNoOfChildrenGreaterThanEqualAndNoOfInfantGreaterThanEqual(Airport departurePort, Airport arrivalPort, LocalDate departureDate, int noOfAdult, int noOfChildren, int noOfInfant);
    List<Flight> findByDeparturePortAndArrivalPortAndDepartureDateAndReturnDateAndNoOfAdultGreaterThanEqualAndNoOfChildrenGreaterThanEqualAndNoOfInfantGreaterThanEqual( Airport departurePort, Airport arrivalPort, LocalDate departureDate, LocalDate returnDate, int noOfAdult, int noOfChildren, int noOfInfant);
}
