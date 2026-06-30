package org.sim.umira.configs;

import java.io.InputStream;
import java.util.List;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;

public class GoogleCalendarConfig {
    public static Calendar getService() throws Exception {

        
            InputStream is = Thread.currentThread()
                    .getContextClassLoader()
                    .getResourceAsStream("calendar-service-account.json");

                if (is == null) {
    throw new RuntimeException("calendar-service-account.json NOT FOUND in resources");
}
                

        GoogleCredentials credentials =
            GoogleCredentials.fromStream(is)
                .createScoped(
                    List.of(
                        "https://www.googleapis.com/auth/calendar"));

        return new Calendar.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                new HttpCredentialsAdapter(credentials))
                .setApplicationName("Umira Absensi App")
                .build();
    }
}
