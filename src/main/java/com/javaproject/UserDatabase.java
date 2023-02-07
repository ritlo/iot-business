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
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firestore.v1.Document;
import com.google.cloud.firestore.Query;
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

    void register() {
        System.out.println("Enter Username: ");
        String user = co.readLine();
        System.out.println("Enter Password: ");
        String password = String.valueOf(co.readPassword());
        Hash hash = Password.hash(password).addRandomSalt().withScrypt();
        DocumentReference docRef = db.collection("Users").document();
        Map<String, Object> data = new HashMap<>();
        data.put("username", user);
        data.put("hashedpassword", hash.toString());
        ApiFuture<WriteResult> result = db.collection("Users").document().set(data);
        try {
            System.out.println("New User registered" + result.get().getUpdateTime());
        } catch (ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    void delete(String username) {
        boolean found = false;
        for (User u : users) {
            if (u.username.contentEquals(username)) {
                users.remove(u);
                found = true;
                break;
            }
        }
        if (found)
            System.out.println(username + " was removed");
        else
            System.out.println("user not found");

    }

    void view() {
        System.out.println(users.size() + " Registered Users");
        for (User u : users) {
            System.out.print(u.username);
            System.out.print(" " + u.authorizationLevel);
            System.out.println();
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
            } else if(query == null) {
                System.out.println("Try again");
            }
            else{
                try {
                    for (DocumentSnapshot document : querySnapshot.get().getDocuments()) {
                        System.out.println("Enter Password");
                        String loginPassword = sc.nextLine();
                        String hash = (String) document.getData().get("hashedpassword");
                        boolean verify = Password.check(loginPassword, hash).withScrypt();
                        if(verify){

                        }
                        

}
                } catch (InterruptedException | ExecutionException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                // while (iterator.hasNext()) {
                //     User currentUser = iterator.next();
                //     if (currentUser.username.contentEquals(loginUser)
                //             && currentUser.password.contentEquals(loginPassword)
                //             && currentUser.authorizationLevel == 1) {
                //         System.out.println("Hello " + currentUser.username);
                //         runloggedin = true;
                //         runlogin = false;
                //         databaseRestaurant(currentUser);
                //         break;
                //     } else if (currentUser.username.contentEquals(loginUser)
                //             && currentUser.password.contentEquals(loginPassword)
                //             && currentUser.authorizationLevel > 1) {
                //         System.out.println("Hello, " + currentUser.username);
                //         runloggedin = true;
                //         runlogin = false;
                //         databaseAdmin(currentUser);
                //         break;
                //     }
                // }
                if (runlogin)
                    System.out.println("Try Again");
            }
        }
    }

    void databaseRestaurant(User U) {
        IoTEmbeddedSystem pc = new IoTEmbeddedSystem(U);
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
                    U.addcount(date, count);
                    break;
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
                        if (u.authorizationLevel == 1) {
                            System.out.println(u.username);
                            u.viewcount();
                        }
                    }
                    break;
                case 5:
                    System.out.println("Enter restaurant Username");
                    String un = sc.nextLine();
                    Iterator<User> iterator2 = users.iterator();
                    while (iterator2.hasNext()) {
                        User u = iterator2.next();
                        if (u.username.contentEquals(un)) {
                            System.out.println("Enter date in DD/MM/YYYY");
                            String date = sc.nextLine();
                            System.out.println("Enter Count");
                            int count = sc.nextInt();
                            sc.nextLine();
                            u.addcount(date, count);
                        }
                    }
                    break;
                case 6:
                    runloggedin = false;
                    runlogin = false;
                    break;

            }
        }
    }

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
        loginscreen();
        // writedatabase();
        // register();

    }
}
