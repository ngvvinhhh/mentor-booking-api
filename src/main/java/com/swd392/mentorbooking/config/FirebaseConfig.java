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
        InputStream inputStream = FirebaseConfig.class.getClassLoader().getResourceAsStream("circuit-project-8bdd7-firebase-adminsdk-xu19u-20e1166d5f.json"); // Đổi tên file phù hợp
        Credentials credentials = null;
        if (inputStream != null) {
            credentials = GoogleCredentials.fromStream(inputStream);
        }
        return StorageOptions.newBuilder()
                .setCredentials(credentials)
                .setProjectId("circuit-project-8bdd7")
                .build().getService();
    }

    @Bean
    public FirebaseMessaging firebaseMessaging() throws IOException {
        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(
                        new ClassPathResource("circuit-project-8bdd7-firebase-adminsdk-xu19u-20e1166d5f.json").getInputStream());
        FirebaseOptions firebaseOptions = FirebaseOptions.builder()
                .setCredentials(googleCredentials)
                .setProjectId("circuit-project-8bdd7")
                .build();
        FirebaseApp app = FirebaseApp.initializeApp(firebaseOptions, "circuit-project-8bdd7");
        return FirebaseMessaging.getInstance(app);
    }

    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        // Đường dẫn tới file serviceAccountKey.json mà bạn tải từ Firebase Console
        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(
                        new ClassPathResource("circuit-project-8bdd7-firebase-adminsdk-xu19u-20e1166d5f.json").getInputStream());

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(googleCredentials)
                .setProjectId("circuit-project-8bdd7")
                .build();

        return FirebaseApp.initializeApp(options);
    }

}
