package org.airway.airwaybackend.repository;

import org.airway.airwaybackend.enums.BookingStatus;
import org.airway.airwaybackend.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookingStatusNotAndCreatedAtBefore(BookingStatus bookingStatus, LocalDateTime created );

}
