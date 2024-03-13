package org.airway.airwaybackend.controller;

import org.airway.airwaybackend.dto.BookingRequestDto;
import org.airway.airwaybackend.serviceImpl.BookingServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/bookings")
public class BookingController {
    private final BookingServiceImpl bookingServiceImp;

    public BookingController(BookingServiceImpl bookingServiceImp) {
        this.bookingServiceImp = bookingServiceImp;
    }

    @PostMapping("/booking-flight")
    public ResponseEntity<String> BookFlight(@RequestBody BookingRequestDto bookingRequestDto) {
        String response = bookingServiceImp.bookFlight(bookingRequestDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}