package com.swd392.mentorbooking.repository;

import com.swd392.mentorbooking.entity.Account;
import com.swd392.mentorbooking.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Optional<Wallet> findByAccount(Account account);
}
