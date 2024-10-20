package com.swd392.mentorbooking.service;


import com.swd392.mentorbooking.dto.Response;
import com.swd392.mentorbooking.dto.wallet.WalletResponse;
import com.swd392.mentorbooking.entity.Account;
import com.swd392.mentorbooking.entity.Wallet;
import com.swd392.mentorbooking.exception.ErrorCode;
import com.swd392.mentorbooking.exception.auth.AuthAppException;
import com.swd392.mentorbooking.repository.WalletRepository;
import com.swd392.mentorbooking.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WalletService {

    @Autowired
    WalletRepository walletRepository;

    @Autowired
    AccountUtils accountUtils;

    public Response<WalletResponse> getWalletByAccount() {
        Account account = accountUtils.getCurrentAccount();

        if (account == null) {
            throw new AuthAppException(ErrorCode.NOT_LOGIN);
        }

        Wallet wallet = walletRepository.findByAccount(account);


        WalletResponse walletResponse = new WalletResponse(
                wallet.getId(),
                wallet.getTotal()
        );

        return new Response<>(200, "Successfully fetched wallet!", walletResponse);
    }
}
