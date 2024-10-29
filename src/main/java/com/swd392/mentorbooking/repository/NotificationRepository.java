package com.swd392.mentorbooking.repository;

import com.swd392.mentorbooking.entity.Account;
import com.swd392.mentorbooking.entity.Booking;
import com.swd392.mentorbooking.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByBookingAndIsDeletedFalse(Booking booking);

    List<Notification> findByAccountAndIsDeletedFalse(Account account);

    Optional<Notification> findByBookingAndAccount(Booking booking, Account account);

}
