package org.controller;

import org.exceptions.IncorrectActionException;
import org.model.Timetable;

import java.io.IOException;

import javafx.concurrent.Task;

public class RequestProcessor {
    public static String processRequest(String request, Timetable timetable) throws IncorrectActionException {
        String[] parts = request.split("\\$");    // split parts with $ separator

        if (parts.length < 2) {
            return "ERROR: Invalid request format.";
        }
        String action = parts[0].trim();
        String details = parts[1].trim();
        try {
            switch (action) {
                case "STOP":
                    System.out.println("Server received STOP command.");
                    return "TERMINATE";
                case "Add":
                    return timetable.addLecture(details);
                case "Remove":
                    return timetable.removeLecture(details);
                case "Display":
                    return timetable.getSchedule();
                case "ExportCSV":
                    return timetable.exportToCSV(details);
                case "ImportCSV":
                    return timetable.importFromCSV(details);
                case "EarlyLectures":
                    offloadEarlyLectures(timetable);  // Call the refactored method
                    return "TimetableUpdated";
                default:
                    throw new IncorrectActionException("Action '" + action + "' is not implemented.");
            }
        } catch (IncorrectActionException e) {
            return e.getMessage();
        } catch (IOException e) {
            return "File Error: " + e.getMessage();
        }
    }
    private static void offloadEarlyLectures(Timetable timetable) {
        Task<Void> earlyLecturesTask = new Task<>() {
            @Override
            protected Void call() {
                timetable.rescheduleLecturesToEarlierTimes();
                return null;
            }
        };
        earlyLecturesTask.setOnSucceeded(event -> System.out.println("Early lectures rescheduling completed."));
        earlyLecturesTask.setOnFailed(event -> System.out.println("Early lectures rescheduling failed: " + earlyLecturesTask.getException()));
        new Thread(earlyLecturesTask).start();
    }

}
