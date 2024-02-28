package org.airway.airwaybackend.repository;

import org.airway.airwaybackend.model.Airport;
import org.airway.airwaybackend.model.Flight;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
@ExtendWith(MockitoExtension.class)
class FlightRepositoryTest {
    @Mock
    private FlightRepository flightRepository;

    @Test
    void findByDeparturePortAndArrivalPortAndDepartureDateAndNoOfAdultGreaterThanEqualAndNoOfChildrenGreaterThanEqualAndNoOfInfantGreaterThanEqual() {
        Airport departurePort = new Airport();
        Airport arrivalPort = new Airport();
        LocalDate departureDate = LocalDate.now();
        int noOfAdult = 2;
        int noOfChildren = 1;
        int noOfInfant = 0;

        when(flightRepository.findByDeparturePortAndArrivalPortAndDepartureDateAndNoOfAdultGreaterThanEqualAndNoOfChildrenGreaterThanEqualAndNoOfInfantGreaterThanEqual(
                departurePort, arrivalPort, departureDate, noOfAdult, noOfChildren, noOfInfant))
                .thenReturn(List.of(new Flight(), new Flight()));

        List<Flight> flights = flightRepository.findByDeparturePortAndArrivalPortAndDepartureDateAndNoOfAdultGreaterThanEqualAndNoOfChildrenGreaterThanEqualAndNoOfInfantGreaterThanEqual(
                departurePort, arrivalPort, departureDate, noOfAdult, noOfChildren, noOfInfant);

        assert !flights.isEmpty();
        assert flights.size() == 2;
    }



    @Test
    void findByDeparturePortAndArrivalPortAndDepartureDateAndReturnDateAndNoOfAdultGreaterThanEqualAndNoOfChildrenGreaterThanEqualAndNoOfInfantGreaterThanEqual() {
        Airport departurePort = new Airport();
        Airport arrivalPort = new Airport();
        LocalDate departureDate = LocalDate.now();
        LocalDate returnDate = LocalDate.of(2024,9, 20 );
        int noOfAdult = 2;
        int noOfChildren = 1;
        int noOfInfant = 0;

        when(flightRepository.findByDeparturePortAndArrivalPortAndDepartureDateAndReturnDateAndNoOfAdultGreaterThanEqualAndNoOfChildrenGreaterThanEqualAndNoOfInfantGreaterThanEqual(
                departurePort, arrivalPort, departureDate, returnDate, noOfAdult, noOfChildren, noOfInfant))
                .thenReturn(List.of(new Flight(), new Flight()));

        List<Flight> flights = flightRepository.findByDeparturePortAndArrivalPortAndDepartureDateAndReturnDateAndNoOfAdultGreaterThanEqualAndNoOfChildrenGreaterThanEqualAndNoOfInfantGreaterThanEqual(
                departurePort, arrivalPort, departureDate, returnDate, noOfAdult, noOfChildren, noOfInfant);

        assert !flights.isEmpty();
        assert flights.size() == 2;
    }
}