package com.swd392.mentorbooking.service;

import com.swd392.mentorbooking.dto.Response;
import com.swd392.mentorbooking.dto.booking.BookingResponse;
import com.swd392.mentorbooking.dto.booking.CreateBookingRequest;
import com.swd392.mentorbooking.dto.booking.UpcomingBookingResponseDTO;
import com.swd392.mentorbooking.entity.*;
import com.swd392.mentorbooking.entity.Enum.BookingStatus;
import com.swd392.mentorbooking.entity.Enum.ScheduleStatus;
import com.swd392.mentorbooking.exception.ErrorCode;
import com.swd392.mentorbooking.exception.auth.AuthAppException;
import com.swd392.mentorbooking.exception.group.NotFoundException;
import com.swd392.mentorbooking.repository.*;
import com.swd392.mentorbooking.utils.AccountUtils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class BookingService {

    private final AccountUtils accountUtils;
    private final BookingRepository bookingRepository;
    private final ScheduleRepository scheduleRepository;
    private final GroupRepository groupRepository;
    private final NotificationRepository notificationRepository;
    private final AccountRepository accountRepository;
    private final WalletRepository walletRepository;
    private final ServiceRepository serviceRepository;

    @Autowired
    public BookingService(AccountUtils accountUtils,
                          BookingRepository bookingRepository,
                          ScheduleRepository scheduleRepository,
                          GroupRepository groupRepository,
                          NotificationRepository notificationRepository,
                          AccountRepository accountRepository,
                          WalletRepository walletRepository,
                          ServiceRepository serviceRepository) {
        this.accountUtils = accountUtils;
        this.bookingRepository = bookingRepository;
        this.scheduleRepository = scheduleRepository;
        this.groupRepository = groupRepository;
        this.notificationRepository = notificationRepository;
        this.accountRepository = accountRepository;
        this.walletRepository = walletRepository;
        this.serviceRepository = serviceRepository;
    }

    @Transactional
    public Response<BookingResponse> createBooking(CreateBookingRequest bookingRequest) {
        Account account = checkAccount();

        Schedule schedule = scheduleRepository.findById(bookingRequest.getScheduleId())
                .orElseThrow(() -> new NotFoundException("Schedule not found with id: " + bookingRequest.getScheduleId()));

        Group group = account.getGroup();

        if (group == null) {
            return new Response<>(400, "You must be in a group or create a group before booking.", null);
        }

        if (bookingRepository.findByAccountAndScheduleAndIsDeletedFalse(account, schedule).isPresent()) {
            throw new NotFoundException("Booking already exists with the same schedule and account!");
        }

        if (bookingRepository.findByGroupAndSchedule(group, schedule).isPresent()) {
            return new Response<>(400, "A member from the same group has already booked this schedule.", null);
        }

        Wallet wallet = walletRepository.findByAccount(account);
        if (wallet == null) {
            throw new NotFoundException("Wallet not found for this account!");
        }

        Services services = serviceRepository.findByAccount(schedule.getAccount());
        if (services == null) {
            throw new NotFoundException("Service not found!!");
        }

        double hoursBooked = Duration.between(schedule.getStartTime(), schedule.getEndTime()).toMinutes() / 60.0;
        double totalCost = hoursBooked * services.getPrice();
        BigDecimal totalCostRounded = new BigDecimal(totalCost).setScale(2, RoundingMode.HALF_UP); // làm tròn đến 2 số
        totalCost = totalCostRounded.doubleValue();

        if(wallet.getTotal() < totalCost){
            return new Response<>(400, "Insufficient balance to book this service.", null);
        }



        Booking booking = createNewBooking(bookingRequest, schedule.getAccount(), group, schedule, totalCost);
        bookingRepository.save(booking);

        //Student
        createNotification(booking, booking.getStatus().getMessage(), account);
        //Mentor
        createNotification(booking, BookingStatus.UNDECIDED.getMessage(), schedule.getAccount());

        BookingResponse bookingResponse = buildBookingResponse(booking, schedule, totalCost);

        schedule.setStatus(ScheduleStatus.PENDING);
        scheduleRepository.save(schedule);

        return new Response<>(200, "Booking created successfully!", bookingResponse);
    }



    public Response<List<UpcomingBookingResponseDTO>> viewUpcomingBookings() {
        Account account = checkAccount();
        Group group = groupRepository.findByStudentsContaining(account).orElse(null);

        if (group == null || group.getBookings() == null) {
            return new Response<>(404, "No group or bookings found for the account.", Collections.emptyList());
        }

        List<UpcomingBookingResponseDTO> data = group.getBookings().stream()
                .filter(booking -> booking.getStatus() == BookingStatus.PROCESSING || booking.getStatus() == BookingStatus.SUCCESSFUL || booking.getStatus() == BookingStatus.DECLINED)
                .map(this::mapToUpcomingBookingResponseDTO)
                .collect(Collectors.toList());

        return new Response<>(200, "Retrieve booking successfully!", data);
    }

    private Booking createNewBooking(CreateBookingRequest bookingRequest, Account account, Group group, Schedule schedule, Double total) {
        return Booking.builder()
                .location(bookingRequest.getLocation())
                .locationNote(bookingRequest.getLocationNote())
                .total(total)
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

    private BookingResponse buildBookingResponse(Booking booking, Schedule schedule, Double total) {
        return BookingResponse.builder()
                .bookingId(booking.getBookingId())
                .location(booking.getLocation())
                .locationNote(booking.getLocationNote())
                .total(total)
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
                .mentorAvatar(booking.getAccount() != null ? booking.getAccount().getAvatar() : "N/A")
                .mentorPrice(booking.getAccount() != null ? booking.getAccount().getService().getPrice() : 0)
                .mentorSpecializations(booking.getAccount() != null ? booking.getAccount().getSpecializations() : null)
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

    public Response<BookingResponse> cancelBooking(Long bookingId) {
        return null;
    }
}

