package org.controller;

import org.exceptions.IncorrectActionException;
import org.model.Timetable;
import java.io.*;
import java.net.*;
import java.util.*;

public class ServerApp {
    private static ServerSocket serverSocket;
    private static volatile boolean isRunning = false;
    private static final Map<Socket, String> connections = new HashMap<>();
    private static int connectionCount = 0;

    public static void startServer() {
        if (isRunning) return;
        isRunning = true;
        connectionCount = 0; // Reset counter on restart

        new Thread(() -> {
            try (ServerSocket ss = new ServerSocket(1234)) {
                serverSocket = ss;
                ServerGUI.log("Server started on port 1234");
                ServerGUI.updateServerStatus(true);

                while (isRunning) {
                    try {
                        Socket clientSocket = serverSocket.accept();
                        String ip = clientSocket.getInetAddress().getHostAddress();

                        synchronized (connections) {
                            connectionCount++;
                            String connId = ip + "-" + connectionCount;
                            connections.put(clientSocket, connId);
                            ServerGUI.log("[" + connId + "] Connected");
                        }

                        new Thread(() -> handleClient(clientSocket)).start();
                    } catch (SocketException e) {
                        if (isRunning) {
                            ServerGUI.log("Server socket error: " + e.getMessage());
                        }
                    } catch (IOException e) {
                        ServerGUI.log("Server error: " + e.getMessage());
                    }
                }
            } catch (IOException e) {
                ServerGUI.log("Failed to start server: " + e.getMessage());
            } finally {
                ServerGUI.updateServerStatus(false);
            }
        }).start();
    }

    private static void handleClient(Socket socket) {
        String connId;
        synchronized (connections) {
            connId = connections.get(socket);
        }

        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            String request;
            while (isRunning && (request = in.readLine()) != null) {
                String response = RequestProcessor.processRequest(request, new Timetable());
                out.println(response);
            }
        } catch (IOException | IncorrectActionException e) {
            if (isRunning) {
                ServerGUI.log("[" + connId + "] Error: " + e.getMessage());
            }
        } finally {
            try {
                socket.close();
                if (isRunning) {
                    ServerGUI.log("[" + connId + "] Disconnected");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            synchronized (connections) {
                connections.remove(socket);
            }
        }
    }

    public static void stopServer() {
        if (!isRunning) return;
        isRunning = false;

        synchronized (connections) {
            connections.forEach((socket, connId) -> {
                try {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } catch (RuntimeException e) {
                    throw new RuntimeException(e);
                }
            });
            connections.clear();
        }

        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
                ServerGUI.log("Server stopped");
            }
        } catch (IOException e) {
            ServerGUI.log("Server close error: " + e.getMessage());
        }
    }
}