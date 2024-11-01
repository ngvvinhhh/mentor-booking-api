package com.swd392.mentorbooking.service;

import com.swd392.mentorbooking.dto.Response;
import com.swd392.mentorbooking.dto.walletlog.WalletLogResponse;
import com.swd392.mentorbooking.entity.Account;
import com.swd392.mentorbooking.entity.WalletLog;
import com.swd392.mentorbooking.repository.WalletLogRepository;
import com.swd392.mentorbooking.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WalletLogService {

    @Autowired
    private AccountUtils accountUtils;

    @Autowired
    private WalletLogRepository walletLogRepository;

    public Response<List<WalletLogResponse>> getWalletLogsByWallet() {
        Account account = accountUtils.getCurrentAccount();
        if (account == null) {
            return new Response<>(401, "Please login first", null);
        }

        // Get a list of WalletLogs for the current user's wallet
        List<WalletLog> walletLogs = walletLogRepository.findByWalletId(account.getWallet().getId());

            // Convert from WalletLog to WalletLogResponse
        List<WalletLogResponse> walletLogResponses = walletLogs.stream()
                .map(walletLog -> new WalletLogResponse(
                        walletLog.getId(),
                        walletLog.getAmount(),
                        walletLog.getFrom(),
                        walletLog.getTo(),
                        walletLog.getTypeOfLog(),
                        walletLog.getCreatedAt()
                ))
                .collect(Collectors.toList());


        return new Response<>(200, "Retrieve wallet logs successfully!", walletLogResponses);
    }
}
