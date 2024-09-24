package com.swd392.mentorbooking.service;

import com.swd392.mentorbooking.entity.Account;
import com.swd392.mentorbooking.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountService {
    @Autowired
    AccountRepository accountRepository;
    public Account register (Account account) {
        Account newAccount = accountRepository.save(account);
        return newAccount;
    }

    public List<Account> getAllAccount(){
        return accountRepository.findAll();
    }
}
