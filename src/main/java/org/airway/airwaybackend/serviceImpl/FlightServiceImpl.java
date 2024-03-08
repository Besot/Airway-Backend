package org.airway.airwaybackend.serviceImpl;


import org.airway.airwaybackend.dto.ClassDto;
import org.airway.airwaybackend.dto.FlightSearchDto;
import org.airway.airwaybackend.dto.FlightSearchResponse;
import org.airway.airwaybackend.enums.FlightDirection;
import org.airway.airwaybackend.enums.FlightStatus;
import org.airway.airwaybackend.exception.AirportNotFoundException;
import org.airway.airwaybackend.exception.FlightNotFoundException;
import org.airway.airwaybackend.model.Airport;
import org.airway.airwaybackend.model.Classes;
import org.airway.airwaybackend.model.Flight;
import org.airway.airwaybackend.repository.*;
import org.airway.airwaybackend.service.FlightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

import static org.airway.airwaybackend.enums.FlightDirection.ONE_WAY;
import static org.airway.airwaybackend.enums.FlightDirection.ROUND_TRIP;

@Service
public class FlightServiceImpl implements FlightService {
    private final FlightRepository flightRepository;
    @Autowired
    public FlightServiceImpl(FlightRepository flightRepository) {
        this.flightRepository = flightRepository;

    }

    @Override
    public String deleteFlight(Long Id) {
        Flight flight = flightRepository.findById(Id)
                .orElseThrow(() -> new FlightNotFoundException("Flight not found"));
        flightRepository.delete(flight);
        return "Flight deleted successfully";

    }
@Override
    public Map<String, FlightSearchResponse> searchAvailableFlight(Airport departurePort, Airport arrivalPort, LocalDate departureDate, FlightDirection flightDirection, LocalDate returnDate, int noOfAdult, int noOfChildren, int noOfInfant) {
        Map<String, FlightSearchResponse> flightsMap = new HashMap<>();
    if (departurePort == null || arrivalPort == null || departureDate == null) {
        throw new FlightNotFoundException("Departure port, arrival port, and departure date must not be null.");
    }

        List<Flight> departingFlight= null;
        List<Flight> returningFlight= null;
        if(Objects.equals(flightDirection, ONE_WAY)){
            departingFlight = flightRepository. searchByDeparturePortAndArrivalPortAndDepartureDateAndNoOfAdultGreaterThanEqualAndNoOfChildrenGreaterThanEqualAndNoOfInfantGreaterThanEqual(  departurePort,  arrivalPort,  departureDate, noOfAdult, noOfChildren, noOfInfant).orElseThrow(()-> new FlightNotFoundException("Flights not found, please adjust your search criteria"));
            List<Flight> confirmedDepartedFlights = new ArrayList<>();
            for(Flight flight : departingFlight) {
                if (flight.getFlightStatus() == FlightStatus.CONFIRMED || flight.getFlightStatus()==FlightStatus.MODIFIED) {
                    confirmedDepartedFlights.add(flight);
                }
            }

            flightsMap.put("Departing Flights", new FlightSearchResponse(convertFlightToDTO(confirmedDepartedFlights), confirmedDepartedFlights.size()));

        }else if (Objects.equals(flightDirection, ROUND_TRIP)){
            departingFlight = flightRepository.searchByDeparturePortAndArrivalPortAndDepartureDateAndNoOfAdultGreaterThanEqualAndNoOfChildrenGreaterThanEqualAndNoOfInfantGreaterThanEqual( departurePort,   arrivalPort,  departureDate,  noOfAdult,  noOfChildren,  noOfInfant).orElseThrow(()-> new FlightNotFoundException("Flight not found, please adjust your search criteria"));
            returningFlight = flightRepository.searchByDeparturePortAndArrivalPortAndDepartureDateAndNoOfAdultGreaterThanEqualAndNoOfChildrenGreaterThanEqualAndNoOfInfantGreaterThanEqual( arrivalPort,   departurePort,  returnDate, noOfAdult,  noOfChildren,  noOfInfant).orElseThrow(()-> new FlightNotFoundException("Flight not found, please adjust your search criteria"));
            List<Flight> confirmedDepartingFlights = new ArrayList<>();
            for(Flight flight : departingFlight) {
                if (flight.getFlightStatus() == FlightStatus.CONFIRMED || flight.getFlightStatus()==FlightStatus.MODIFIED) {
                    confirmedDepartingFlights.add(flight);
                }
            }
            flightsMap.put("Departing Flights", new FlightSearchResponse(convertFlightToDTO(confirmedDepartingFlights), convertFlightToDTO(confirmedDepartingFlights).size()));
            List<Flight> confirmedReturningFlights = new ArrayList<>();
            for(Flight flight : returningFlight) {
                if (flight.getFlightStatus() == FlightStatus.CONFIRMED || flight.getFlightStatus()==FlightStatus.MODIFIED) {
                    confirmedReturningFlights.add(flight);
                }
            }

            flightsMap.put("Returning Flights", new FlightSearchResponse(convertFlightToDTO(confirmedReturningFlights), convertFlightToDTO(confirmedReturningFlights).size()));

        }

    return flightsMap;
}


    public List<FlightSearchDto> convertFlightToDTO(List<Flight> flightList) {
        if (flightList.isEmpty()) {
            throw new FlightNotFoundException("No flight found for specified criteria. Please adjust search parameters");
        }
        List<FlightSearchDto> availableFlightDTOs = new ArrayList<>();
        for (Flight flight : flightList) {
            FlightSearchDto flightDTO = new FlightSearchDto();
            flightDTO.setId(flight.getId());
            flightDTO.setFlightStatus(flight.getFlightStatus());
            flightDTO.setFlightNo(flight.getFlightNo());
            flightDTO.setAirline(flight.getAirline().getName());
            flightDTO.setArrivalDate(flight.getArrivalDate());
            flightDTO.setDepartureDate(flight.getDepartureDate());
            flightDTO.setArrivalTime(flight.getArrivalTime());
            flightDTO.setDepartureTime(flight.getDepartureTime());
            flightDTO.setDuration(flight.getDuration());
            flightDTO.setArrivalPortName(flight.getArrivalPort().getName());
            flightDTO.setDeparturePortName(flight.getDeparturePort().getName());
            flightDTO.setFlightDirection(flight.getFlightDirection());
            List<ClassDto> classDtos = new ArrayList<>();

            for(Classes classes : flight.getClasses()){
                ClassDto classDto = new ClassDto();
                classDto.setId(classes.getId());
                classDto.setClassName(classes.getClassName());
                classDto.setTotalPrice(classes.getTotalPrice());
                classDto.setAvailableSeat(classes.getSeat().getAvailableSeat());
                classDtos.add(classDto);
            }
            flightDTO.setClasses(classDtos);
            availableFlightDTOs.add(flightDTO);
        }

        return  availableFlightDTOs;
    }

@Override
    public FlightSearchResponse getReturningFlights(Airport departurePort, Airport arrivalPort, LocalDate returnDate, int noOfAdult, int noOfChildren, int noOfInfant) {
    if (departurePort == null || arrivalPort == null || returnDate == null) {
        throw new FlightNotFoundException("Departure port, arrival port, and departure date must not be null.");
    }
        List<Flight> availableFlight= flightRepository.searchByDeparturePortAndArrivalPortAndDepartureDateAndNoOfAdultGreaterThanEqualAndNoOfChildrenGreaterThanEqualAndNoOfInfantGreaterThanEqual( arrivalPort, departurePort, returnDate, noOfAdult, noOfChildren, noOfInfant).orElseThrow(()-> new FlightNotFoundException("Flight not found, please adjust your search criteria"));
    List<Flight> confirmedReturningFlights = new ArrayList<>();
    for(Flight flight : availableFlight) {
        if (flight.getFlightStatus() == FlightStatus.CONFIRMED|| flight.getFlightStatus() == FlightStatus.MODIFIED) {
            confirmedReturningFlights.add(flight);
        }
    }

    return new FlightSearchResponse(convertFlightToDTO(confirmedReturningFlights),convertFlightToDTO(confirmedReturningFlights).size());

}
    @Override
    public FlightSearchResponse getAllReturningFlights(Airport departurePort, Airport arrivalPort) {
        if (departurePort == null || arrivalPort == null) {
            throw new FlightNotFoundException("Departure port, arrival port, and departure date must not be null.");
        }
        List<Flight> allReturningFlight= flightRepository.searchByDeparturePortAndArrivalPort(arrivalPort, departurePort).orElseThrow(()-> new FlightNotFoundException("Flight not found, please adjust your search criteria"));
        List<Flight> confirmedReturningFlights = new ArrayList<>();
        for(Flight flight : allReturningFlight) {
            if (flight.getFlightStatus() == FlightStatus.CONFIRMED|| flight.getFlightStatus() == FlightStatus.MODIFIED) {
                confirmedReturningFlights.add(flight);
            }
        }

        return new FlightSearchResponse(convertFlightToDTO(confirmedReturningFlights),convertFlightToDTO(confirmedReturningFlights).size());

    }
    @Override
    public FlightSearchResponse getAllDepartingFlights(Airport departurePort, Airport arrivalPort) {
        if (departurePort == null || arrivalPort == null) {
            throw new FlightNotFoundException("Departure port, arrival port, and departure date must not be null.");
        }
        List<Flight> allDepartingFlight= flightRepository.searchByDeparturePortAndArrivalPort(departurePort, arrivalPort).orElseThrow(()-> new FlightNotFoundException("Flight not found, please adjust your search criteria"));
        List<Flight> confirmedDepartedFlights = new ArrayList<>();
        for(Flight flight : allDepartingFlight) {
            if (flight.getFlightStatus() == FlightStatus.CONFIRMED|| flight.getFlightStatus() == FlightStatus.MODIFIED) {
                confirmedDepartedFlights.add(flight);
            }

        }
        return new FlightSearchResponse(convertFlightToDTO(confirmedDepartedFlights), convertFlightToDTO(confirmedDepartedFlights).size());
    }

    @Override
    public FlightSearchResponse getDepartingFlights(Airport departurePort, Airport arrivalPort, LocalDate departureDate, int noOfAdult, int noOfChildren, int noOfInfant) {
    if (departurePort == null || arrivalPort == null || departureDate == null) {
        throw new FlightNotFoundException("Departure port, arrival port, and departure date must not be null.");
    }
        List<Flight> availableFlight= flightRepository.searchByDeparturePortAndArrivalPortAndDepartureDateAndNoOfAdultGreaterThanEqualAndNoOfChildrenGreaterThanEqualAndNoOfInfantGreaterThanEqual(departurePort, arrivalPort, departureDate, noOfAdult, noOfChildren, noOfInfant).orElseThrow(()-> new FlightNotFoundException("Flight not found, please adjust your search criteria"));
    List<Flight> confirmedDepartedFlights = new ArrayList<>();
    for(Flight flight : availableFlight) {
        if (flight.getFlightStatus() == FlightStatus.CONFIRMED || flight.getFlightStatus() == FlightStatus.MODIFIED) {
            confirmedDepartedFlights.add(flight);
        }
    }
    return new FlightSearchResponse(convertFlightToDTO(confirmedDepartedFlights), convertFlightToDTO(confirmedDepartedFlights).size());
}


    @Override
    public Page<Flight> getAllFlights(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return flightRepository.findAll(pageable);
    }
    @Override
    public int getTotalNumberOfFlights() {
        return (int) flightRepository.count();
    }
}

