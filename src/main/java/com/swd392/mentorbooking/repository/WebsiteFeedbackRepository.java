package com.swd392.mentorbooking.repository;

import com.swd392.mentorbooking.entity.WebsiteFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WebsiteFeedbackRepository extends JpaRepository<WebsiteFeedback, Long> {
    List<WebsiteFeedback> findByIsDeletedFalse();

}
