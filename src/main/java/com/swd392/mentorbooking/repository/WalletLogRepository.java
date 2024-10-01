package com.swd392.mentorbooking.repository;

import com.swd392.mentorbooking.entity.WalletLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletLogRepository extends JpaRepository<WalletLog, Long> {
}
