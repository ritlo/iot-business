package com.javaproject;

import com.pi4j.io.gpio.digital.*;

import org.threeten.bp.LocalTime;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.google.firebase.messaging.AndroidConfig.Priority;
import com.pi4j.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;

public class PersonCounter extends Thread {
    static int SENSOR_PIN = 15;
    int currentPersonCount;
    User u;

    PersonCounter(User u) {
        this.u = u;
    }

    DateTimeFormatter currentDateFormatter = DateTimeFormatter.ofPattern("dd/MM/YYYY");
    LocalDate currentDate = LocalDate.now();
    LocalTime currentTime = LocalTime.now();
    LocalTime openingTime = LocalTime.parse("12:00");
    LocalTime closingTime = LocalTime.parse("13:00");

    void initialiseFirebase() throws IOException {
        FileInputStream serviceAccount = new FileInputStream(
                "/home/pi/Desktop/javaproject/serviceAccountKey.json");

        // FirebaseOptions options = new FirebaseOptions.Builder()
        // .setCredentials(GoogleCredentials.fromStream(serviceAccount))
        // .setDatabaseUrl("https://fireproject-ee4f5.firebaseio.com").build();

        FirebaseOptions options = FirebaseOptions.builder().setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://fireproject-ee4f5.firebaseio.com").build();

        FirebaseApp.initializeApp(options);
    }

    void sendMessage() throws FirebaseMessagingException {
        
        String title = "Person Detected at time " + LocalTime.now();
        Message message = Message.builder().setNotification(Notification.builder().setTitle(title).build())
                .setTopic("Firetest").setAndroidConfig(AndroidConfig.builder().setPriority(Priority.HIGH).build())
                .build();

        String response = FirebaseMessaging.getInstance().send(message);

        System.out.println("Person Detected, Alert Sent");
    }

    public void run() {
        try {
            initialiseFirebase();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        String date = currentDateFormatter.format(currentDate);

        var pi4j = Pi4J.newAutoContext();

        var config = DigitalInput.newConfigBuilder(pi4j).address(SENSOR_PIN).pull(PullResistance.PULL_DOWN).build();

        DigitalInputProvider digitalInputProvider = pi4j.provider("pigpio-digital-input");

        var input = digitalInputProvider.create(config);
        try {
            currentPersonCount = u.PersonCount.get(date);
        } catch (NullPointerException e2) {
            u.PersonCount.put(date, 0);
        }

        System.out.println("Running");

        input.addListener(e -> {

            if (e.state() == DigitalState.HIGH) {
                // Night test
                // if (!(LocalTime.now().isAfter(openingTime) && LocalTime.now().isBefore(closingTime))) {
                //     currentPersonCount++;
                //     u.PersonCount.put(date, currentPersonCount);
                //     System.out.println("Person detected " + currentPersonCount + "th time");

                // } else {
                //     try {
                //         sendMessage();
                //     } catch (FirebaseMessagingException e1) {
                //         // TODO Auto-generated catch block
                //         e1.printStackTrace();
                //     }
                // }
                if (LocalTime.now().isAfter(openingTime) && LocalTime.now().isBefore(closingTime)) {
                    currentPersonCount++;
                    u.PersonCount.put(date, currentPersonCount);
                    System.out.println("Person detected " + currentPersonCount + "th time");

                } else {
                    try {
                        sendMessage();
                    } catch (FirebaseMessagingException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                }
                

            }
        });

        while (!interrupted()) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e1) {
                // TODO Auto-generated catch block
                System.out.println("Person Counter Stopping");
                
            }
        }
        pi4j.shutdown();
        System.out.println("Person Counter Stopped");

    }

}