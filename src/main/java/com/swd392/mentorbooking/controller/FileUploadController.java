package com.swd392.mentorbooking.controller;

import com.swd392.mentorbooking.entity.Account;
import com.swd392.mentorbooking.repository.AccountRepository;
import com.swd392.mentorbooking.utils.FirebaseStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
public class FileUploadController {

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final List<String> VALID_IMAGE_TYPES = List.of("image/jpeg", "image/png", "image/gif");


    @Autowired
    private FirebaseStorageService firebaseStorageService;

    @Autowired
    private AccountRepository accountRepository;

    @PostMapping("/upload-avatar")
    public ResponseEntity<String> uploadAvatar(@RequestParam("file") MultipartFile file) {
        try {
            // Kiểm tra kích thước file
            if (file.getSize() > MAX_FILE_SIZE) {
                return ResponseEntity.badRequest().body("File size exceeds the maximum allowed size of 5MB.");
            }

            // Kiểm tra định dạng file
            String contentType = file.getContentType();
            if (contentType == null || !VALID_IMAGE_TYPES.contains(contentType)) {
                return ResponseEntity.badRequest().body("Invalid file type. Only JPEG, PNG, and GIF are allowed.");
            }

            String fileUrl = firebaseStorageService.uploadFile(file, "avatars");
            return ResponseEntity.ok(fileUrl);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error uploading file: " + e.getMessage());
        }
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

    @PostMapping("/upload-blog-image")
    public ResponseEntity<String> uploadBlogImage(@RequestParam("file") MultipartFile file) {
        try {
            // Kiểm tra kích thước file
            if (file.getSize() > MAX_FILE_SIZE) {
                return ResponseEntity.badRequest().body("File size exceeds the maximum allowed size of 5MB.");
            }

            // Kiểm tra định dạng file
            String contentType = file.getContentType();
            if (contentType == null || !VALID_IMAGE_TYPES.contains(contentType)) {
                return ResponseEntity.badRequest().body("Invalid file type. Only JPEG, PNG, and GIF are allowed.");
            }

            String fileUrl = firebaseStorageService.uploadFile(file, "blogs");
            return ResponseEntity.ok(fileUrl);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error uploading file: " + e.getMessage());
        }
    }
}
