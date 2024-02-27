package org.airway.airwaybackend.controller;

import org.airway.airwaybackend.serviceImpl.FlightServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/flights")
public class FlightController {
    private final FlightServiceImpl flightService;

    @Autowired
    public FlightController(FlightServiceImpl flightService) {
        this.flightService = flightService;
    }

    @DeleteMapping("/delete-flight/{Id}")
    public ResponseEntity<String> deleteFlight(@PathVariable Long Id) {
        String response = flightService.deleteFlight(Id);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }
}
