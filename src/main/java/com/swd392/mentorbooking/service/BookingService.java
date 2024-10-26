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

    private final AccountUtils accountUtils;
    private final BookingRepository bookingRepository;
    private final ScheduleRepository scheduleRepository;
    private final GroupRepository groupRepository;
    private final NotificationRepository notificationRepository;
    private final AccountRepository accountRepository;

    @Autowired
    public BookingService(AccountUtils accountUtils,
                          BookingRepository bookingRepository,
                          ScheduleRepository scheduleRepository,
                          GroupRepository groupRepository,
                          NotificationRepository notificationRepository,
                          AccountRepository accountRepository) {
        this.accountUtils = accountUtils;
        this.bookingRepository = bookingRepository;
        this.scheduleRepository = scheduleRepository;
        this.groupRepository = groupRepository;
        this.notificationRepository = notificationRepository;
        this.accountRepository = accountRepository;
    }

    @Transactional
    public Response<BookingResponse> createBooking(CreateBookingRequest bookingRequest) {
        Account account = checkAccount();

        Schedule schedule = scheduleRepository.findById(bookingRequest.getScheduleId())
                .orElseThrow(() -> new NotFoundException("Schedule not found with id: " + bookingRequest.getScheduleId()));

        Group group = groupRepository.findById(bookingRequest.getGroupId())
                .orElseThrow(() -> new NotFoundException("Group not found with id: " + bookingRequest.getGroupId()));

        if (bookingRepository.findByAccountAndScheduleAndIsDeletedFalse(account, schedule).isPresent()) {
            throw new NotFoundException("Booking already exists with the same schedule and account!");
        }

        Booking booking = createNewBooking(bookingRequest, account, group, schedule);
        bookingRepository.save(booking);

        createNotification(booking, booking.getStatus().getMessage(), booking.getAccount());
        createNotification(booking, BookingStatus.UNDECIDED.getMessage(), schedule.getAccount());

        BookingResponse bookingResponse = buildBookingResponse(booking, schedule);
        return new Response<>(200, "Booking created successfully!", bookingResponse);
    }

    public Response<List<UpcomingBookingResponseDTO>> viewUpcomingBookings() {
        Account account = checkAccount();
        Group group = groupRepository.findByStudentsContaining(account).orElse(null);

        if (group == null || group.getBookings() == null) {
            return new Response<>(404, "No group or bookings found for the account.", Collections.emptyList());
        }

        List<UpcomingBookingResponseDTO> data = group.getBookings().stream()
                .filter(booking -> booking.getStatus() == BookingStatus.PROCESSING || booking.getStatus() == BookingStatus.SUCCESSFUL)
                .map(this::mapToUpcomingBookingResponseDTO)
                .collect(Collectors.toList());

        return new Response<>(200, "Retrieve booking successfully!", data);
    }

    private Booking createNewBooking(CreateBookingRequest bookingRequest, Account account, Group group, Schedule schedule) {
        return Booking.builder()
                .location(bookingRequest.getLocation())
                .locationNote(bookingRequest.getLocationNote())
                .status(BookingStatus.PROCESSING)
                .account(account)
                .group(group)
                .schedule(schedule)
                .createdAt(LocalDateTime.now())
                .isDeleted(false)
                .build();
    }

    private void createNotification(Booking booking, String message, Account account) {
        Notification notification = new Notification();
        notification.setMessage(message);
        notification.setDate(booking.getSchedule().getDate());
        notification.setStatus(booking.getStatus());
        notification.setAccount(account);
        notification.setBooking(booking);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setIsDeleted(false);
        notificationRepository.save(notification);
    }

    private BookingResponse buildBookingResponse(Booking booking, Schedule schedule) {
        return BookingResponse.builder()
                .bookingId(booking.getBookingId())
                .location(booking.getLocation())
                .locationNote(booking.getLocationNote())
                .scheduleId(schedule.getId())
                .message(booking.getStatus().getMessage())
                .status(booking.getStatus())
                .mentorName(schedule.getAccount().getName())
                .build();
    }

    private UpcomingBookingResponseDTO mapToUpcomingBookingResponseDTO(Booking booking) {
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
    }

    public Account checkAccount() {
        Account account = accountUtils.getCurrentAccount();
        if (account == null) {
            throw new AuthAppException(ErrorCode.NOT_LOGIN);
        }
        return accountRepository.findByEmail(account.getEmail())
                .orElseThrow(() -> new AuthAppException(ErrorCode.ACCOUNT_NOT_FOUND));
    }
}

