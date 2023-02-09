package com.javaproject;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import com.password4j.Hash;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class User implements Serializable {
    public String username;
    public boolean admin;
    public Map<String, Integer> DailyCount = new HashMap<String, Integer>();
    public Map<String, ArrayList<String>> PersonCount = new HashMap<String, ArrayList<String>>();
    public String hash;

    User(String username, String hash, boolean admin) {
        this.username = username;
        this.hash = hash;
        this.admin = admin;
        DateTimeFormatter currentDateFormatter = DateTimeFormatter.ofPattern("dd/MM/YYYY");
        LocalDate currentDate = LocalDate.now();
        this.DailyCount.put(currentDate.toString(), 0);
        this.PersonCount.put(currentDate.toString(), null);
    }

    User(String username, boolean admin, Map<String, Integer> DailyCount, Map<String, ArrayList<String>> PersonCount) {
        this.username = username;
        this.hash = null;
        this.admin = admin;
        this.DailyCount.putAll(DailyCount);
        this.PersonCount.putAll(PersonCount);
    }

    void addcount(String date, int count) {
        int existingDailyCount;
        try {
            existingDailyCount = this.DailyCount.get(date);
        } catch (Exception e) {
            existingDailyCount = 0;
        }

        Random random = new Random();
        this.DailyCount.put(date, count + existingDailyCount);
        LocalTime time = LocalTime.now();
        LocalTime past = time.minusSeconds(count);
        for (int i = 0; i < count; i++) {
            try {
                this.PersonCount.get(date).add("New Customer: " + past);
            } catch (Exception NullPointerException) {
                this.PersonCount.put(date, new ArrayList<String>());
                this.PersonCount.get(date).add("New Customer: " + past);
            }
            past.plusSeconds(1);
            past.plusNanos(random.nextInt(50));
        }

    }

    void viewcount() {
        for (Map.Entry<String, Integer> entry : DailyCount.entrySet()) {
            System.out.println(entry.getKey() + " " + entry.getValue());
        }
    }
}
