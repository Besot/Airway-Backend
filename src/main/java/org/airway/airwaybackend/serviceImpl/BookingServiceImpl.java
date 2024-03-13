package org.airway.airwaybackend.serviceImpl;


import org.airway.airwaybackend.dto.BookingFlightDto;
import org.airway.airwaybackend.dto.BookingRequestDto;
import org.airway.airwaybackend.dto.PassengerDTo;
import org.airway.airwaybackend.enums.BookingStatus;
import org.airway.airwaybackend.exception.FlightNotFoundException;
import org.airway.airwaybackend.model.*;
import org.airway.airwaybackend.repository.*;
import org.airway.airwaybackend.service.BookingService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;

import static java.lang.Boolean.FALSE;

@Service
public class BookingServiceImpl implements BookingService {
    private  final BookingRepository bookingRepository;
    private final PassengerRepository passengerRepository;
    private final UserRepository userRepository;
    private final FlightRepository flightRepository;
    private final ClassesRepository classesRepository;
    private final BookingFlightRepository bookingFlightRepository;
    public BookingServiceImpl(BookingRepository bookingRepository, PassengerRepository passengerRepository, UserRepository userRepository, FlightRepository flightRepository, ClassesRepository classesRepository, BookingFlightRepository bookingFlightRepository) {
        this.bookingRepository = bookingRepository;
        this.passengerRepository = passengerRepository;
        this.userRepository = userRepository;
        this.flightRepository = flightRepository;
        this.classesRepository = classesRepository;
        this.bookingFlightRepository = bookingFlightRepository;
    }

    @Override
    public String bookFlight(BookingRequestDto bookingRequestDto) {
        try {
            Booking booking = new Booking();

            booking.setBookingStatus(BookingStatus.PENDING);
            Booking savedBooking = bookingRepository.save(booking);

            List<PassengerDTo> passengerDTos = bookingRequestDto.getPassengers();
            List<Passenger> passengers = new ArrayList<>();
            for (int i = 0; i < passengerDTos.size(); i++) {
                PassengerDTo passengerDTo = passengerDTos.get(i);
                Passenger passenger = new Passenger();
                passenger.setFirstName(passengerDTo.getFirstName());
                passenger.setPassengerEmail(passengerDTo.getPassengerEmail());
                passenger.setCategory(passengerDTo.getCategory());
                passenger.setContact(passengerDTo.getContact());
                passenger.setGender(passengerDTo.getGender());
                passenger.setContactPhone(passengerDTo.getContactPhone());
                passenger.setPhoneNumber(passengerDTo.getPhoneNumber());
                passenger.setContactEmail(passengerDTo.getContactEmail());
                passenger.setDateOfBirth(passengerDTo.getDateOfBirth());
                passenger.setNationality(passengerDTo.getNationality());
                passenger.setTitle(passengerDTo.getTitle());
                passenger.setLastName(passengerDTo.getLastName());
                passenger.setBookings(savedBooking);
                passenger.setPSN(String.format("PSN%03d", i + 1));
                passenger.setContactEmail(passengerDTo.getContactEmail());
                passenger.setContactEmail(passengerDTo.getContactEmail());
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                String username = authentication.getName();
                User user = userRepository.findUserByEmail(username);
                if (user == null && passenger.getContact()) {
                    passenger.setPassengerCode(generateMemberShip("GU"));
                    booking.setPassengerCode(passenger.getPassengerCode());
                } else if (user !=null) {
                    booking.setUserId(user);

                }
                passengers.add(passenger);
            }
            List<Passenger> savedPassengers = passengerRepository.saveAll(passengers);
            List<BookingFlightDto> bookingFlightDtos = bookingRequestDto.getBookingFlights();
            List<BookingFlight> bookingFlights = new ArrayList<>();
            for (BookingFlightDto bookingFlightDto : bookingFlightDtos) {
                BookingFlight bookingFlight = new BookingFlight();
                Flight flight = flightRepository.findById(bookingFlightDto.getFlightId()).orElseThrow(() -> new FlightNotFoundException("flight not available"));
                bookingFlight.setFlight(flight);
                Classes classes = classesRepository.findById(bookingFlightDto.getClassId()).orElseThrow(() -> new ClassNotFoundException("classes not found"));
                bookingFlight.setBaseFare(calculateFare(classes.getBaseFare(), savedPassengers.size()));
                bookingFlight.setBaggageAllowance(String.valueOf(calculateBaggageAllowance(classes.getBaggageAllowance(), savedPassengers.size())));
                bookingFlight.setServiceCharge(calculateFare(classes.getServiceCharge(), savedPassengers.size()));
                bookingFlight.setSurchargeFee(calculateFare(classes.getSurchargeFee(), savedPassengers.size()));
                bookingFlight.setTaxFee(calculateFare(classes.getTaxFee(), savedPassengers.size()));
                bookingFlight.setTotalFare(calculateFare(classes.getTotalFare(), passengers.size()));
                bookingFlight.setClasses(classes);
                bookingFlight.setBooking(savedBooking);
                bookingFlights.add(bookingFlight);
            }
            List<BookingFlight> savedBookingFlight = bookingFlightRepository.saveAll(bookingFlights);
            booking.setBookingFlights(savedBookingFlight);
            booking.setPassengers(savedPassengers);
            booking.setBookingFlights(savedBookingFlight);
            Set<String> usedNumbers = new HashSet<>();
            booking.setBookingReferenceCode(generateBookingReferenceNumber(usedNumbers));
            booking.setPay(FALSE);
            booking.setTripType(bookingRequestDto.getTripType());
            booking.setTotalFare(getALLtotalFare(savedBookingFlight));
            booking.setSurchargeFee(calculateTotal(savedBookingFlight, BookingFlight::getSurchargeFee));
            booking.setTaxFee(calculateTotal(savedBookingFlight, BookingFlight::getTaxFee));
            booking.setBaseFare(calculateTotal(savedBookingFlight, BookingFlight::getBaseFare));
            booking.setServiceCharge(calculateTotal(savedBookingFlight, BookingFlight::getServiceCharge));
            booking.setBaggageAllowance(calculateAllBaggageAllowances(savedBookingFlight, BookingFlight::getBaggageAllowance));
            booking.setPassengers(savedPassengers);

            bookingRepository.save(booking);
            return "booking successful";
        }catch (FlightNotFoundException ex){

            return "flight not available";
        }catch (Exception e){
            e.printStackTrace();
            return "error occurred in the process";
        }
    }
    @Override
    public BigDecimal calculateFare(BigDecimal fare, int passenger){
        return fare.multiply(BigDecimal.valueOf(passenger));
    }
    @Override
    public BigDecimal getALLtotalFare (List<BookingFlight> flights){
        BigDecimal totalFare = BigDecimal.ZERO;
        for (BookingFlight bookingFlight: flights){
            if(bookingFlight!=null) {
                totalFare= totalFare.add(bookingFlight.getTotalFare());
            }
        }
        return totalFare;
    }

    @Override
    public String generateBookingReferenceNumber (Set<String> usedNumber){
        String prefix = "XY";
        int digit = 6;
        StringBuilder stringBuilder = new StringBuilder();
        Random random = new Random();

        String randomNumber;
        do{
            randomNumber = String.format("%06d", random.nextInt(1000000));

        }while (usedNumber.contains(prefix+randomNumber));
        String bookingReferenceNumber= prefix+ randomNumber;
        usedNumber.add(bookingReferenceNumber);
        return bookingReferenceNumber;
    }
    public String generateMemberShip (String prefix) {
        Random random = new Random();
        int suffixLength = 4;
        StringBuilder suffixBuilder = new StringBuilder();
        for (int i = 0; i < suffixLength; i++) {
            suffixBuilder.append(random.nextInt(10));
        }
        return prefix + suffixBuilder.toString();
    }

    @Override
    public  String calculateBaggageAllowance(String weightString, double factor) {
        String weightValue = weightString.replaceAll("[^\\d.]+", "");
        double weightInNumeric = Double.parseDouble(weightValue);
        double calculatedWeight = weightInNumeric * factor;
        return String.format("%.2f kg", calculatedWeight);
    }
    @Override
    public  String calculateAllBaggageAllowances(List<BookingFlight> flights, Function<BookingFlight, String> propertyExtractor) {
        double totalWeight = 0.0;
        for (BookingFlight flight : flights) {
            if (flight != null) {
                String baggageAllowance = propertyExtractor.apply(flight);
                String weightValue = baggageAllowance.replaceAll("[^\\d.]+", "");
                double weightInNumeric = Double.parseDouble(weightValue);
                totalWeight += weightInNumeric;
            }
        }
        return String.format("%.2f kg", totalWeight);
    }
    @Override
    public BigDecimal calculateTotal(List<BookingFlight> flights, Function<BookingFlight, BigDecimal> propertyExtractor) {
        BigDecimal total = BigDecimal.ZERO;
        for (BookingFlight flight : flights) {
            if (flight != null) {
                total = total.add(propertyExtractor.apply(flight));
            }
        }
        return total;
    }

}
