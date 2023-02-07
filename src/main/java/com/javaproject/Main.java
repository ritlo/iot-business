package com.javaproject;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ExecutionException;

public class Main {
    public static void main(String[] args) throws IOException, InvocationTargetException, ClassNotFoundException {
        System.out.println("Business Management System 2k19/EC/150 2k19/EC/190 2k19/EC/191");
        // PersonCounter p = new PersonCounter();
        // // p.initialiseSensor();
        init.initialiseFirebase();
        try {
            firestoretest.exec();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        

        // UserDatabase userd = new UserDatabase();
        // userd.userdatabase();

        
    }
}
