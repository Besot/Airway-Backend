package org.airway.airwaybackend.boot;

import org.airway.airwaybackend.model.Airline;
import org.airway.airwaybackend.model.Airport;
import org.airway.airwaybackend.repository.AirlineRepository;
import org.airway.airwaybackend.repository.AirportRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

@Component
@Transactional
public class DataLoader {
    private static final Logger logger = LoggerFactory.getLogger(DataLoader.class);
    private static final String airportsFile = "data/airportDetails.csv";

    @Autowired
    private AirportRepository airportRepository;

    @Autowired
    private AirlineRepository airlineRepository;

    @Bean
    CommandLineRunner loadAirportsAndAirlines() {
        return args -> {
            loadAirports();
            loadAirlines();
        };
    }

    private void loadAirports() {
        try (InputStream is = new ClassPathResource(airportsFile).getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");
                Airport airport = new Airport(values[0], values[1], values[2], values[3], values[4], values[5]);
                airportRepository.save(airport);
            }
        } catch (IOException e) {
            logger.error("Error loading airports from CSV: {}", e.getMessage());
        }
    }

    private void loadAirlines() {
        List<String> airlineNames = Arrays.asList("Air Peace", "Arik Air", "Dana Air", "Med-View Airline",
                "Overland Airways", "Azman Air", "Max Air", "Ibom Air", "Aero Contractors", "Green Africa Airways",
                "Chanchangi Airlines", "First Nation Airways", "Kabo Air", "Allied Air", "Bristow Helicopters Nigeria",
                "Dornier Aviation Nigeria", "Discovery Air", "SkyJet Aviation Services", "TopBrass Aviation",
                "OAS Helicopters");

        airlineNames.forEach(name -> {
            Airline airline = new Airline();
            airline.setName(name);
            airlineRepository.save(airline);
        });
    }
}
