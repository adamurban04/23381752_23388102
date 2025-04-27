package org.view;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.controller.ClientConnection;
import org.exceptions.IncorrectActionException;

import java.io.IOException;

public class OtherView {
    private final Stage stage;
    private final Runnable onBack;

    public OtherView(Stage stage, Runnable onBack) {
        this.stage = stage;
        this.onBack = onBack;
        showOtherScreen();
    }

    private void showOtherScreen() {
        Label titleLabel = new Label("Test Invalid Action");
        titleLabel.getStyleClass().add("title");

        Label instructionLabel = new Label("Click the button to send an invalid request:");
        instructionLabel.setStyle("-fx-font-size: 14px;");

        Button sendButton = createButton("Send Invalid Request", "#6f3deb");
        Button quitButton = createButton("STOP", "#E63946");
        Button backButton = createButton("Back", "#e27e3d");

        Label responseLabel = new Label();
        responseLabel.setWrapText(true);
        responseLabel.setStyle("-fx-padding: 10px; -fx-text-fill: #D32F2F; -fx-border-radius: 5px;");

        sendButton.setOnAction(e -> {
            try {
                responseLabel.setText(sendInvalidRequest());
            } catch (IncorrectActionException ex) {
                throw new RuntimeException(ex);
            }
        });

        quitButton.setOnAction(e -> {
            try {
                String response = ClientConnection.getInstance().sendRequest("STOP$Close");

                if ("TERMINATE".equals(response)) {
                    System.out.println("Client received TERMINATE, shutting down...");
                    ClientConnection.getInstance().closeConnection();
                    Platform.exit();
                    System.exit(0);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });


        backButton.setOnAction(e -> onBack.run());

        VBox layout = new VBox(15, titleLabel, instructionLabel, sendButton, quitButton, backButton, responseLabel);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout);
        ThemeManager.setScene(scene);
        ThemeManager.applyTheme();
        stage.setScene(scene);

    }

    private String sendInvalidRequest() throws IncorrectActionException {
        try {
            return ClientConnection.getInstance().sendRequest("InvalidCommand$test");
        } catch (IOException e) {
            throw new IncorrectActionException("Action not implemented.");
        }
    }

    private Button createButton(String text, String color) {
        Button button = new Button(text);
        button.getStyleClass().add("custom-button");
        button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10px 20px; -fx-border-radius: 5px;");
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: black; -fx-font-weight: bold; -fx-padding: 10px 20px; -fx-border-radius: 5px;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10px 20px; -fx-border-radius: 5px;"));
        return button;
    }
}
