package org.model;

import org.controller.ServerGUI;
import org.exceptions.IncorrectActionException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.Iterator;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.ForkJoinPool;


import javafx.concurrent.Task;



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
        if (parts.length < 4)
            throw new IncorrectActionException("Invalid lecture format. Expected: module,date,time,room");

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
                    ServerGUI.log("Lecture for "+ module +" removed");
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
            } else {
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

    public String clearTimetable() {
        for (ArrayList<Lecture> dayLectures : weeklyTimetable) {
            dayLectures.clear();
        }
        return "Timetable cleared!";
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

    public synchronized void addLectureLite(LocalDate date, LocalTime time, String room, String module) {
        weeklyTimetable.get(date.getDayOfWeek().getValue() - 1)
                .add(new Lecture(module, date, time, room));
    }

    public synchronized void removeLectureLite(LocalDate date, LocalTime time, String room, String module) {
        int dayIndex = date.getDayOfWeek().getValue() - 1;

        Iterator<Lecture> iterator = weeklyTimetable.get(dayIndex).iterator();
        while (iterator.hasNext()) {
            Lecture lecture = iterator.next();
            if (lecture.getModule().equalsIgnoreCase(module) &&
                    lecture.getDate().equals(date) &&
                    lecture.getTime().equals(time) &&
                    lecture.getRoom().equalsIgnoreCase(room)) {
                iterator.remove();
                return;
            }
        }
    }


    public void rescheduleLecturesToEarlierTimes() {
        ForkJoinPool.commonPool().invoke(new EarlyLecturesForkJoin(this, 0, 5));
        ServerGUI.log("Lectures moved to earlier times!");
    }

    public synchronized List<Lecture> getLecturesForDay(int day) {
        return new ArrayList<>(weeklyTimetable.get(day));
    }


    public synchronized boolean hasLectureAt(int day, String room, LocalTime time) {
        for (Lecture lecture : weeklyTimetable.get(day)) {
            if (lecture.getTime().equals(time) && lecture.getRoom().equalsIgnoreCase(room)) {
                return true;
            }
        }
        return false;
    }

    public void rescheduleDay(int day) {
        List<Lecture> dayLectures;

        synchronized (this) {
            dayLectures = new ArrayList<>(getLecturesForDay(day));
        }

        // Sort lectures by original time
        dayLectures.sort(Comparator.comparing(Lecture::getTime));

        for (Lecture lecture : dayLectures) {
            List<LocalTime> preferredTimes = new ArrayList<>();

            // Build preferred times dynamically from 9:00 up to just before the lecture's current time
            int lectureHour = lecture.getTime().getHour();
            for (int hour = 9; hour < lectureHour; hour++) {
                preferredTimes.add(LocalTime.of(hour, 0));
            }

            synchronized (this) {
                for (LocalTime newTime : preferredTimes) {
                    if (!hasLectureAt(day, lecture.getRoom(), newTime)) {
                        // Remove lecture at old time
                        removeLectureLite(
                                lecture.getDate(),
                                lecture.getTime(),
                                lecture.getRoom(),
                                lecture.getModule()
                        );

                        // Add lecture at new earlier time
                        addLectureLite(
                                lecture.getDate(),
                                newTime,
                                lecture.getRoom(),
                                lecture.getModule()
                        );
                        break; // Once moved, break to next lecture
                    }
                }
            }
        }

    }

    public static class EarlyLecturesForkJoin extends RecursiveAction {
        private static final int SEQUENTIAL_THRESHOLD = 1; // handle one day at a time
        private final Timetable timetable;
        private final int startDay;
        private final int endDay;

        public EarlyLecturesForkJoin(Timetable timetable, int startDay, int endDay) {
            this.timetable = timetable;
            this.startDay = startDay;
            this.endDay = endDay;
        }


        @Override
        protected void compute() {
            if (endDay - startDay <= SEQUENTIAL_THRESHOLD) {
                timetable.rescheduleDay(startDay);
            } else {
                int mid = (startDay + endDay) / 2;
                EarlyLecturesForkJoin left = new EarlyLecturesForkJoin(timetable, startDay, mid);
                EarlyLecturesForkJoin right = new EarlyLecturesForkJoin(timetable, mid, endDay);
                invokeAll(left, right);
            }
        }
    }
}