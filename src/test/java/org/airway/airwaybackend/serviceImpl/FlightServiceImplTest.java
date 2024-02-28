package org.airway.airwaybackend.serviceImpl;

import org.airway.airwaybackend.dto.FlightSearchDto;
import org.airway.airwaybackend.exception.FlightNotFoundException;
import org.airway.airwaybackend.model.Airline;
import org.airway.airwaybackend.model.Airport;
import org.airway.airwaybackend.model.Flight;
import org.airway.airwaybackend.repository.FlightRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class FlightServiceImplTest {
    @Mock
    private FlightRepository flightRepository;

    @InjectMocks
    private FlightServiceImpl flightServiceImp;
    @Mock
    List<FlightSearchDto> flightDTOList;

    AutoCloseable autoCloseable;

    @BeforeEach
    void setUp(){
        autoCloseable = MockitoAnnotations.openMocks(this);
    }
    @Test
    void searchAvailableFlight() {
        Airport departurePort = new Airport();
        Airport arrivalPort = new Airport();
        LocalDate departureDate = LocalDate.of(2024, 2, 25);
        LocalDate returnDate2= LocalDate.of(2024, 3, 25);
        LocalDate returnDate1 = null;
        Duration duration = Duration.ofMinutes(23);
        LocalTime timeDeparture = LocalTime.of(6,0,0);
        LocalTime timeArrival = timeDeparture.plusMinutes(duration.toMinutes());
        int noOfAdult = 2;
        int noOfChildren = 1;
        int noOfInfant = 0;
        Flight flight1 = new Flight();
        flight1.setId(1L);
        Flight flight2 = new Flight();
        Airline airline = new Airline();
        airline.setName("Dana");
        flight2.setAirline(airline);
        flight1.setAirline(airline);
        flight2.setId(2L);

        when (flightRepository.findByDeparturePortAndArrivalPortAndDepartureDateAndNoOfAdultGreaterThanEqualAndNoOfChildrenGreaterThanEqualAndNoOfInfantGreaterThanEqual(departurePort,arrivalPort,departureDate,noOfAdult,noOfChildren,noOfInfant)).thenReturn(Collections.singletonList(flight1));
        when(flightRepository.findByDeparturePortAndArrivalPortAndDepartureDateAndReturnDateAndNoOfAdultGreaterThanEqualAndNoOfChildrenGreaterThanEqualAndNoOfInfantGreaterThanEqual(departurePort,arrivalPort,departureDate,returnDate2,noOfAdult,noOfChildren,noOfInfant)).thenReturn(Collections.singletonList(flight2));
        flightDTOList = flightServiceImp.searchAvailableFlight(departurePort,arrivalPort,departureDate, null,noOfAdult,noOfChildren, noOfInfant);
        List<FlightSearchDto> availableFlightsRoundTrip= flightServiceImp.searchAvailableFlight(departurePort,arrivalPort,departureDate, returnDate2,noOfAdult,noOfChildren, noOfInfant);


        assertNotNull(flightDTOList);
        assertFalse(flightDTOList.isEmpty());
        assertNotNull(availableFlightsRoundTrip);
        assertFalse(availableFlightsRoundTrip.isEmpty());
        assertEquals(1, availableFlightsRoundTrip.size());
        assertEquals(1, flightDTOList.size());

        assertEquals(flight1.getId(), flightDTOList.get(0).getId());
        assertEquals(flight2.getId(), availableFlightsRoundTrip.get(0).getId());

    }

    @Test
    void TestSearchAvailableFlight_NoFlightsFound(){
        Airport departurePort = new Airport();
        Airport arrivalPort = new Airport();
        LocalDate departureDate = LocalDate.of(2024, 2, 25);
        LocalDate arrivalDate2= departureDate.plusDays(1);
        LocalDate returnDate2 = LocalDate.of(2024, 3, 25);
        LocalDate returnDate1 = null;
        LocalDate arrivalDate1 = null;
        Duration duration = Duration.ofMinutes(23);
        LocalTime timeDeparture = LocalTime.of(6,0,0);
        LocalTime timeArrival = timeDeparture.plusMinutes(duration.toMinutes());
        int noOfAdult = 2;
        int noOfChildren = 1;
        int noOfInfant = 0;

        when(flightRepository.findByDeparturePortAndArrivalPortAndDepartureDateAndNoOfAdultGreaterThanEqualAndNoOfChildrenGreaterThanEqualAndNoOfInfantGreaterThanEqual(departurePort,arrivalPort,departureDate,noOfAdult,noOfChildren,noOfInfant)).thenReturn(Collections.emptyList());
        when(flightRepository.findByDeparturePortAndArrivalPortAndDepartureDateAndReturnDateAndNoOfAdultGreaterThanEqualAndNoOfChildrenGreaterThanEqualAndNoOfInfantGreaterThanEqual(departurePort,arrivalPort,departureDate,returnDate2,noOfAdult,noOfChildren,noOfInfant)).thenReturn(Collections.emptyList());

        assertThrows(FlightNotFoundException.class, ()->
                flightServiceImp.searchAvailableFlight(departurePort,arrivalPort,departureDate,returnDate2,noOfAdult,noOfChildren,noOfInfant));
    }

}
