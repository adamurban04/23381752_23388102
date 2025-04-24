package org.controller;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ServerGUI extends Application {
    private static final int WIDTH = 700;
    private static final int HEIGHT = 500;
    private Stage primaryStage;
    private static TextArea logArea;
    private static ServerApp serverApp;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setWidth(WIDTH);
        primaryStage.setHeight(HEIGHT);
        primaryStage.setResizable(false);

        primaryStage.setOnCloseRequest(event -> {
            if (serverApp != null) {
                ServerApp.stopServer();
            }
            System.exit(0);
        });

        showMainScreen();

    }

    private void showMainScreen() {
        logArea = new TextArea();
        logArea.setEditable(false);

        VBox root = new VBox(logArea);
        Scene scene = new Scene(root, WIDTH, HEIGHT);

        primaryStage.setTitle("Timetable Server");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Start the server in a new thread
        new Thread(() -> {
            serverApp = new ServerApp();
            serverApp.startServer();
        }).start();
    }

    public static void log(String message) {
        javafx.application.Platform.runLater(() ->
                logArea.appendText(message + "\n")
        );
    }

    public static void main(String[] args) {
        launch(args);
    }
}