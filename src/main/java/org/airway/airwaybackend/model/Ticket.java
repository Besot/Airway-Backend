package org.airway.airwaybackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private Flight flight;
    @ManyToOne
    private Passenger passenger;
    @ManyToOne
    private Booking booking;
    private String ticketNo;
}
