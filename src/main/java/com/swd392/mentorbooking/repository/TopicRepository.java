package com.swd392.mentorbooking.repository;

import com.swd392.mentorbooking.entity.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {

    List<Topic> findStudentsByIsDeletedFalse();
}
