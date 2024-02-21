package org.airway.airwaybackend.repository;

import org.airway.airwaybackend.model.Airline;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AirlineRepository extends JpaRepository<Airline, String> {
}
