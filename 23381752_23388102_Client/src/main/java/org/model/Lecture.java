package org.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;


public class Lecture {
    private static ArrayList<String> modules = new ArrayList<>();
    private String module;
    private LocalDate date;    // e.g., "2025-02-14"
    private LocalTime time;    // e.g., "10:00"
    private String room;

    public Lecture(String module, LocalDate date, LocalTime time, String room) {
        if (!modules.contains(module)) {  // If the module is new
            modules.add(module);  // Add the module to the list
            System.out.println(module + " added!");
            System.out.println("Current module count: " + modules.size());  // Debugging line
        }
        this.module = module;
        this.date = date;
        this.time = time;
        this.room = room;
    }
    public String getModule() {
        return module;
    }
    public LocalDate getDate() {
        return date;
    }
    public LocalTime getTime() {
        return time;
    }
    public String getRoom() {
        return room;
    }

    @Override
    public String toString() {
        return "{" +
                "module=" + module +
                ",date=" + date +
                ",time=" + time +
                ",room=" + room +
                '}';
    }
}