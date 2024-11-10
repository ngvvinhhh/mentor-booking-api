package com.swd392.mentorbooking.repository;

import com.swd392.mentorbooking.entity.Otp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OTPRepository extends JpaRepository<Otp, Long> {
    Optional<Otp> findByEmail(String email);

    List<Otp> findByExpirationTimeBefore(LocalDateTime expirationTime);
}
