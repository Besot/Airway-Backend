package org.airway.airwaybackend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.airway.airwaybackend.enums.FlightDirection;
import org.airway.airwaybackend.enums.FlightStatus;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Flight {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private FlightDirection flightDirection;
    private FlightStatus flightStatus;
    @Column(unique = true)
    private String flightNo;
    @ManyToOne
    private Airline airline;
    private LocalDate arrivalDate;
    private LocalDate departureDate;
    private LocalTime arrivalTime;
    private LocalTime departureTime;
    private long duration;
    private LocalDate returnDate;
    private LocalTime returnTime;
    @ManyToOne
    private Airport arrivalPort;
    @ManyToOne
    private Airport departurePort;
    @OneToMany(mappedBy = "flight")
    private List<Classes> classes;
    private int totalSeat;
    private int availableSeat;
    private int noOfChildren;
    private int noOfAdult;
    private int noOfInfant;
    @ManyToOne
    private User user;
    @JsonIgnore
    @ManyToMany
    @JoinTable(
            joinColumns = @JoinColumn(name = "flight_id"),
            inverseJoinColumns = @JoinColumn(name = "passenger_id")
    )
    private List<Passenger> passengers;
}