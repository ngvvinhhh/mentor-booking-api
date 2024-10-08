package com.swd392.mentorbooking.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;

@Configuration
public class FirebaseConfig {
    @Value("${firebase.credentials.file.path}")
    private String firebaseCredentialsPath;

    @Value("${firebase.storage.bucket}")
    private String firebaseStorageBucket;

    @Bean
    public void initializeFirebase() throws IOException {
        FileInputStream serviceAccount = new FileInputStream(firebaseCredentialsPath);

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setStorageBucket(firebaseStorageBucket)
                .build();

        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options);
        }
    }
}
