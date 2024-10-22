package com.swd392.mentorbooking.repository;

import com.swd392.mentorbooking.entity.Otp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OTPRepository extends JpaRepository<Otp, Long> {
    Optional<Otp> findByEmail(String email);

    List<Otp> findByExpirationTimeBefore(LocalDateTime expirationTime);
}
