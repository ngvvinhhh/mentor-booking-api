package com.swd392.mentorbooking.controller;

import com.swd392.mentorbooking.dto.Response;
import com.swd392.mentorbooking.dto.walletlog.WalletLogResponse;
import com.swd392.mentorbooking.service.WalletLogService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin("**")
@RequestMapping("/wallet-log")
@SecurityRequirement(name = "api")
public class WalletLogController {

    @Autowired
    WalletLogService walletLogService;

    @GetMapping("/view")
    public Response<List<WalletLogResponse>> getWalletLogByAccount() {
        return walletLogService.getWalletLogsByWallet();
    }
}
