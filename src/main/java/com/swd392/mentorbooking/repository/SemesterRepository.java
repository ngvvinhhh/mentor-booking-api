package com.swd392.mentorbooking.repository;

import com.swd392.mentorbooking.entity.Semester;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SemesterRepository extends JpaRepository<Semester, Long> {

    List<Semester> findByIsDeletedFalse();
}
