package com.javaproject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

public class UserDatabase implements java.io.Serializable {
    Scanner sc = new Scanner(System.in);
    ArrayList<User> users = new ArrayList<User>();
    Boolean runlogin = true;
    Boolean runloggedin = false;

    void register() {
        System.out.println("Enter Username: ");
        String user = sc.nextLine();
        System.out.println("Enter Password: ");
        String password = sc.nextLine();
        System.out.println("Authorization Level");
        int authorizationLevel = sc.nextInt();

        User u = new User(user, password, authorizationLevel);
        users.add(u);

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

    void writedatabase() {
        try {
            FileOutputStream out = new FileOutputStream("userdatabase.ser");
            ObjectOutputStream userswrite = new ObjectOutputStream(out);

            userswrite.writeObject(users);
            out.close();
            userswrite.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    void readdatabase() throws ClassNotFoundException, IOException {
        try {
            FileInputStream in = new FileInputStream("userdatabase.ser");
            ObjectInputStream usersread = new ObjectInputStream(in);

        users = (ArrayList<User>) usersread.readObject();
        in.close();
        usersread.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            System.out.println("File doesnt exist");
        }
        
    }

    void loginscreen() {
        System.out.println("Press Q to quit");
        int wrongcount = 0;
        while (runlogin) {
            System.out.println("Enter Username");
            String loginUser = sc.nextLine();
            System.out.println("Enter Password");
            String loginPassword = sc.nextLine();
            Iterator<User> iterator = users.iterator();
            if (loginUser.toLowerCase().contentEquals("q") || loginPassword.toLowerCase().contentEquals("q")) {
                runlogin = false;
                runloggedin = false;
                break;
            } else {
                while (iterator.hasNext()) {
                    User currentUser = iterator.next();
                    if (currentUser.username.contentEquals(loginUser)
                            && currentUser.password.contentEquals(loginPassword)
                            && currentUser.authorizationLevel == 1) {
                        System.out.println("Hello " + currentUser.username);
                        runloggedin = true;
                        runlogin = false;
                        databaseRestaurant(currentUser);
                        break;
                    } else if (currentUser.username.contentEquals(loginUser)
                            && currentUser.password.contentEquals(loginPassword)
                            && currentUser.authorizationLevel > 1) {
                        System.out.println("Hello, " + currentUser.username);
                        runloggedin = true;
                        runlogin = false;
                        databaseAdmin(currentUser);
                        break;
                    } 
                }
                if(runlogin) System.out.println("Try Again");
            }
        }
    }

    void databaseRestaurant(User U) {
        PersonCounter pc = new PersonCounter(U);
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
        readdatabase();
        User admin = new User("admin", "password", 2);
        boolean adminExists = false;
        Iterator <User> it = users.iterator();
        while(it.hasNext()){
            User u = it.next();
            if(u.username.contentEquals("admin")) adminExists = true;
        }
        if(!adminExists){
            System.out.println("Adding Admin User to Database");
            users.add(admin);
        }
        loginscreen();
        writedatabase();

    }
}
