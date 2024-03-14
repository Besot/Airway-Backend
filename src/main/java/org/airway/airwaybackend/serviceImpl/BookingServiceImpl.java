package org.airway.airwaybackend.serviceImpl;


import org.airway.airwaybackend.model.Booking;
import org.airway.airwaybackend.repository.BookingRepository;
import org.airway.airwaybackend.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @Override
    public Page<Booking> getAllBookings(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return bookingRepository.findAll(pageable);
    }

    @Override
    public int getTotalNumberOfBookings() {return (int) bookingRepository.count();}



}
