package com.swd392.mentorbooking.repository;

import com.swd392.mentorbooking.entity.ProjectProgress;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectProgressRepository extends JpaRepository<ProjectProgress, Long> {
    List<ProjectProgress> findAllByIsDeletedFalse(Sort sort);
}
