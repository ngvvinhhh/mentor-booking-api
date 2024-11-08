package com.swd392.mentorbooking.repository;

import com.swd392.mentorbooking.dto.dashboard.TopDealUser;
import com.swd392.mentorbooking.entity.Account;
import com.swd392.mentorbooking.entity.WalletLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WalletLogRepository extends JpaRepository<WalletLog, Long> {
    List<WalletLog> findByWalletId(Long walletId);

    @Query("SELECT new com.swd392.mentorbooking.dto.dashboard.TopDealUser(a.id, a.avatar, a.name, a.email, SUM(wl.amount)) " +
            "FROM Account a " +
            "JOIN Wallet w ON a.id = w.account.id " +
            "JOIN WalletLog wl ON w.id = wl.wallet.id " +
            "WHERE a.email != 'admin' AND a.role = 'MENTOR' " +  // Điều kiện loại trừ người có email là 'admin'
            "GROUP BY a.id " +
            "ORDER BY SUM(wl.amount) DESC")
    List<TopDealUser> findTop7UsersByAmount();
}
