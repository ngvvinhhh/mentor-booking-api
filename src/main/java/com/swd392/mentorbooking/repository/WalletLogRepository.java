package com.swd392.mentorbooking.repository;

import com.swd392.mentorbooking.entity.WalletLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WalletLogRepository extends JpaRepository<WalletLog, Long> {
    List<WalletLog> findByWalletId(Long walletId);
}
