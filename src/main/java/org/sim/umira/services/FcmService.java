package org.sim.umira.services;

import org.eclipse.microprofile.context.ManagedExecutor;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class FcmService {

    @Inject
    ManagedExecutor executor;
    
    public String sendToToken(String token, String title, String body) throws Exception {
        Message message = Message.builder()
                .setToken(token)
                .setNotification(
                        Notification.builder()
                                .setTitle(title)
                                .setBody(body)
                                .build()
                )
                .build();

        return FirebaseMessaging.getInstance().send(message);
    }

    public void sendAsync(String token, String title, String body) {
        executor.runAsync(() -> {
            try {
                sendToToken(token, title, body);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
