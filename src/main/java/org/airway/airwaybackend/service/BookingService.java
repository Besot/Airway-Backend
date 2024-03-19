package org.airway.airwaybackend.service;

import org.airway.airwaybackend.dto.BookingEditingDto;
import org.airway.airwaybackend.dto.BookingRequestDto;
import org.airway.airwaybackend.exception.ClassNotFoundException;
import org.airway.airwaybackend.exception.UnauthorizedAccessException;
import org.airway.airwaybackend.model.BookingFlight;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import org.airway.airwaybackend.model.Booking;
import org.springframework.data.domain.Page;

import org.airway.airwaybackend.dto.BookingRequestDto;
import org.airway.airwaybackend.model.BookingFlight;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public interface BookingService {
    Page<Booking> getAllBookings(int pageNo, int pageSize);
    int getTotalNumberOfBookings();
    String bookFlight(BookingRequestDto bookingRequestDto);
    BigDecimal calculateFare(BigDecimal fare, int passenger);
    BigDecimal getALLtotalFare (List<BookingFlight> flights);
    String generateBookingReferenceNumber (Set<String> usedNumber);
    String generateMemberShip (String prefix);
    String calculateBaggageAllowance(String weightString, double factor);
    String calculateAllBaggageAllowances(List<BookingFlight> flights, Function<BookingFlight, String> propertyExtractor);
    BigDecimal calculateTotal(List<BookingFlight> flights, Function<BookingFlight, BigDecimal> propertyExtractor);

    String editBookingById(Long id, BookingEditingDto bookingEditingDto) throws UnauthorizedAccessException, ClassNotFoundException;
}
