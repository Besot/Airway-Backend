package org.airway.airwaybackend.controller;

import org.airway.airwaybackend.dto.FlightSearchDto;
import org.airway.airwaybackend.model.Airport;
import org.airway.airwaybackend.model.Flight;
import org.airway.airwaybackend.serviceImpl.FlightServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class FlightControllerTest {
    @Mock
    private FlightServiceImpl flightServiceImpl;

    @InjectMocks
    private FlightController flightController;

    public FlightControllerTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAvailableFlight() {
        Airport departurePort = new Airport();
        Airport arrivalPort = new Airport();
        LocalDate departureDate = LocalDate.now();
        LocalDate arrivalDate = LocalDate.now().plusDays(1);
        LocalDate returnDate = LocalDate.of(2024,3,2);
        int noOfAdult = 2;
        int noOfChildren = 1;
        int noOfInfant = 0;
        Flight flight1 = new Flight();
        Flight flight2 = new Flight();
        FlightSearchDto flightDTO1 = new FlightSearchDto();
        FlightSearchDto flightDTO2 = new FlightSearchDto();

        when(flightServiceImpl.searchAvailableFlight(departurePort, arrivalPort, departureDate, returnDate, noOfAdult,noOfChildren, noOfInfant))
                .thenReturn(List.of(flightDTO1, flightDTO2));

        ResponseEntity<List<FlightSearchDto>> responseEntity = flightController.getAvailableFlight(departurePort, arrivalPort, departureDate, returnDate, noOfAdult, noOfChildren, noOfInfant);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(2, responseEntity.getBody().size());
    }
}