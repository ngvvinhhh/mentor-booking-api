package com.swd392.mentorbooking.utils;

import com.google.cloud.storage.BlobId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.UUID;

@Service
public class FirebaseStorageService {

    @Autowired
    private Storage storage;

    private File convertToFile(MultipartFile multipartFile, String fileName) throws IOException {
        File tempFile = new File(fileName);
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(multipartFile.getBytes());
        }
        return tempFile;
    }

    private String uploadFile(File file, String fileName) throws IOException {
        BlobId blobId = BlobId.of("mentor-booking-3d46a.appspot.com", fileName); // Thay bằng tên bucket của bạn
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("media").build();
        storage.create(blobInfo, Files.readAllBytes(file.toPath()));

        String DOWNLOAD_URL = "https://firebasestorage.googleapis.com/v0/b/mentor-booking-3d46a.appspot.com/o/%s?alt=media";
        return String.format(DOWNLOAD_URL, URLEncoder.encode(fileName, StandardCharsets.UTF_8));
    }

    private String getExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf("."));
    }

    public String upload(MultipartFile multipartFile) {
        try {
            String fileName = multipartFile.getOriginalFilename();
            if (fileName != null) {
                fileName = UUID.randomUUID().toString().concat(this.getExtension(fileName));  // Tạo tên file ngẫu nhiên
            }

            File file = this.convertToFile(multipartFile, fileName);  // Chuyển MultipartFile thành File
            String URL = this.uploadFile(file, fileName);  // Upload và nhận link tải file
            file.delete();  // Xóa file tạm sau khi upload
            return URL;
        } catch (Exception e) {
            e.printStackTrace();
            return "Image couldn't upload, Something went wrong";
        }
    }
}
