package com.swd392.mentorbooking.service;

import com.swd392.mentorbooking.dto.Response;
import com.swd392.mentorbooking.dto.booking.BookingResponse;
import com.swd392.mentorbooking.dto.booking.CreateBookingRequest;
import com.swd392.mentorbooking.dto.booking.UpcomingBookingResponseDTO;
import com.swd392.mentorbooking.entity.*;
import com.swd392.mentorbooking.entity.Enum.BookingStatus;
import com.swd392.mentorbooking.exception.ErrorCode;
import com.swd392.mentorbooking.exception.auth.AuthAppException;
import com.swd392.mentorbooking.exception.group.NotFoundException;
import com.swd392.mentorbooking.repository.*;
import com.swd392.mentorbooking.utils.AccountUtils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


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

    @Autowired
    private AccountRepository accountRepository;


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

        // Create new Notification for mentor object
        Notification notificationMentor = new Notification();
        notificationMentor.setMessage(BookingStatus.UNDECIDED.getMessage());
        notificationMentor.setDate(booking.getSchedule().getDate());
        notificationMentor.setStatus(BookingStatus.UNDECIDED);
        notificationMentor.setAccount(schedule.getAccount());
        notificationMentor.setBooking(booking);
        notificationMentor.setCreatedAt(LocalDateTime.now());
        notificationMentor.setIsDeleted(false);
        notificationRepository.save(notificationMentor);


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

    public Response<List<UpcomingBookingResponseDTO>> viewUpcomingBookings() {
        Account account = checkAccount();

        // Get group that contains this account
        Group group = groupRepository.findByStudentsContaining(account).orElse(null);
        if (group == null || group.getBookings() == null) {
            return new Response<>(404, "No group or bookings found for the account.", Collections.emptyList());
        }

        // Filter and map the bookings to DTOs
        List<UpcomingBookingResponseDTO> data = group.getBookings().stream()
                .filter(booking -> booking.getStatus() == BookingStatus.PROCESSING || booking.getStatus() == BookingStatus.SUCCESSFUL)
                .map(booking -> {
                    Schedule schedule = booking.getSchedule();
                    return UpcomingBookingResponseDTO.builder()
                            .bookingId(booking.getBookingId())
                            .bookingDate(schedule != null ? schedule.getDate().toString() : "N/A")
                            .startTime(schedule != null ? schedule.getStartTime().toString() : "N/A")
                            .endTime(schedule != null ? schedule.getEndTime().toString() : "N/A")
                            .location(booking.getLocation() != null ? booking.getLocation() : "N/A")
                            .locationNote(booking.getLocationNote() != null ? booking.getLocationNote() : "N/A")
                            .mentorId(booking.getAccount() != null ? booking.getAccount().getId() : null)
                            .mentorName(booking.getAccount() != null ? booking.getAccount().getName() : "N/A")
                            .bookingStatus(booking.getStatus())
                            .build();
                })
                .collect(Collectors.toList());

        return new Response<>(200, "Retrieve booking successfully!", data);
    }


    public Account checkAccount() {
        // Get the current account
        Account account = accountUtils.getCurrentAccount();
        if (account == null) {
            throw new AuthAppException(ErrorCode.NOT_LOGIN);
        }

        account = accountRepository.findByEmail(account.getEmail()).orElse(null);
        if (account == null) {
            throw new AuthAppException(ErrorCode.ACCOUNT_NOT_FOUND);
        }

        return account;
    }
}
