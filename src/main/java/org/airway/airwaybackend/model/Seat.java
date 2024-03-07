package org.airway.airwaybackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    private Classes className;
    private char seatAlphabet;
    private int totalNumberOfSeat;
    @ManyToOne
    private Flight flightName;
    private int availableSeat;
    private int noOfOccupiedSeats;
    @OneToMany
    private List<SeatList> seatLists;
}
