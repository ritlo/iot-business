package com.javaproject;

import java.io.Console;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.SetOptions;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firestore.v1.Document;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.password4j.Hash;
import com.password4j.Password;

public class UserDatabase implements java.io.Serializable {
    ArrayList<User> users = new ArrayList<User>();
    Boolean runlogin = true;
    Boolean runloggedin = false;
    Firestore db = FirestoreClient.getFirestore();
    Console co = System.console();
    Scanner sc = new Scanner(System.in);
    SecureRandom random = new SecureRandom();
    Map<String, Object> data = new HashMap<String, Object>();
    public String id = "";

    void register() {
        System.out.println("Enter Username: ");
        String user = co.readLine();
        System.out.println("Enter Password: ");
        String password = String.valueOf(co.readPassword());
        System.out.println("Is User an Admin? (true/false)");
        boolean admin = sc.nextBoolean();

        Hash hash = Password.hash(password).addRandomSalt(5).withScrypt();
        User u = new User(user, hash.toString(), admin);
        ApiFuture<WriteResult> result = db.collection("Users").document().set(u);
        try {
            System.out.println("New User registered " + result.get().getUpdateTime());
        } catch (ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    void delete(String deluser) {
        CollectionReference users = db.collection("Users");
        Query query = users.whereEqualTo("username", deluser);
        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        try {
            for (DocumentSnapshot document : querySnapshot.get().getDocuments()) {
                ApiFuture<WriteResult> writeResult = db.collection("Users").document(document.getId()).delete();
                System.out.println("User Deleted");
            }
        } catch (InterruptedException | ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.out.println("User not found");
        }
    }

    void view() {
        CollectionReference users = db.collection("Users");
        ApiFuture<QuerySnapshot> future = db.collection("Users").get();
        List<QueryDocumentSnapshot> documents;
        try {
            documents = future.get().getDocuments();
            for (DocumentSnapshot document : documents) {
                System.out.println(document.get("username"));
            }
        } catch (InterruptedException | ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    void loginscreen() {
        System.out.println("Press Q to quit");
        int wrongcount = 0;
        while (runlogin) {
            System.out.println("Enter Username");
            String loginUser = co.readLine();
            CollectionReference users = db.collection("Users");
            Query query = users.whereEqualTo("username", loginUser);
            ApiFuture<QuerySnapshot> querySnapshot = query.get();
            if (loginUser.toLowerCase().contentEquals("q")) {
                runlogin = false;
                runloggedin = false;
                break;
            } else if (query == null) {
                System.out.println("Try again");
            } else {
                try {
                    for (DocumentSnapshot document : querySnapshot.get().getDocuments()) {
                        System.out.println("Enter Password");
                        String loginPassword = String.valueOf(co.readPassword());
                        id = document.getId();
                        System.out.println(data);
                        String hash = document.getData().get("hash").toString();
                        boolean verify = Password.check(loginPassword, hash).withScrypt();
                        if (verify) {
                            data = document.getData();
                            id = document.getId();
                            System.out.println("User verified");
                            System.out.println("Welcome " + data.get("username").toString());
                            String username = data.get("username").toString();
                            Boolean admin = (boolean) data.get("admin");
                            Map DailyCount = (Map) data.get("DailyCount");
                            Map PersonCount = (Map) data.get("PersonCount");
                            User u = new User(username, admin, DailyCount, PersonCount);
                            if ((boolean) data.get("admin") == true) {
                                // databaseAdmin(DocumentSnapshot document);
                                runloggedin = true;
                                runlogin = false;
                                databaseAdmin(u);
                            } else {
                                runloggedin = true;
                                runlogin = false;
                                databaseRestaurant(u);
                            }
                        }

                    }
                } catch (InterruptedException | ExecutionException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                // while (iterator.hasNext()) {
                // User currentUser = iterator.next();
                // if (currentUser.username.contentEquals(loginUser)
                // && currentUser.password.contentEquals(loginPassword)
                // && currentUser.authorizationLevel == 1) {
                // System.out.println("Hello " + currentUser.username);
                // runloggedin = true;
                // runlogin = false;
                // databaseRestaurant(currentUser);
                // break;
                // } else if (currentUser.username.contentEquals(loginUser)
                // && currentUser.password.contentEquals(loginPassword)
                // && currentUser.authorizationLevel > 1) {
                // System.out.println("Hello, " + currentUser.username);
                // runloggedin = true;
                // runlogin = false;
                // databaseAdmin(currentUser);
                // break;
                // }
                // }
                if (runlogin)
                    System.out.println("Try Again");
            }
        }
    }

    void databaseRestaurant(User U) {
        IoTEmbeddedSystem pc = new IoTEmbeddedSystem(U, id);
        pc.start();
        while (runloggedin) {
            System.out.println("Enter option");
            System.out.println("1. View Customer Data");
            System.out.println("2. Add Manual Data");
            System.out.println("3. Quit");

            int choice = sc.nextInt();
            sc.nextLine();
            switch (choice) {
                case 1:
                    System.out.println("Viewing Restaurant Info");
                    U.viewcount();
                    break;
                case 2:
                    System.out.println("Enter Date in DD/MM/YYYY");
                    String date = sc.nextLine();
                    System.out.println("Enter Person Count");
                    int count = sc.nextInt();
                    sc.nextLine();
                case 3:
                    runloggedin = false;
                    runlogin = false;
                    pc.stop();
                    break;

            }
        }
    }

    void databaseAdmin(User U) {
        while (runloggedin) {
            System.out.println("Enter option");
            System.out.println("1. Register User");
            System.out.println("2. Delete User");
            System.out.println("3. View Users");
            System.out.println("4. View All Counts");
            System.out.println("5. Add Manual Count");
            System.out.println("6. Quit");
            int choice = sc.nextInt();
            sc.nextLine();
            switch (choice) {
                case 1:
                    register();
                    break;
                case 2:
                    System.out.println("Enter username to be deleted:");
                    String del = sc.nextLine();
                    delete(del);
                    break;
                case 3:
                    view();
                    break;
                case 4:
                    Iterator<User> iterator = users.iterator();
                    while (iterator.hasNext()) {
                        User u = iterator.next();
                        if (u.admin) {
                            System.out.println(u.username);
                            u.viewcount();
                        }
                    }
                    break;
                case 5:
                    System.out.println("Enter restaurant Username");
                    String userinput = sc.nextLine();
                    CollectionReference users = db.collection("Users");
                    Query query = users.whereEqualTo("username", userinput);
                    ApiFuture<QuerySnapshot> querySnapshot = query.get();
                    try {
                        for (DocumentSnapshot document : querySnapshot.get().getDocuments()) {
                            data = document.getData();
                            System.out.println("Enter date in YYYY-MM-DD");
                            String date = sc.nextLine();
                            System.out.println("Enter Count");
                            int count = sc.nextInt();
                            String username = data.get("username").toString();
                            Boolean admin = (boolean) data.get("admin");
                            Map DailyCount = (Map) data.get("DailyCount");
                            Map PersonCount = (Map) data.get("PersonCount");
                            User ud = new User(username, admin, DailyCount, PersonCount);
                            ud.addcount(date, count);
                            ApiFuture<WriteResult> result = db.collection("Users").document(document.getId()).set(ud,SetOptions.merge());
                            System.out.println("Count Added");
                        }  
                    }   
                    catch (InterruptedException | ExecutionException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        System.out.println("User not found");
                    }
                    break;
                case 6:
                    runloggedin = false;
                    runlogin = false;
                    break;

            }

    }}

    public void userdatabase() throws ClassNotFoundException, IOException {
        // readdatabase();
        // User admin = new User("admin", "password", 2);
        // boolean adminExists = false;
        // Iterator <User> it = users.iterator();
        // while(it.hasNext()){
        // User u = it.next();
        // if(u.username.contentEquals("admin")) adminExists = true;
        // }
        // if(!adminExists){
        // System.out.println("Adding Admin User to Database");
        // users.add(admin);
        // }
        // register();
        loginscreen();
        // writedatabase();

    }
}
