package org.airway.airwaybackend.service;

import org.airway.airwaybackend.model.Booking;
import org.springframework.data.domain.Page;

public interface BookingService {
    Page<Booking> getAllBookings(int pageNo, int pageSize);
    int getTotalNumberOfBookings();

}
