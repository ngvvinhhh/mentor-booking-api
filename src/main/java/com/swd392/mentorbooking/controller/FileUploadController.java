package com.swd392.mentorbooking.controller;

import com.swd392.mentorbooking.dto.Response;
import com.swd392.mentorbooking.entity.Account;
import com.swd392.mentorbooking.repository.AccountRepository;
import com.swd392.mentorbooking.utils.FirebaseStorageService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@CrossOrigin("**")
@RequestMapping("/firebase")
@SecurityRequirement(name = "api")
public class FileUploadController {

    @Autowired
    private FirebaseStorageService firebaseStorageService;

    @Autowired
    private AccountRepository accountRepository;

    @PostMapping("/upload-avatar")
    public Response<List<String>> uploadAvatar(@RequestParam("file") MultipartFile file) {
        return firebaseStorageService.upload(file);
    }

    @GetMapping("/account/{accountId}/avatar")
    public ResponseEntity<String> getAvatar(@PathVariable Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        String avatarUrl = account.getAvatar();

        if (avatarUrl == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(avatarUrl);
    }
}
