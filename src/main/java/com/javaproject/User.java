package com.javaproject;

import java.io.Serializable;
import java.util.Map;
import java.util.HashMap;

public class User implements Serializable{
    String username;
    String password;
    int authorizationLevel;
    Map <String, Integer> PersonCount = new HashMap<String, Integer>();

    User(String username, String password, int authorizationLevel) {
        this.username = username;
        this.password = password;
        this.authorizationLevel = authorizationLevel;
    }

    void addcount(String date, int count) {
        this.PersonCount.put(date, count);
    }

    void viewcount() {
        for(Map.Entry<String,Integer> entry : PersonCount.entrySet()) {
            System.out.println(entry.getKey()+" "+entry.getValue());
        }
    }
}
