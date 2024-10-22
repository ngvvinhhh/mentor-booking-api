package com.swd392.mentorbooking.controller;

import com.swd392.mentorbooking.dto.Response;
import com.swd392.mentorbooking.dto.booking.BookingResponse;
import com.swd392.mentorbooking.dto.booking.CreateBookingRequest;
import com.swd392.mentorbooking.dto.group.GroupRequest;
import com.swd392.mentorbooking.dto.group.GroupResponse;
import com.swd392.mentorbooking.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("**")
@RequestMapping("/booking")
public class BookingController {

    @Autowired
    BookingService bookingService;

    @PostMapping("create")
    public Response<BookingResponse> createBooking(@Valid @RequestBody CreateBookingRequest bookingRequest){
        return bookingService.createBooking(bookingRequest);
    }
}
