package org.airway.airwaybackend.service;

import org.airway.airwaybackend.dto.BookingRequestDto;
import org.airway.airwaybackend.model.BookingFlight;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public interface BookingService {
    String bookFlight(BookingRequestDto bookingRequestDto);
    BigDecimal calculateFare(BigDecimal fare, int passenger);
    BigDecimal getALLtotalFare (List<BookingFlight> flights);
    String generateBookingReferenceNumber (Set<String> usedNumber);
    String generateMemberShip (String prefix);
    String calculateBaggageAllowance(String weightString, double factor);
    String calculateAllBaggageAllowances(List<BookingFlight> flights, Function<BookingFlight, String> propertyExtractor);
    BigDecimal calculateTotal(List<BookingFlight> flights, Function<BookingFlight, BigDecimal> propertyExtractor);
}
