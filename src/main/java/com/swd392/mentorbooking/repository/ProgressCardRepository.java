package com.swd392.mentorbooking.repository;

import com.swd392.mentorbooking.entity.ProgressCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProgressCardRepository extends JpaRepository<ProgressCard, Long> {
}
