package com.swd392.mentorbooking.config;

import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    @Bean
    public Storage firebaseStorage() throws Exception {
        InputStream inputStream = FirebaseConfig.class.getClassLoader().getResourceAsStream("mentor-booking-3d46a-firebase-adminsdk-ohqij-8f07903118.json"); // Đổi tên file phù hợp
        Credentials credentials = null;
        if (inputStream != null) {
            credentials = GoogleCredentials.fromStream(inputStream);
        }
        return StorageOptions.newBuilder().setCredentials(credentials).build().getService();
    }

    @Bean
    public FirebaseMessaging firebaseMessaging() throws IOException {
        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(
                        new ClassPathResource("mentor-booking-3d46a-firebase-adminsdk-ohqij-8f07903118.json").getInputStream());
        FirebaseOptions firebaseOptions = FirebaseOptions
                .builder()
                .setCredentials(googleCredentials)
                .build();
        FirebaseApp app = FirebaseApp.initializeApp(firebaseOptions, "mentor-booking-app");
        return FirebaseMessaging.getInstance(app);
    }
}
