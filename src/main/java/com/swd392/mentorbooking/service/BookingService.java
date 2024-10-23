package com.swd392.mentorbooking.service;

import com.swd392.mentorbooking.dto.Response;
import com.swd392.mentorbooking.dto.booking.BookingResponse;
import com.swd392.mentorbooking.dto.booking.CreateBookingRequest;
import com.swd392.mentorbooking.entity.*;
import com.swd392.mentorbooking.entity.Enum.BookingStatus;
import com.swd392.mentorbooking.exception.group.NotFoundException;
import com.swd392.mentorbooking.repository.*;
import com.swd392.mentorbooking.utils.AccountUtils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;


@Service
public class BookingService {


    @Autowired
    private AccountUtils accountUtils;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private NotificationRepository notificationRepository;


    @Transactional
    public Response<BookingResponse> createBooking(CreateBookingRequest bookingRequest) {

        // Get current account
        Account account = accountUtils.getCurrentAccount();
        if (account == null) return new Response<>(401, "Please login first", null);

        // Check if the schedule information is valid
        Schedule schedule = scheduleRepository.findById(bookingRequest.getScheduleId())
                .orElseThrow(() -> new NotFoundException("Schedule not found with id: " + bookingRequest.getScheduleId()));

        Group group = groupRepository.findById(bookingRequest.getGroupId())
                .orElseThrow(() -> new NotFoundException("Group not found with id: " + bookingRequest.getGroupId()));

        // If a booking already exists with the same schedule and account, return an error.
        Optional<Booking> existingBooking = bookingRepository.findByAccountAndScheduleAndIsDeletedFalse(account, schedule);
        if (existingBooking.isPresent()) {
            throw new NotFoundException("Booking already exists booking with same schedule and account!!");
        }

        // Create new Booking object
        Booking booking = Booking.builder()
                .location(bookingRequest.getLocation())
                .locationNote(bookingRequest.getLocationNote())
                .status(BookingStatus.PROCESSING)
                .account(account)
                .group(group)
                .schedule(schedule)
                .createdAt(LocalDateTime.now())
                .isDeleted(false)
                .build();



        // Save booking to database
        bookingRepository.save(booking);

        // Create new Notification object
        Notification notification = new Notification();
        notification.setMessage(booking.getStatus().getMessage());
        notification.setDate(booking.getSchedule().getDate());
        notification.setStatus(booking.getStatus());
        notification.setAccount(booking.getAccount());
        notification.setBooking(booking);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setIsDeleted(false);
        notificationRepository.save(notification);


        // Create response object
        BookingResponse bookingResponse = BookingResponse.builder()
                .bookingId(booking.getBookingId())
                .location(booking.getLocation())
                .locationNote(booking.getLocationNote())
                .scheduleId(schedule.getId())
                .message(notification.getMessage())
                .status(booking.getStatus())
                .mentorName(schedule.getAccount().getName()) // Mentor name
                .build();

        return new Response<>(200, "Booking created successfully!", bookingResponse);
    }

}
