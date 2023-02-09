package com.javaproject;

import com.pi4j.io.gpio.digital.*;

import org.threeten.bp.LocalTime;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.SetOptions;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
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
import java.util.ArrayList;

public class IoTEmbeddedSystem extends Thread {
    static int MOTION_SENSOR_PIN = 15;
    static int FIRE_SENSOR_PIN = 18;
    int currentDailyCount;
    User u;
    String id;
    Firestore db = FirestoreClient.getFirestore();

    IoTEmbeddedSystem(User u, String id) {
        this.u = u;
        this.id = id;
    }

    DateTimeFormatter currentDateFormatter = DateTimeFormatter.ofPattern("dd/MM/YYYY");
    LocalDate currentDate = LocalDate.now();
    LocalTime currentTime = LocalTime.now();
    LocalTime openingTime = LocalTime.parse("12:00");
    LocalTime closingTime = LocalTime.parse("21:00");

    void initialiseFirebase() throws IOException {
        FileInputStream serviceAccount = new FileInputStream(
                "/home/pi/Desktop/javaprojectopenstore/serviceAccountKey.json");

        // FirebaseOptions options = new FirebaseOptions.Builder()
        // .setCredentials(GoogleCredentials.fromStream(serviceAccount))
        // .setDatabaseUrl("https://fireproject-ee4f5.firebaseio.com").build();

        FirebaseOptions options = FirebaseOptions.builder().setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://fireproject-ee4f5.firebaseio.com").build();

        FirebaseApp.initializeApp(options);
    }

    void sendMotionMessage() throws FirebaseMessagingException {
        String time = LocalTime.now().toString();
        String date = LocalDate.now().toString();
        String desc = "Person Detected at time " + time;
        // Message message =
        // Message.builder().setNotification(Notification.builder().setTitle(title).build())
        // .setTopic("Firetest").setAndroidConfig(AndroidConfig.builder().setPriority(Priority.HIGH).build())
        // .build();
        AndroidConfig config = AndroidConfig.builder().setPriority(AndroidConfig.Priority.HIGH).build();
        Message message = Message.builder()
                .putData("event", "Burglar")
                .putData("title", "Possible Burglar Detected")
                .putData("desc", desc)
                .putData("time", time)
                .putData("date", date)
                .setTopic("Firetest")
                .setAndroidConfig(config)
                .build();

        String response = FirebaseMessaging.getInstance().send(message);
        try {
            u.PersonCount.get(date).add("PERSON DETECTED: "+time);
        } catch (Exception NullPointerException) {
            u.PersonCount.put(date,new ArrayList <String>());
            u.PersonCount.get(date).add("PERSON DETECTED: "+time);
        }
        ApiFuture<WriteResult> result = db.collection("Users").document(id).set(u,SetOptions.merge());

        System.out.println("Person Detected, Alert Sent");
    }

    void sendFireMessage() throws FirebaseMessagingException {
        String time = LocalTime.now().toString();
        String date = LocalDate.now().toString();
        String desc = "Fire Detected at time " + time;
        // Message message =
        // Message.builder().setNotification(Notification.builder().setTitle(title).build())
        // .setTopic("Firetest").setAndroidConfig(AndroidConfig.builder().setPriority(Priority.HIGH).build())
        // .build();
        AndroidConfig config = AndroidConfig.builder().setPriority(AndroidConfig.Priority.HIGH).build();
        Message message = Message.builder()
                .putData("event", "Fire")
                .putData("title", "Fire Detected")
                .putData("desc", desc)
                .putData("time", time)
                .putData("date", date)
                .setTopic("Firetest")
                .setAndroidConfig(config)
                .build();

        String response = FirebaseMessaging.getInstance().send(message);
        try {
            u.PersonCount.get(date).add("FIRE DETECTED: "+time);
        } catch (Exception NullPointerException) {
            u.PersonCount.put(date,new ArrayList <String>());
            u.PersonCount.get(date).add("FIRE DETECTED: "+time);
        }
        ApiFuture<WriteResult> result = db.collection("Users").document(id).set(u,SetOptions.merge());
        System.out.println("Fire Alarm Sent");

    }

    public void run() {
        // try {
        //     initialiseFirebase();
        // } catch (IOException e1) {
        //     // TODO Auto-generated catch block
        //     e1.printStackTrace();
        // }

        String date = currentDateFormatter.format(currentDate);

        var pi4j = Pi4J.newAutoContext();

        var motionConfig = DigitalInput.newConfigBuilder(pi4j).address(MOTION_SENSOR_PIN).pull(PullResistance.PULL_DOWN)
                .build();
        var fireConfig = DigitalInput.newConfigBuilder(pi4j).address(FIRE_SENSOR_PIN).pull(PullResistance.PULL_DOWN)
                .build();
        DigitalInputProvider digitalInputProvider = pi4j.provider("pigpio-digital-input");

        var motioninput = digitalInputProvider.create(motionConfig);
        var fireinput = digitalInputProvider.create(fireConfig);
        try {
            currentDailyCount = u.DailyCount.get(date);
        } catch (NullPointerException e2) {
            u.DailyCount.put(date, 0);
        }

        System.out.println("Running");

        motioninput.addListener(e -> {

            if (e.state() == DigitalState.HIGH) {
                // Night test
                // if (!(LocalTime.now().isAfter(openingTime) &&
                // LocalTime.now().isBefore(closingTime))) {
                // currentDailyCount++;
                // u.PersonCount.put(date, currentDailyCount);
                // System.out.println("Person detected " + currentDailyCount + "th time");

                // } else {
                // try {
                // sendMotionMessage();
                // } catch (FirebaseMessagingException e1) {
                // // TODO Auto-generated catch block
                // e1.printStackTrace();
                // }
                // }
                if (LocalTime.now().isAfter(openingTime) && LocalTime.now().isBefore(closingTime)) {
                    currentDailyCount++;
                    String time = LocalTime.now().toString();
                    u.DailyCount.put(date, currentDailyCount);
                    try {
                        u.PersonCount.get(date).add("New Customer: "+time);
                    } catch (Exception NullPointerException) {
                        u.PersonCount.put(date,new ArrayList <String>());
                        u.PersonCount.get(date).add("New Customer: "+time);
                    }
                    
                    ApiFuture<WriteResult> result = db.collection("Users").document(id).set(u,SetOptions.merge());
                    System.out.println("Person detected " + currentDailyCount + "th time");
                    try {
                        updateApp(Integer.toString(currentDailyCount));
                    } catch (FirebaseMessagingException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }

                } else {
                    try {
                        sendMotionMessage();
                    } catch (FirebaseMessagingException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                }

            }
        });

        fireinput.addListener(e -> {

            if (e.state() == DigitalState.HIGH) {
                // Night test
                // if (!(LocalTime.now().isAfter(openingTime) &&
                // LocalTime.now().isBefore(closingTime))) {
                // currentDailyCount++;
                // u.PersonCount.put(date, currentDailyCount);
                // System.out.println("Person detected " + currentDailyCount + "th time");

                // } else {
                // try {
                // sendMotionMessage();
                // } catch (FirebaseMessagingException e1) {
                // // TODO Auto-generated catch block
                // e1.printStackTrace();
                // }
                // }
                try {
                    sendFireMessage();
                } catch (FirebaseMessagingException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }

        });

        while (!interrupted()) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e1) {
                // TODO Auto-generated catch block
                System.out.println("IOT System Stopping");

            }
        }
        pi4j.shutdown();
        System.out.println("IOT Embedded System Stopped");

    }

    void updateApp(String p) throws FirebaseMessagingException {
        AndroidConfig config = AndroidConfig.builder().setPriority(AndroidConfig.Priority.HIGH).build();
        Message message = Message.builder()
                .putData("personCount", p)
                .setTopic("Firetest")
                .setAndroidConfig(config)
                .build();
        String response = FirebaseMessaging.getInstance().send(message);
    }
}
