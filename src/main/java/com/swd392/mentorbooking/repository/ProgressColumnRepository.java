package com.swd392.mentorbooking.repository;

import com.swd392.mentorbooking.entity.ProgressColumn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProgressColumnRepository extends JpaRepository<ProgressColumn, Long> {
}
