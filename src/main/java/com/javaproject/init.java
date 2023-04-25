package com.javaproject;

import java.io.FileInputStream;
import java.io.IOException;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

public class init {
    public static void initialiseFirebase() throws IOException {
        FileInputStream serviceAccount = new FileInputStream("serviceaccountkey.json");

        // FirebaseOptions options = new FirebaseOptions.Builder()
        // .setCredentials(GoogleCredentials.fromStream(serviceAccount))
        // .setDatabaseUrl("https://fireproject-ee4f5.firebaseio.com").build();

        FirebaseOptions options = FirebaseOptions.builder().setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://fireproject-ee4f5.firebaseio.com").build();

        FirebaseApp.initializeApp(options);
    }

}