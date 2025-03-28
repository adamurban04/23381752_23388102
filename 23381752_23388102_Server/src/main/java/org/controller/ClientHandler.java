package org.controller;

import org.exceptions.IncorrectActionException;
import org.model.Timetable;

import java.net.Socket;
import java.io.*;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private final Timetable sharedTimetable;

    public ClientHandler(Socket socket, Timetable timetable) {
        this.socket = socket;
        this.sharedTimetable = timetable;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            String request;
            while ((request = in.readLine()) != null) {
                ServerGUI.log("Received Message: " + request);

                // Process request
                String response;
                response = RequestProcessor.processRequest(request, sharedTimetable);

                out.println(response);
                if ("TERMINATE".equals(response)) {
                    ServerGUI.log("Closing client connection...");
                    break;
                }
            }
        } catch (IOException e) {
            ServerGUI.log("IOException: " + e.getMessage());
        } catch (IncorrectActionException e) {
            ServerGUI.log("IncorrectActionException: " + e.getMessage());
            throw new RuntimeException(e);
        } finally {
            try {
                socket.close();
                ServerGUI.log("Client socket closed.");
            } catch (IOException e) {
                ServerGUI.log("Error closing socket: " + e.getMessage());
            }
        }
    }
}
