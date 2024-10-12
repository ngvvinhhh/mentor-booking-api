package com.swd392.mentorbooking.repository;

import com.swd392.mentorbooking.entity.Account;
import com.swd392.mentorbooking.entity.Enum.RoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByEmail(String email);

    List<Account> findAccountsByRole(RoleEnum role);

    List<Account> findByIdIn(List<Long> accountIds);

}
