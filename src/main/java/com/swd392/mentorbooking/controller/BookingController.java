package com.swd392.mentorbooking.controller;

import com.swd392.mentorbooking.dto.Response;
import com.swd392.mentorbooking.dto.booking.BookingResponse;
import com.swd392.mentorbooking.dto.booking.CreateBookingRequest;
import com.swd392.mentorbooking.dto.booking.UpcomingBookingResponseDTO;
import com.swd392.mentorbooking.service.BookingService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("**")
@RequestMapping("/booking")
@SecurityRequirement(name = "api")
public class BookingController {

    @Autowired
    BookingService bookingService;

    // ** STUDENT SECTION ** //

    //Student book mentor
    @PostMapping("/student/create")
    public Response<BookingResponse> createBooking(@Valid @RequestBody CreateBookingRequest bookingRequest){
        return bookingService.createBooking(bookingRequest);
    }

    //Student view upcoming booking date
    @GetMapping("/student/view-upcoming")
    public Response<List<UpcomingBookingResponseDTO>> viewUpcomingBookings(){
        return bookingService.viewUpcomingBookings();
    }

    @PostMapping("/student/booking/cancel/{bookingId}")
    public Response<BookingResponse> approveBooking(@PathVariable Long bookingId) {
        return bookingService.cancelBooking(bookingId);
    }

    // ** MENTOR SECTION ** //
}
