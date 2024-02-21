package org.airway.airwaybackend.repository;

import org.airway.airwaybackend.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {
}
