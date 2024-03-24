package org.airway.airwaybackend.serviceImpl;


import org.airway.airwaybackend.dto.BookingEditingDto;
import org.airway.airwaybackend.dto.BookingFlightDto;
import org.airway.airwaybackend.dto.BookingRequestDto;
import org.airway.airwaybackend.dto.PassengerDTo;
import org.airway.airwaybackend.enums.BookingStatus;
import org.airway.airwaybackend.enums.Role;
import org.airway.airwaybackend.exception.BookingNotFoundException;
import org.airway.airwaybackend.exception.ClassNotFoundException;
import org.airway.airwaybackend.exception.SeatListNotFoundException;
import org.airway.airwaybackend.exception.UnauthorizedAccessException;
import org.airway.airwaybackend.model.*;
import org.airway.airwaybackend.repository.*;
import org.airway.airwaybackend.model.Booking;
import org.airway.airwaybackend.repository.BookingRepository;
import org.airway.airwaybackend.service.BookingService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;

import static java.lang.Boolean.FALSE;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final PassengerRepository passengerRepository;
    private final UserRepository userRepository;
    private final FlightRepository flightRepository;
    private final ClassesRepository classesRepository;
    private final BookingFlightRepository bookingFlightRepository;
    private final PNRServiceImpl pnrServiceImpl;

    public BookingServiceImpl(BookingRepository bookingRepository, PassengerRepository passengerRepository, UserRepository userRepository, FlightRepository flightRepository, ClassesRepository classesRepository, BookingFlightRepository bookingFlightRepository, PNRServiceImpl pnrServiceImpl) {
        this.bookingRepository = bookingRepository;
        this.passengerRepository = passengerRepository;
        this.userRepository = userRepository;
        this.flightRepository = flightRepository;
        this.classesRepository = classesRepository;
        this.bookingFlightRepository = bookingFlightRepository;
        this.pnrServiceImpl = pnrServiceImpl;
    }

    @Override
    public Page<Booking> getAllBookings(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return bookingRepository.findAll(pageable);
    }

    @Override
    public int getTotalNumberOfBookings() {
        return (int) bookingRepository.count();
    }


    @Override
    public String bookFlight(BookingRequestDto bookingRequestDto) {
        try {
            Booking booking = new Booking();
            List<PNR> pnrList = new ArrayList<>();
            PNR pnr;
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
                if (user == null) {
                    passenger.setPassengerCode(generateMemberShip("GU"));
                    if (passenger.getContact().equals(true)) {
                        booking.setPassengerCode(passenger.getPassengerCode());
                        booking.setPassengerContactEmail(passenger.getPassengerEmail());
                    }
                } else if (user != null && user.getUserRole().equals(Role.PASSENGER)) {
                    booking.setUserId(user);
                    passenger.setPassengerCode(generateMemberShip("ME"));
                    if (passenger.getContact().equals(true)) {
                        booking.setPassengerCode(user.getMembershipNo());
                        booking.setPassengerContactEmail(passenger.getPassengerEmail());
                    }
                } else if (user != null && user.getUserRole().equals(Role.ADMIN)) {
                    booking.setUserId(user);
                    passenger.setPassengerCode(generateMemberShip("GU"));
                    if (passenger.getContact().equals(true)) {
                        booking.setPassengerCode(passenger.getPassengerCode());
                        booking.setPassengerContactEmail(passenger.getPassengerEmail());

                    }
                }
                passengers.add(passenger);
            }
            List<Passenger> savedPassengers = passengerRepository.saveAll(passengers);
            List<BookingFlightDto> bookingFlightDtos = bookingRequestDto.getBookingFlights();
            List<BookingFlight> bookingFlights = new ArrayList<>();
            for (BookingFlightDto bookingFlightDto : bookingFlightDtos) {
                BookingFlight bookingFlight = new BookingFlight();
                Classes classes = classesRepository.findById(bookingFlightDto.getClassId()).orElseThrow(() -> new ClassNotFoundException("classes not found"));
                if (classes.getSeat().getAvailableSeat() == 0) {
                    throw new SeatListNotFoundException("no Seat is available");
                }
                Flight flight = classes.getFlight();
                bookingFlight.setFlight(flight);
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
            for (BookingFlight bookingFlight : savedBookingFlight) {
                List<PNR> classPnrs = bookingFlight.getClasses().getPnrList();
                pnr = pnrServiceImpl.generatePNRForEachPassengerAndFlight(bookingFlight, savedPassengers);
               pnr.setPassengerList(savedPassengers);
               pnr.setBookingFlight(bookingFlight);
                pnrList.add(pnr);
                bookingFlight.setPnr(pnr);
                classPnrs.add(pnr);
                classesRepository.save(bookingFlight.getClasses());
                bookingFlightRepository.save(bookingFlight);
            }

            booking.setBookingFlights(savedBookingFlight);
            booking.setPassengers(savedPassengers);
            booking.setBookingFlights(savedBookingFlight);
            Set<String> usedNumbers = new HashSet<>();
            booking.setBookingReferenceCode(generateBookingReferenceNumber(usedNumbers));
            booking.setPnrList(pnrList);
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
        } catch (ClassNotFoundException ex) {
            return "Class not available";
        } catch (SeatListNotFoundException ex) {
            return "Seat not Available";
        } catch (Exception e) {
            e.printStackTrace();
            return "error occurred in the process";
        }
    }

    @Override
    public BigDecimal calculateFare(BigDecimal fare, int passenger) {
        return fare.multiply(BigDecimal.valueOf(passenger));
    }

    @Override
    public BigDecimal getALLtotalFare(List<BookingFlight> flights) {
        BigDecimal totalFare = BigDecimal.ZERO;
        for (BookingFlight bookingFlight : flights) {
            if (bookingFlight != null) {
                totalFare = totalFare.add(bookingFlight.getTotalFare());
            }
        }
        return totalFare;
    }

    @Override
    public String generateBookingReferenceNumber(Set<String> usedNumber) {
        String prefix = "XY";
        int digit = 6;
        Random random = new Random();

        String randomNumber;
        do {
            randomNumber = String.format("%06d", random.nextInt(1000000));

        } while (usedNumber.contains(prefix + randomNumber));
        String bookingReferenceNumber = prefix + randomNumber;
        usedNumber.add(bookingReferenceNumber);
        return bookingReferenceNumber;
    }

    public String generateMemberShip(String prefix) {
        Random random = new Random();
        int suffixLength = 4;
        StringBuilder suffixBuilder = new StringBuilder();
        for (int i = 0; i < suffixLength; i++) {
            suffixBuilder.append(random.nextInt(10));
        }
        return prefix + suffixBuilder.toString();
    }

    @Override
    public String calculateBaggageAllowance(String weightString, double factor) {
        String weightValue = weightString.replaceAll("[^\\d.]+", "");
        double weightInNumeric = Double.parseDouble(weightValue);
        double calculatedWeight = weightInNumeric * factor;
        return String.format("%.2f kg", calculatedWeight);
    }

    @Override
    public String calculateAllBaggageAllowances(List<BookingFlight> flights, Function<BookingFlight, String> propertyExtractor) {
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


    public String editBookingById(Long id, BookingEditingDto bookingEditingDto) throws IllegalArgumentException, ClassNotFoundException {
        if (bookingEditingDto == null || bookingEditingDto.getPassengers() == null) {
            throw new IllegalArgumentException("booking sata is missing");
        }
        Booking booking = getBookingById(id);
        updateBookingDetail(booking, bookingEditingDto);
        List<Passenger> savedPassengersList = updatePassengerDetails(booking, bookingEditingDto.getPassengers());
        List<BookingFlight> savedBookingFlights = updateBookingFlights(booking, bookingEditingDto.getBookingFlights());
        booking.setPassengers(savedPassengersList);
        booking.setBookingFlights(savedBookingFlights);
        return "Booking updated Successfully";
    }
    public Booking getBookingById(Long id){
        return bookingRepository.findById(id)
                .orElseThrow(()-> new BookingNotFoundException("Booking not found with id:" + id));
    }

    public void updateBookingDetail (Booking booking, BookingEditingDto bookingEditingDto){
        if(bookingEditingDto.getTripType()!= null) {
            booking.setTripType(bookingEditingDto.getTripType());
        }
        if(bookingEditingDto.getBookingStatus()!=null) {
            booking.setBookingStatus(bookingEditingDto.getBookingStatus());
        }
        bookingRepository.save(booking);
    }
    private List<Passenger> updatePassengerDetails(Booking booking, List<PassengerDTo> passengerDtos) {
        List<Passenger> passengersList = booking.getPassengers();
        List<Passenger> savedPassengersList = new ArrayList<>();
        for (int i = 0; i < Math.min(passengerDtos.size(), passengersList.size()); i++) {
            PassengerDTo passengerDto = passengerDtos.get(i);
            Passenger passenger = passengersList.get(i);
            passenger.setFirstName(passengerDto.getFirstName());
            passenger.setPassengerEmail(passengerDto.getPassengerEmail());
            passenger.setContactEmail(passengerDto.getContactEmail());
            passenger.setContact(passengerDto.getContact());
            passenger.setDateOfBirth(passengerDto.getDateOfBirth());
            passenger.setPhoneNumber(passengerDto.getPhoneNumber());
            passenger.setCategory(passengerDto.getCategory());
            passenger.setGender(passengerDto.getGender());
            passenger.setNationality(passengerDto.getNationality());
            passenger.setTitle(passengerDto.getTitle());
            passenger.setContactPhone(passengerDto.getContactPhone());
            passenger.setLastName(passengerDto.getLastName());
            passenger.setPSN(String.format("PSN%03d", i + 1));
            passenger.setPassengerCode(passengerDto.getPassengerCode());
            if(passenger.getContact()){
                booking.setPassengerContactEmail(passenger.getPassengerEmail());
            }
            passenger.setBookings(booking);
            passenger = passengerRepository.save(passenger);
            savedPassengersList.add(passenger);
        }
        return savedPassengersList;
    }
    private List<BookingFlight> updateBookingFlights(Booking booking, List<BookingFlightDto> bookingFlightDtos) throws ClassNotFoundException {
        List<BookingFlight> bookingFlights = booking.getBookingFlights();
        List<BookingFlight> savedBookingFlights = new ArrayList<>();

        for (int i = 0; i < Math.min(bookingFlightDtos.size(), bookingFlights.size()); i++) {
            BookingFlightDto bookingFlightDto = bookingFlightDtos.get(i);
            BookingFlight bookingFlight = bookingFlights.get(i);
            Classes classes = classesRepository.findById(bookingFlightDto.getClassId())
                    .orElseThrow(() -> new ClassNotFoundException("Class not Available"));
            if (classes.getSeat().getAvailableSeat() == 0) {
                throw new SeatListNotFoundException("no Seat is available");
            }
            bookingFlight.setClasses(classes);
            Flight flight = classes.getFlight();
            bookingFlight.setFlight(flight);
            List<Passenger> passengers = booking.getPassengers();

            bookingFlight.setBaseFare(calculateFare(classes.getBaseFare(), passengers.size()));
            bookingFlight.setBaggageAllowance(String.valueOf(calculateBaggageAllowance(classes.getBaggageAllowance(), passengers.size())));
            bookingFlight.setServiceCharge(calculateFare(classes.getServiceCharge(), passengers.size()));
            bookingFlight.setSurchargeFee(calculateFare(classes.getSurchargeFee(), passengers.size()));
            bookingFlight.setTaxFee(calculateFare(classes.getTaxFee(), passengers.size()));
            bookingFlight.setTotalFare(calculateFare(classes.getTotalFare(), passengers.size()));
            bookingFlight.setClasses(classes);
            bookingFlight.setBooking(booking);
            bookingFlightRepository.save(bookingFlight);
            savedBookingFlights.add(bookingFlight);

        }
        return savedBookingFlights;

    }
}