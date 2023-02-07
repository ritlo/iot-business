package com.javaproject;

import java.io.Serializable;
import java.util.Map;

import com.password4j.Hash;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class User implements Serializable{
    public String username;
    public int authorizationLevel;
    public Map <String, Integer> DailyCount = new HashMap<String, Integer>();
    public Map <String, ArrayList<String>> PersonCount = new HashMap<String, ArrayList<String>>();
    public List <Hash> hash;
    
    User(String username, List<Hash> hash) {
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
