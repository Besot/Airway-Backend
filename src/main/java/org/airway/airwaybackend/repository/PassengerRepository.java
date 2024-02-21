package org.airway.airwaybackend.repository;

import org.airway.airwaybackend.model.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PassengerRepository extends JpaRepository<Passenger, Long> {
}
