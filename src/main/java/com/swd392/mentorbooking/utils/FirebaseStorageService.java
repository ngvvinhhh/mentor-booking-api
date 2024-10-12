package com.swd392.mentorbooking.utils;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.swd392.mentorbooking.dto.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class FirebaseStorageService {

    @Autowired
    private Storage storage;

    @Autowired
    public FirebaseStorageService(Storage storage) {
        this.storage = storage;
    }

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

    public Response<List<String>> upload(MultipartFile multipartFile) {
        try {
            String fileName = multipartFile.getOriginalFilename();
            if (fileName != null) {
                fileName = UUID.randomUUID().toString().concat(this.getExtension(fileName));  // Tạo tên file ngẫu nhiên
            }

            File file = this.convertToFile(multipartFile, fileName);  // Chuyển MultipartFile thành File
            String URL = this.uploadFile(file, fileName);  // Upload và nhận link tải file
            file.delete();  // Xóa file tạm sau khi upload
            List<String> response = new ArrayList<>();
            response.add(URL);
            response.add(fileName);
            return new Response<>(200, "Upload image successful", response);
        } catch (Exception e) {
            e.printStackTrace();
            return new Response<>(400, "Image couldn't upload, Something went wrong", null);
        }
    }

    public String getAvatarUrl(String fileName) {
        // Define your bucket name (change this to your Firebase bucket name)
        String bucketName = "your-firebase-project-id.appspot.com";

        // Get the blob (file) from the bucket
        Blob blob = storage.get(bucketName, "avatars/" + fileName);  // Assuming avatars are in 'avatars/' folder

        if (blob == null) {
            throw new RuntimeException("File not found in Firebase Storage: " + fileName);
        }

        // Get a signed URL (downloadable link to the file)
        String downloadUrl = blob.getMediaLink();  // This is the URL to access the file

        return downloadUrl;
    }
}
