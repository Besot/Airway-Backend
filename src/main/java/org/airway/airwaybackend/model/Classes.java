package org.airway.airwaybackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.airway.airwaybackend.enums.FlightStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Classes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String className;
    private String baggageAllowance;
    private BigDecimal taxFee;
    private BigDecimal surchargeFee;
    private BigDecimal serviceCharge;
    private BigDecimal baseFare;
    private BigDecimal totalFare;
    @OneToOne
    private Seat seat;
    @ManyToOne
    private Flight flight;
    @OneToMany
    private List<Passenger> passengers;

    public void setBasePrice(BigDecimal basePrice) {
        this.baseFare = basePrice;
    }

    public void setFlightStatus(String flightStatus) {
        if (flight != null && flightStatus != null) {
            FlightStatus status = FlightStatus.valueOf(flightStatus);
            flight.setFlightStatus(status);
        }
    }

    public void setNumOfSeats(int numOfSeats) {
        if (numOfSeats == 1) {
            if (seat == null) {
                seat = new Seat();
            }
        } else {

        }
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Objects.hash(id);
        return result;
    }
}

