package com.swd392.mentorbooking.repository;

import com.swd392.mentorbooking.entity.Account;
import com.swd392.mentorbooking.entity.Booking;
import com.swd392.mentorbooking.entity.Enum.BookingStatus;
import com.swd392.mentorbooking.entity.Group;
import com.swd392.mentorbooking.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findBookingsByAccountAndIsDeletedFalse(Account mentorAccount);

    Optional<Booking> findByAccountAndScheduleAndIsDeletedFalse(Account account, Schedule schedule);

    List<Booking> findByAccount(Account account);

    // Tính tổng số lượng booking không bị xóa
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.isDeleted = false")
    long countActiveBookings();

    @Query("SELECT b FROM Booking b WHERE b.schedule = :schedule AND b.group = :group AND b.isDeleted = false")
    Optional<Booking> findByGroupAndSchedule(@Param("group") Group group, @Param("schedule") Schedule schedule);

    List<Booking> findBookingsByAccountAndStatusAndIsDeletedFalse(Account account, BookingStatus bookingStatus);

    @Query("SELECT SUM(b.total) FROM Booking b WHERE b.status = 'COMPLETED' AND b.isDeleted = false")
    String findTotalRevenueOfCompletedBookings();

    @Query("SELECT FUNCTION('DAYOFWEEK', b.createdAt), SUM(b.total) " +
            "FROM Booking b " +
            "WHERE b.status = 'COMPLETED' " +
            "AND b.createdAt BETWEEN :startOfWeek AND :endOfWeek " +
            "GROUP BY FUNCTION('DAYOFWEEK', b.createdAt) " +
            "ORDER BY FUNCTION('DAYOFWEEK', b.createdAt)")
    List<Object[]> findWeeklyRevenue(@Param("startOfWeek") LocalDateTime startOfWeek,
                                     @Param("endOfWeek") LocalDateTime endOfWeek);
}
