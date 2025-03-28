package org.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientConnection {
    private static ClientConnection instance;
    private Socket socket;
    private PrintWriter out;
    private Scanner in;

    private ClientConnection() throws IOException {
        socket = new Socket("localhost", 1234);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new Scanner(socket.getInputStream());
    }

    public static ClientConnection getInstance() throws IOException {
        if (instance == null) {
            System.out.println("Creating a new connection to the server...");
            instance = new ClientConnection();
        }
        return instance;
    }

    public String sendRequest(String request) {
        out.println(request); //send request
        return in.nextLine(); //read response
    }

    public void closeConnection() throws IOException {
        if (socket != null) {
            socket.close();
            instance = null; //initialise to null to allow reconnecting
        }
    }
}
