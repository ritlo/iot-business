package com.javaproject;

import java.io.Serializable;
import java.util.Map;

import com.password4j.Hash;

import java.util.ArrayList;
import java.util.HashMap;

public class User implements Serializable{
    String username;
    int authorizationLevel;
    Map <String, Integer> DailyCount = new HashMap<String, Integer>();
    Map <String, ArrayList<String>> PersonCount = new HashMap<String, ArrayList<String>>();
    Hash hash;
    
    User(String username, Hash hash) {
        this.username = username;
        this.hash = hash;
    }

    void addcount(String date, int count) {
        this.DailyCount.put(date, count);
    }

    void viewcount() {
        for(Map.Entry<String,Integer> entry : DailyCount.entrySet()) {
            System.out.println(entry.getKey()+" "+entry.getValue());
        }
    }
}
