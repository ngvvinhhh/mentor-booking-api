package com.swd392.mentorbooking.repository;

import com.swd392.mentorbooking.entity.ServiceFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceFeedbackRepository extends JpaRepository<ServiceFeedback, Long> {
    // Query to get all feedbacks where isDeleted is false
    @Query("SELECT f FROM ServiceFeedback f WHERE f.isDeleted = false")
    List<ServiceFeedback> findAllActiveFeedbacks();
}
