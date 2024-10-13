package com.swd392.mentorbooking.repository;

import com.swd392.mentorbooking.entity.Invitation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InvitaionRepository  extends JpaRepository<Invitation, Long> {
    Optional<Invitation> findByEmailAndGroupIdAndToken(String email, Long groupId, String token);

    Optional<Invitation> findByToken (String token);
}
