package com.swd392.mentorbooking.controller;

import com.swd392.mentorbooking.entity.Account;
import com.swd392.mentorbooking.repository.AccountRepository;
import com.swd392.mentorbooking.utils.FirebaseStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin("**")
@RequestMapping("/firebase")
public class FileUploadController {

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final List<String> VALID_IMAGE_TYPES = List.of("image/jpeg", "image/png", "image/gif");


    @Autowired
    private FirebaseStorageService firebaseStorageService;

    @Autowired
    private AccountRepository accountRepository;

    @PostMapping("/upload-avatar")
    public String uploadAvatar(@RequestParam("file") MultipartFile file) {
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
