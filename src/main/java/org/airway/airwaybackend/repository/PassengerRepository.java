package org.airway.airwaybackend.repository;

import org.airway.airwaybackend.model.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PassengerRepository extends JpaRepository<Passenger, Long> {
    List<Passenger> findAllByPassengerEmail(String passengerMail);
}
