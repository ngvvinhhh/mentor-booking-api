package com.swd392.mentorbooking.service;

import com.swd392.mentorbooking.dto.Response;
import com.swd392.mentorbooking.entity.Account;
import com.swd392.mentorbooking.entity.Enum.RoleEnum;
import com.swd392.mentorbooking.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    @Autowired
    private AccountRepository accountRepository;

    public Response<List<Account>> getAllAccountByRole(String role) {

        // Get all account by role, if role = null => Get all role
        if (role == null) {
            // Get data
            List<Account> data = accountRepository.findAll();

            //Response message
            String message = "Retrieve data successfully!";
            return new Response<>(200, message, data);
        }

        else if (role.equalsIgnoreCase("mentor")) {
            // Get data
            List<Account> data = accountRepository.findAccountsByRole(RoleEnum.MENTOR);

            //Response message
            String message = "Retrieve data successfully!";
            return new Response<>(200, message, data);
        }else if (role.equalsIgnoreCase("student")) {
            // Get data
            List<Account> data = accountRepository.findAccountsByRole(RoleEnum.STUDENT);

            //Response message
            String message = "Retrieve data successfully!";
            return new Response<>(200, message, data);
        } else {
            // Get data
            List<Account> data = null;

            //Response message
            String message = "Your role is not supported!";
            return new Response<>(200, message, data);
        }
    }
}
