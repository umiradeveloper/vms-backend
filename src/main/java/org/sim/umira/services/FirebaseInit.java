package org.sim.umira.services;

import java.io.InputStream;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;

@ApplicationScoped
public class FirebaseInit {
    void onStart(@Observes StartupEvent ev) {
        try {
            InputStream is = Thread.currentThread()
                    .getContextClassLoader()
                    .getResourceAsStream("firebase-service-account.json");

            if (is == null) {
                throw new RuntimeException("firebase-service-account.json NOT FOUND");
            }

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(is))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                System.out.println("🔥 Firebase initialized");
            }

        } catch (Exception e) {
            throw new RuntimeException("❌ Firebase init failed", e);
        }
    }
}
