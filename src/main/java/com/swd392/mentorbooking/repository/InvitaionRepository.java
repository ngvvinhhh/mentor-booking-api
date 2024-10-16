package com.swd392.mentorbooking.repository;

import com.swd392.mentorbooking.entity.Invitation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InvitaionRepository  extends JpaRepository<Invitation, Long> {

    Optional<Invitation> findByToken (String token);

    List<Invitation> findByEmailAndIsDeletedFalse(String email);
}
