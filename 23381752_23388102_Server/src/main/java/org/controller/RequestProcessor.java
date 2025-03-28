package org.controller;

import org.exceptions.IncorrectActionException;
import org.model.Timetable;

import java.io.IOException;

public class RequestProcessor {
    public static String processRequest(String request, Timetable timetable) throws IncorrectActionException {
        String[] parts = request.split("\\$");    // split parts with $ separator

        if (parts.length < 2) {
            return "Error: Invalid request format.";
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
                default:
                    throw new IncorrectActionException("Action '" + action + "' is not implemented.");
            }
        } catch (IncorrectActionException e) {
            return "Server Error: " + e.getMessage();
        } catch (IOException e) {
            return "File Error: " + e.getMessage();
        }
    }
}