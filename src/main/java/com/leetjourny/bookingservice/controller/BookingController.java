package com.leetjourny.bookingservice.controller;

import com.leetjourny.bookingservice.request.BookingRequest;
import com.leetjourny.bookingservice.response.BookingResponse;
import com.leetjourny.bookingservice.service.BookingService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping(consumes = "application/json",produces = "application/json",path = "/booking")
    public BookingResponse getBooking(@RequestBody BookingRequest request) {
        return bookingService.createBooking(request);
    }
}
