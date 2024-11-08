package com.swd392.mentorbooking.controller;

import com.swd392.mentorbooking.dto.Response;
import com.swd392.mentorbooking.dto.wallet.WalletResponse;
import com.swd392.mentorbooking.service.WalletService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin("**")
@RequestMapping("/wallet")
@SecurityRequirement(name = "api")
public class WalletController {

    @Autowired
    WalletService walletService;

    @GetMapping("/view")
    public Response<WalletResponse> getWalletByAccount() {
        return walletService.getWalletByAccount();
    }

}
