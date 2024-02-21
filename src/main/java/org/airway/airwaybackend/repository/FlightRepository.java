package org.airway.airwaybackend.repository;

import org.airway.airwaybackend.model.Flight;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FlightRepository extends JpaRepository<Flight, Long> {
}
