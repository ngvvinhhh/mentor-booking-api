package com.swd392.mentorbooking.repository;

import com.swd392.mentorbooking.entity.ProgressTask;
import com.swd392.mentorbooking.entity.ProjectProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProgressTaskRepository extends JpaRepository<ProgressTask, Long> {
    List<ProgressTask> findByProjectProgressOrderByTaskKeyAsc(ProjectProgress projectProgress);
}
