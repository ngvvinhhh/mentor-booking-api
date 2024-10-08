package com.swd392.mentorbooking.utils;

import org.springframework.stereotype.Service;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class FirebaseStorageService {
    @Value("${firebase.storage.bucket}")
    private String bucketName;

    public String uploadFile(MultipartFile file, String folderName) throws IOException {
        // Tạo tên file với UUID để đảm bảo không trùng lặp
        String fileName = folderName + "/" + UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Storage storage = StorageOptions.getDefaultInstance().getService();

        // Tạo BlobInfo và upload file lên Firebase Storage
        BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, fileName).build();
        storage.create(blobInfo, file.getBytes());

        // Trả về URL của file đã upload
        return String.format("https://storage.googleapis.com/%s/%s", bucketName, fileName);
    }
}
