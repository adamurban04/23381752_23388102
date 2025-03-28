package org.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.controller.ClientConnection;
import org.exceptions.IncorrectActionException;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

public class AddLectureView {
    private final Stage stage;
    private final Runnable onBack;
    private TextField moduleField, timeField, roomField;
    private DatePicker datePicker;
    private Label responseLabel;

    public AddLectureView(Stage stage, Runnable onBack) {
        this.stage = stage;
        this.onBack = onBack;
        showAddLectureScreen();
    }

    private void showAddLectureScreen() {
        // Title Label
        Label titleLabel = new Label("Add a New Lecture");
        titleLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #2C3E50;");

        moduleField = createTextField("e.g., CS1001");
        datePicker = new DatePicker();
        timeField = createTextField("HH:mm");
        roomField = createTextField("e.g., CG001");

        datePicker.setDayCellFactory(picker -> new javafx.scene.control.DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (date.isBefore(LocalDate.now()) || date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY) {
                    setDisable(true);
                    setStyle("-fx-background-color: #FFCCCC;"); // Light red for past dates and weekends
                }
            }
        });

        Button addLectureButton = createButton("Add Lecture", "#2C7A7B");  // Deep teal
        Button backButton = createButton("Back", "#e27e3d");  // Soft red-orange

        responseLabel = new Label();
        responseLabel.setWrapText(true);
        responseLabel.setStyle("-fx-padding: 10px; -fx-text-fill: #277200; -fx-border-radius: 5px;");

        addLectureButton.setOnAction(e -> responseLabel.setText(sendAddLectureRequest()));
        backButton.setOnAction(e -> onBack.run());

        GridPane formGrid = new GridPane();
        formGrid.setPadding(new Insets(20));
        formGrid.setHgap(15);
        formGrid.setVgap(15);
        formGrid.setAlignment(Pos.CENTER);
        formGrid.addRow(0, new Label("Module:"), moduleField);
        formGrid.addRow(1, new Label("Date:"), datePicker);
        formGrid.addRow(2, new Label("Time:"), timeField);
        formGrid.addRow(3, new Label("Room:"), roomField);
        formGrid.addRow(4, backButton, addLectureButton);

        VBox layout = new VBox(20, titleLabel, formGrid, responseLabel);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #F0F4F8; -fx-border-radius: 10px; -fx-background-radius: 10px;");

        Scene scene = new Scene(layout, 700, 500);
        stage.setScene(scene);
        stage.setTitle("Add Lecture");
    }

    private String sendAddLectureRequest() {
        try {
            String module = moduleField.getText().trim();
            LocalDate date = datePicker.getValue();
            String time = timeField.getText().trim();
            String room = roomField.getText().trim();

            if (module.isEmpty() || date == null || time.isEmpty() || room.isEmpty()) {
                return "Please fill all fields.";
            }

            if (module.length() != 6) {
                throw new IncorrectActionException("Module code must be 6 chars long.");
            }

            String firstTwoChars = module.substring(0, 2);
            if (!Character.isLetter(firstTwoChars.charAt(0)) || !Character.isLetter(firstTwoChars.charAt(1))) {
                throw new IncorrectActionException("First two chars of module code must be letters");
            }

            String lastFourChars = module.substring(2, 6);
            if (lastFourChars.length() != 4) {
                throw new IncorrectActionException("Last four chars of module must be 4 digits.");
            }

            for (int i = 0; i < lastFourChars.length(); i++) {
                char currentChar = lastFourChars.charAt(i);
                if (!Character.isDigit(currentChar)) {
                    throw new IncorrectActionException("Last four chars of module must be numbers");
                }
            }

            // Ensure the time is at a full hour
            LocalTime parsedTime = LocalTime.parse(time);
            if (parsedTime.getMinute() != 0) {
                throw new IncorrectActionException("Invalid time format; must be full hour (e.g 12:00)");
            }

            String request = "Add$" + module + "," + date + "," + time + "," + room;
            return ClientConnection.getInstance().sendRequest(request); // Use persistent connection

        } catch (IncorrectActionException | IOException e) {
            return e.getMessage();
        }
    }


    private TextField createTextField(String prompt) {
        TextField textField = new TextField();
        textField.setPromptText(prompt);
        textField.setStyle("-fx-border-color: #ffffff; -fx-border-radius: 5px; -fx-padding: 7px; -fx-background-color: #ffffff;");
        return textField;
    }

    private Button createButton(String text, String color) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10px 20px; -fx-border-radius: 5px;");
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: black; -fx-font-weight: bold; -fx-padding: 10px 20px; -fx-border-radius: 5px;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10px 20px; -fx-border-radius: 5px;"));

        return button;
    }
}
