package com.swd392.mentorbooking.controller;

import com.swd392.mentorbooking.entity.Account;
import com.swd392.mentorbooking.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AccountController {
    @Autowired
    AccountService authenticationService;

    @PostMapping("/register")
    public ResponseEntity register(@Valid @RequestBody Account account){
        Account newAccount = authenticationService.register(account);
        return ResponseEntity.ok(newAccount);
    }

    @GetMapping("/account")
    public ResponseEntity getAllAccount(){
        List<Account> accounts = authenticationService.getAllAccount();
        return ResponseEntity.ok(accounts);
    }
}
