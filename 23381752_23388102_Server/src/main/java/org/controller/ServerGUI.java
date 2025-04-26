package org.controller;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class ServerGUI extends Application {
    private static TextArea logArea;
    private static Button startBtn;
    private static Button stopBtn;
    private static Label statusLabel;

    @Override
    public void start(Stage stage) {
        // Title with nice styling
        Label title = new Label("SERVER CONTROL PANEL");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        title.setTextFill(Color.web("#2C3E50"));

        // Status indicator
        statusLabel = new Label("● Stopped");
        statusLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        statusLabel.setTextFill(Color.web("#E74C3C"));

        // Log area with subtle styling
        logArea = new TextArea();
        logArea.setEditable(false);
        logArea.setWrapText(true);
        logArea.setStyle("-fx-font-family: 'Consolas'; -fx-font-size: 14px; " +
                "-fx-control-inner-background: #FDFDFD; " +
                "-fx-border-color: #BDC3C7;");

        ScrollPane logScroll = new ScrollPane(logArea);
        logScroll.setFitToWidth(true);
        logScroll.setStyle("-fx-background: #FDFDFD;");

        // Buttons with consistent styling
        startBtn = createStyledButton("START SERVER", "#2ECC71");
        stopBtn = createStyledButton("STOP SERVER", "#E74C3C");
        stopBtn.setDisable(true);

        startBtn.setOnAction(e -> {
            startBtn.setDisable(true);
            stopBtn.setDisable(false);
            ServerApp.startServer();
        });

        stopBtn.setOnAction(e -> {
            ServerApp.stopServer();
            startBtn.setDisable(false);
            stopBtn.setDisable(true);
        });

        // Button container
        HBox buttonBox = new HBox(20, startBtn, stopBtn);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(15, 0, 15, 0));

        // Main layout
        VBox root = new VBox(15, title, statusLabel, new Separator(), logScroll, buttonBox);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #ECF0F1;");

        Scene scene = new Scene(root, 750, 550);
        stage.setScene(scene);
        stage.setTitle("Timetable Server Manager");
        stage.setOnCloseRequest(e -> {
            ServerApp.stopServer();
            Platform.exit();
        });
        stage.show();
    }

    private Button createStyledButton(String text, String color) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: " + color + "; " +
                "-fx-text-fill: white; -fx-font-weight: bold; " +
                "-fx-font-size: 14px; -fx-padding: 10 25; " +
                "-fx-background-radius: 5px; -fx-border-radius: 5px;");

        btn.setOnMouseEntered(e -> btn.setStyle(
                "-fx-background-color: derive(" + color + ", -10%); " +
                        "-fx-text-fill: white; -fx-font-weight: bold; " +
                        "-fx-font-size: 14px; -fx-padding: 10 25; " +
                        "-fx-background-radius: 5px; -fx-border-radius: 5px;"
        ));

        btn.setOnMouseExited(e -> btn.setStyle(
                "-fx-background-color: " + color + "; " +
                        "-fx-text-fill: white; -fx-font-weight: bold; " +
                        "-fx-font-size: 14px; -fx-padding: 10 25; " +
                        "-fx-background-radius: 5px; -fx-border-radius: 5px;"
        ));

        return btn;
    }

    public static void log(String message) {
        Platform.runLater(() -> {
            logArea.appendText("[" + java.time.LocalTime.now().withNano(0) + "] " + message + "\n");
            logArea.setScrollTop(Double.MAX_VALUE);
        });
    }

    public static void updateServerStatus(boolean isRunning) {
        Platform.runLater(() -> {
            if (isRunning) {
                statusLabel.setText("Running");
                statusLabel.setTextFill(Color.web("#2ECC71"));
            } else {
                statusLabel.setText("Stopped");
                statusLabel.setTextFill(Color.web("#E74C3C"));
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}