package com.javaproject;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import com.password4j.Hash;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



public class User implements Serializable{
    public String username;
    public boolean admin;
    public Map <String, Integer> DailyCount = new HashMap<String, Integer>();
    public Map <String, ArrayList<String>> PersonCount = new HashMap<String, ArrayList<String>>();
    public String hash;
    
    User(String username, String hash, boolean admin) {
        this.username = username;
        this.hash = hash;
        this.admin = admin;
        DateTimeFormatter currentDateFormatter = DateTimeFormatter.ofPattern("dd/MM/YYYY");
        LocalDate currentDate = LocalDate.now();
        this.DailyCount.put(currentDate.toString(), 0);
        this.PersonCount.put(currentDate.toString(),null);
    }

    User(String username, boolean admin, Map <String,Integer> DailyCount, Map <String, ArrayList<String>> PersonCount) {
        this.username = username;
        this.hash = null;
        this.admin = admin;
        this.DailyCount.putAll(DailyCount);
        this.PersonCount.putAll(PersonCount);
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
