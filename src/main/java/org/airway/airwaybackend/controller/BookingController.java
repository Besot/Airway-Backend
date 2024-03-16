package org.airway.airwaybackend.controller;

import org.airway.airwaybackend.dto.BookingRequestDto;
import org.airway.airwaybackend.model.Booking;
import org.airway.airwaybackend.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/booking")
@CrossOrigin(origins = "http://localhost:5173" )
public class BookingController {

        private BookingService bookingService;
        @Autowired
        public BookingController(BookingService bookingService) {
            this.bookingService = bookingService;
        }
        @GetMapping("/bookings")
        public ResponseEntity<Page<Booking>> getAllBookings(
                @RequestParam(defaultValue = "0") int pageNo,
                @RequestParam(defaultValue = "10") int pageSize) {
            return new ResponseEntity<>(bookingService.getAllBookings(pageNo, pageSize), HttpStatus.OK);
        }



    @PostMapping("/booking-flight")
    public ResponseEntity<String> BookFlight(@RequestBody BookingRequestDto bookingRequestDto) {
        String response = bookingService.bookFlight(bookingRequestDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}