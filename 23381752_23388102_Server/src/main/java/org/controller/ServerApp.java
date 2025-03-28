package org.controller;

import org.model.Timetable;
import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Server side for TCP Connection between clients and the server

public class ServerApp {
    private static final int PORT = 1234;
    private static final int MAX_CLIENTS = 10;
    private static Timetable timetable = new Timetable();

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(MAX_CLIENTS);

        try (ServerSocket serverSocket = new ServerSocket(PORT)){   // (try with resources - closes automatically)
            System.out.println("Server is running on port: " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept(); // accept client connection
                System.out.println("Client connected: " + clientSocket.getInetAddress());

                executorService.execute(new ClientHandler(clientSocket, timetable));
                //each client has instance of timetable (handles multiple clients, set max to 10)
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            executorService.shutdown();
        }
    }
}
