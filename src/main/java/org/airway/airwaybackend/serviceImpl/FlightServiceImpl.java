package org.airway.airwaybackend.serviceImpl;


import org.airway.airwaybackend.dto.AddFlightDto;
import org.airway.airwaybackend.dto.ClassDto;
import org.airway.airwaybackend.dto.FlightSearchDto;
import org.airway.airwaybackend.enums.FlightDirection;
import org.airway.airwaybackend.enums.Role;
import org.airway.airwaybackend.exception.*;
import org.airway.airwaybackend.model.*;
import org.airway.airwaybackend.dto.FlightSearchResponse;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.*;

import static org.airway.airwaybackend.enums.FlightDirection.ONE_WAY;
import static org.airway.airwaybackend.enums.FlightDirection.ROUND_TRIP;

@Service
public class FlightServiceImpl implements FlightService {
    private final FlightRepository flightRepository;
    private final UserRepository userRepository;
    private final AirlineRepository airlineRepository;
    private final SeatRepository seatRepository;
    private final AirportRepository airportRepository;
    private final ClassesRepository classesRepository;
    private final SeatListRepository seatListRepository;
    @Autowired
    public FlightServiceImpl(FlightRepository flightRepository, UserRepository userRepository, AirlineRepository airlineRepository, SeatRepository seatRepository, AirportRepository airportRepository, ClassesRepository classesRepository, SeatListRepository seatListRepository) {
        this.flightRepository = flightRepository;

        this.userRepository = userRepository;
        this.airlineRepository = airlineRepository;
        this.seatRepository = seatRepository;
        this.airportRepository = airportRepository;
        this.classesRepository = classesRepository;
        this.seatListRepository = seatListRepository;
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


    @Override
    public String addNewFlight(AddFlightDto flightDto) throws AirportNotFoundException, AirlineNotFoundException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByEmail(username).orElseThrow(()-> new UserNotFoundException("User Not Found"));

        if (user == null) {
            throw new UserNotFoundException("Admin must be Logged In to Continue");
        }
        if (!user.getUserRole().equals(Role.ADMIN)) {
            throw new UserNotVerifiedException("You are not allowed to Add New Flight");
        }

        Flight newFlight = new Flight();
        newFlight.setFlightDirection(flightDto.getFlightDirection());
        newFlight.setFlightStatus((flightDto.getFlightStatus()));

        String newFlightNoLetter = generateRandomLetters(2);
        String newFlightNo = generateRandomNumber(3);
        String generatedFlightNo = newFlightNoLetter + newFlightNo;
        newFlight.setFlightNo(generatedFlightNo);
        newFlight.setUser(user);
        Airline airline = airlineRepository.findByNameIgnoreCase(flightDto.getAirlineName()).orElseThrow(() -> new AirlineNotFoundException("Airline with name not Found"));
        newFlight.setAirline(airline);
        newFlight.setDuration(flightDto.getDuration());
        newFlight.setDepartureDate(flightDto.getDepartureDate());
        LocalDate arrivalDate = calculateArrivalDate(flightDto.getDepartureDate(), flightDto.getDuration());
        newFlight.setArrivalDate(arrivalDate);
        newFlight.setDepartureTime(flightDto.getDepartureTime());
        Airport arrivalPort =airportRepository.findByIataCodeIgnoreCase(flightDto.getArrivalPortName()).orElseThrow(() -> new AirportNotFoundException("Airport with code not Found"));
        newFlight.setArrivalPort(arrivalPort);
        Airport departurePort = airportRepository.findByIataCodeIgnoreCase(flightDto.getDeparturePortName()).orElseThrow(() -> new AirportNotFoundException("Airport with code not Found"));
        newFlight.setDeparturePort(departurePort);
        newFlight.setTotalSeat(flightDto.getTotalSeat());
        newFlight.setNoOfAdult(flightDto.getNoOfAdult());
        newFlight.setNoOfChildren(flightDto.getNoOfChildren());
        newFlight.setNoOfInfant(flightDto.getNoOfInfant());
        LocalTime arrivalTime = flightDto.getDepartureTime().plusMinutes(flightDto.getDuration());
        newFlight.setArrivalTime(arrivalTime);

        if (flightDto.getFlightDirection() == FlightDirection.ROUND_TRIP) {
            newFlight.setReturnDate(flightDto.getReturnDate());
            newFlight.setReturnTime(flightDto.getReturnTime());

        }

        Flight saveFlight = flightRepository.save(newFlight);
        List<Classes> classesList = flightDto.getClasses();
        if (classesList != null) {
            for (Classes classes : classesList) {
                Classes saveClasses = new Classes();
                saveClasses.setClassName(classes.getClassName());
                saveClasses.setBasePrice(classes.getBasePrice());
                saveClasses.setBaggageAllowance(classes.getBaggageAllowance());

                saveClasses.setTaxFee(classes.getTaxFee());
                saveClasses.setSurchargeFee(classes.getSurchargeFee());
                saveClasses.setServiceCharge(classes.getServiceCharge());
                saveClasses.setTotalPrice(classes.getBasePrice().add( classes.getTaxFee()).add (classes.getSurchargeFee() ). add(classes.getServiceCharge()));
                saveClasses.setFlight(saveFlight);
                Classes savedClasses = classesRepository.save(saveClasses);
                classes.getSeat().setClassName(savedClasses);
                classes.getSeat().setFlightName(saveFlight);
                classes.getSeat().setAvailableSeat(classes.getSeat().getTotalNumberOfSeat());

                Seat seat = seatRepository.save(classes.getSeat());
                classes.getSeat().setSeatLists(generateSeatList(seat));

                saveClasses.setSeat(seat);
                seatRepository.save(seat);

                classesRepository.save(savedClasses);
            }
        }

        return "Flight Added Successfully";
    }
@Override
    public LocalDate calculateArrivalDate(LocalDate departureDate, long durationMinutes) {
        long days = durationMinutes / (24 * 60);
        long remainingMinutes = durationMinutes % (24 * 60);
        LocalDate arrivalDate = departureDate.plusDays(days);
        if (remainingMinutes > 0) {
            LocalTime departureTime = LocalTime.of(0, 0);
            LocalDateTime departureDateTime = LocalDateTime.of(departureDate, departureTime);
            LocalDateTime arrivalDateTime = departureDateTime.plusMinutes(remainingMinutes);
            arrivalDate = arrivalDateTime.toLocalDate();
        }
        return arrivalDate;
    }
@Override
    public String generateRandomNumber(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("Length must be greater than 0");
        }

        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int digit = random.nextInt(10);
            stringBuilder.append(digit);
        }

        return stringBuilder.toString();
    }
@Override
    public String generateRandomLetters(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            char randomChar = (char) ('A' + random.nextInt(26));
            sb.append(randomChar);
        }
        return sb.toString();
    }
    @Override
    public  List<SeatList> generateSeatList (Seat seat){
        List<SeatList> seatLists = new ArrayList<>();
        for(int i=1; i<= seat.getTotalNumberOfSeat(); i++){
            SeatList seatList = new SeatList();
            seatList.setSeat(seat);
            seatList.setSeatLabel(seat.getSeatAlphabet() + String.valueOf(i));
            seatList.setOccupied(false);
            seatLists.add(seatList);
        }
        return seatListRepository.saveAll(seatLists);

    }
}

