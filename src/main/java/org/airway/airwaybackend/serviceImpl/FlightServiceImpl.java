package org.airway.airwaybackend.serviceImpl;


import org.airway.airwaybackend.dto.AddFlightDto;
import org.airway.airwaybackend.dto.FlightSearchDto;
import org.airway.airwaybackend.enums.FlightDirection;
import org.airway.airwaybackend.enums.Role;
import org.airway.airwaybackend.exception.*;
import org.airway.airwaybackend.model.*;
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
    public List<FlightSearchDto> searchAvailableFlight(Airport departurePort, Airport arrivalPort, LocalDate departureDate, LocalDate returnDate, int noOfAdult, int noOfChildren, int noOfInfant) {
        List<Flight> availableFlight;
        if(returnDate== null){
            availableFlight = flightRepository.findByDeparturePortAndArrivalPortAndDepartureDateAndNoOfAdultGreaterThanEqualAndNoOfChildrenGreaterThanEqualAndNoOfInfantGreaterThanEqual(  departurePort,  arrivalPort,  departureDate, noOfAdult, noOfChildren, noOfInfant);
        }else{
            availableFlight = flightRepository.findByDeparturePortAndArrivalPortAndDepartureDateAndReturnDateAndNoOfAdultGreaterThanEqualAndNoOfChildrenGreaterThanEqualAndNoOfInfantGreaterThanEqual( departurePort,   arrivalPort,  departureDate,  returnDate,  noOfAdult,  noOfChildren,  noOfInfant);
        }
        if(availableFlight.isEmpty()){
            throw new FlightNotFoundException("No flight found for specified criteria. Please adjust search parameters");
        }
            List<FlightSearchDto> availableFlightDTOs = new ArrayList<>();
            for (Flight flight : availableFlight) {
                FlightSearchDto flightDTO = new FlightSearchDto();
                flightDTO.setId(flight.getId());
                flightDTO.setFlightDirection(flight.getFlightDirection());
                flightDTO.setFlightNo(flight.getFlightNo());
                flightDTO.setAirline(flight.getAirline().getName());
                flightDTO.setArrivalDate(flight.getArrivalDate());
                flightDTO.setDepartureDate(flight.getDepartureDate());
                flightDTO.setArrivalTime(flight.getArrivalTime());
                flightDTO.setReturnDate(flight.getReturnDate());
                flightDTO.setReturnTime(flight.getReturnTime());
                flightDTO.setDepartureTime(flight.getDepartureTime());
                flightDTO.setDuration((int) flight.getDuration());
                flightDTO.setArrivalPort(flight.getArrivalPort());
                flightDTO.setDeparturePort(flight.getDeparturePort());
                flightDTO.setClasses(flight.getClasses());
                flightDTO.setTotalSeat(flight.getTotalSeat());
                flightDTO.setAvailableSeat(flight.getAvailableSeat());
                flightDTO.setNoOfChildren(flight.getNoOfChildren());
                flightDTO.setNoOfAdult(flight.getNoOfAdult());
                flightDTO.setNoOfInfant(flight.getNoOfInfant());


                availableFlightDTOs.add(flightDTO);
            }

            return availableFlightDTOs;
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

