package com.swd392.mentorbooking.repository;

import com.swd392.mentorbooking.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
