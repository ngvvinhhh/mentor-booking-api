package com.swd392.mentorbooking.repository;

import com.swd392.mentorbooking.entity.Account;
import com.swd392.mentorbooking.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {

    // Custom query method to fetch all non-deleted groups
    List<Group> findByIsDeletedFalse();

    Optional<Group> findByStudentsContaining(Account account);

    @Query("SELECT g FROM Group g JOIN g.students s WHERE s.id IN :accountIds")
    List<Group> findGroupsByStudentIds(@Param("accountIds") List<Long> accountIds);
}
