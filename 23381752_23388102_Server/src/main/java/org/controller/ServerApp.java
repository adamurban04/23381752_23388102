package org.controller;

import org.model.Timetable;
import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

// Server side for TCP Connection between clients and the server

public class ServerApp {
    private static final int PORT = 1234;
    private static final int MAX_CLIENTS = 10;
    private static Timetable timetable = new Timetable();
    private static ExecutorService executorService;

    public void startServer() {
        executorService = Executors.newFixedThreadPool(MAX_CLIENTS);

        try (ServerSocket serverSocket = new ServerSocket(PORT)){
            ServerGUI.log("Server is running on port: " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept(); // accept client connection
                ServerGUI.log("Client connected: " + clientSocket.getInetAddress());
                executorService.execute(new ClientHandler(clientSocket, timetable));
            }

        } catch (IOException e) {
            ServerGUI.log("Server error: " + e.getMessage());
        }
    }

    public static void stopServer() {
        if (executorService != null) {
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
            }
            ServerGUI.log("Server stopped");
        }
    }
}
