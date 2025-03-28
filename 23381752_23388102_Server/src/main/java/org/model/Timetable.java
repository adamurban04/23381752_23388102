package org.model;

import org.exceptions.IncorrectActionException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.Iterator;
import java.io.FileWriter;
import java.io.IOException;


public class Timetable {
    // ArrayList of ArrayLists, each holding lectures for a specific day
    private final ArrayList<ArrayList<Lecture>> weeklyTimetable; // 5 slots for Mon-Fri

    public Timetable() {
        weeklyTimetable = new ArrayList<>(5);
        for (int i = 0; i < 5; i++) {
            weeklyTimetable.add(new ArrayList<>());  // create empty lists for each day
        }
    }

    // Method to add a lecture to a specific day
    public synchronized String addLecture(String details) throws IncorrectActionException {
        String[] parts = details.split(",");
        if (parts.length < 4) throw new IncorrectActionException("Invalid lecture format. Expected: module,date,time,room");

        try {
            String module = parts[0].trim();
            LocalDate date = LocalDate.parse(parts[1].trim());
            LocalTime time = LocalTime.parse(parts[2].trim());
            String room = parts[3].trim();

            if (isTimeSlotOccupied(date, time, room)) {
                throw new IncorrectActionException("ERROR: Time slot occupied for " + room + " at " + time);
            }
            if (isTimeSlotFree(date, time)) {
                throw new IncorrectActionException("ERROR: Time slot occupied by another lecture");
            }

            weeklyTimetable.get(date.getDayOfWeek().getValue() - 1).add(new Lecture(module, date, time, room)); //stores in timetable
            return "Lecture added.";
        } catch (Exception e) {
            throw new IncorrectActionException(e.getMessage());
        }
    }

    public synchronized String removeLecture(String details) throws IncorrectActionException {
        String[] parts = details.split(",");
        if (parts.length < 2) throw new IncorrectActionException("Invalid lecture format. Expected: module,date,time");

        try {
            String module = parts[0].trim();
            LocalDate date = LocalDate.parse(parts[1].trim());
            LocalTime time = LocalTime.parse(parts[2].trim());
            String room = parts[3].trim();

            int dayIndex = date.getDayOfWeek().getValue() - 1;

            Iterator<Lecture> iterator = weeklyTimetable.get(dayIndex).iterator();
            while (iterator.hasNext()) {
                Lecture lecture = iterator.next();
                if (lecture.getModule().equalsIgnoreCase(module) && lecture.getDate().equals(date) && lecture.getTime().equals(time)) {
                    iterator.remove();
                    return "Lecture removed.";
                }
            }
            return "ERROR: Lecture not found.";
        } catch (Exception e) {
            throw new IncorrectActionException("Invalid date format.");
        }
    }

    public synchronized String getSchedule() {
        StringBuilder schedule = new StringBuilder("Scheduled Lectures|");
        String[] weekdays = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};

        //each weekday- display the lectures for that day
        for (int i = 0; i < 5; i++) {
            // check if the day has lectures scheduled
            schedule.append(weekdays[i]);

            if (weeklyTimetable.get(i).isEmpty()) {
            }
            else {
                for (Lecture lecture : weeklyTimetable.get(i)) {
                    schedule.append(lecture.toString()); // format the lecture properly
                }
            }
            schedule.append("|");
        }

        System.out.println(schedule);

        return schedule.toString();
    }



    private boolean isTimeSlotOccupied(LocalDate date, LocalTime time, String room) {
        int dayIndex = date.getDayOfWeek().getValue() - 1;
        for (Lecture lecture : weeklyTimetable.get(dayIndex)) {
            if (lecture.getTime().equals(time) && lecture.getRoom().equals(room)) {
                return true;
            }
        }
        return false;
    }

    private boolean isTimeSlotFree(LocalDate date, LocalTime time) {
        int dayIndex = date.getDayOfWeek().getValue() - 1;
        for (Lecture lecture : weeklyTimetable.get(dayIndex)) {
            if (lecture.getTime().equals(time)) {
                return true;
            }
        }

        return false;
    }

    public synchronized String exportToCSV(String filePath) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            //header
            writer.write("Module,Date,Time,Room\n");

            //timetable data
            for (ArrayList<Lecture> dayLectures : weeklyTimetable) {
                for (Lecture lecture : dayLectures) {
                    writer.write(
                            lecture.getModule() + "," +
                                    lecture.getDate() + "," +
                                    lecture.getTime() + "," +
                                    lecture.getRoom() + "\n"
                    );
                }
            }
        }
        return "Timetable exported successfully to " + filePath;
    }

    public synchronized String importFromCSV(String filePath) throws IOException, IncorrectActionException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean isHeader = true;

            // read  lectures
            while ((line = reader.readLine()) != null) {
                if (isHeader) {
                    isHeader = false; // skip header
                    continue;
                }

                String[] parts = line.split(",");
                if (parts.length != 4) {
                    throw new IncorrectActionException("Invalid CSV format. Expected: Module,Date,Time,Room");
                }

                String module = parts[0].trim();
                LocalDate date = LocalDate.parse(parts[1].trim());
                LocalTime time = LocalTime.parse(parts[2].trim());
                String room = parts[3].trim();

                // add lecture to timetable
                weeklyTimetable.get(date.getDayOfWeek().getValue() - 1).add(new Lecture(module, date, time, room));
            }
        }
        return "Timetable imported successfully from " + filePath;
    }





}
