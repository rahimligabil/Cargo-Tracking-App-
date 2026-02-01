package com.gabil.kargo.security.config;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class FirebaseConfig {

    private final ResourceLoader resourceLoader;

    @Value("${firebase.enabled:false}")
    private boolean firebaseEnabled;

    @Value("${firebase.service-account-path:classpath:firebase-service-account.json}")
    private String serviceAccountPath;

    @PostConstruct
    public void initFirebase() throws IOException {

        if (!firebaseEnabled) {
            log.warn("Firebase integration disabled (firebase.enabled=false); skipping initialization.");
            return;
        }

        if (!FirebaseApp.getApps().isEmpty()) {
            log.debug("FirebaseApp already initialized, skipping duplicate initialization.");
            return;
        }

        Resource serviceAccountResource = resourceLoader.getResource(serviceAccountPath);

        if (!serviceAccountResource.exists()) {
            log.error("Firebase service account resource not found at '{}'. Firebase integration remains inactive.", serviceAccountPath);
            return;
        }

        try (InputStream serviceAccountStream = serviceAccountResource.getInputStream()) {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccountStream))
                    .build();

            FirebaseApp.initializeApp(options);
            log.info("FirebaseApp initialized successfully using '{}'.", serviceAccountPath);
        }
    }
}
