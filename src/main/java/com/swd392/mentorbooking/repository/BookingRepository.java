package com.swd392.mentorbooking.repository;

import com.swd392.mentorbooking.entity.Account;
import com.swd392.mentorbooking.entity.Booking;
import com.swd392.mentorbooking.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findBookingsByAccountAndIsDeletedFalse(Account mentorAccount);

    Optional<Booking> findByAccountAndScheduleAndIsDeletedFalse(Account account, Schedule schedule);

    List<Booking> findByAccount(Account account);

}
