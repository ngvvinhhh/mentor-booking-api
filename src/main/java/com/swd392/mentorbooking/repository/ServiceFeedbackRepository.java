package com.swd392.mentorbooking.repository;

import com.swd392.mentorbooking.entity.ServiceFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceFeedbackRepository extends JpaRepository<ServiceFeedback, Long> {
}
