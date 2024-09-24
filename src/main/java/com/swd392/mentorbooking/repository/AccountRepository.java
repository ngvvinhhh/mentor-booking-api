package com.swd392.mentorbooking.repository;

import com.swd392.mentorbooking.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Integer> {
}
