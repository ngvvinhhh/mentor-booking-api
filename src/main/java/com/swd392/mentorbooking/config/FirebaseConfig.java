package com.swd392.mentorbooking.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    @Bean
    public FirebaseApp initializeFirebase() throws IOException {
        // Sử dụng ClassPathResource để lấy tệp tin từ thư mục src/main/resources
        InputStream serviceAccount = new ClassPathResource("mentor-booking-3d46a-firebase-adminsdk-ohqij-8f07903118.json").getInputStream();

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(new ClassPathResource("mentor-booking-3d46a-firebase-adminsdk-ohqij-8f07903118.json").getInputStream()))
                .setStorageBucket("mentor-booking-3d46a.appspot.com")  // Đảm bảo tên bucket đúng
                .build();

        return FirebaseApp.initializeApp(options);
    }
}
