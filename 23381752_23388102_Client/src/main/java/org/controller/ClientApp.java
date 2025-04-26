package org.controller;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import org.view.*;


public class ClientApp extends Application {

    private static final int WIDTH = 700;
    private static final int HEIGHT = 500;
    private Stage primaryStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setWidth(WIDTH);
        primaryStage.setHeight(HEIGHT);
        primaryStage.setResizable(false);
        showMainScreen();



    }

    private void showMainScreen() {
        Label titleLabel = new Label("LM121 Timetable Manager");
        titleLabel.getStyleClass().add("title");


        Button addButton = createButton("Add Lecture", "#2C7A7B");
        Button removeButton = createButton("Remove Lecture", "#7b2d2c");
        Button displayButton = createButton("Display Timetable", "#537b2c");
        Button otherButton = createButton("Other", "#552c7b");
        Button themeButton = new Button("Toggle Light/Dark Mode");

        addButton.setOnAction(e -> new AddLectureView(primaryStage, this::showMainScreen));
        removeButton.setOnAction(e -> new RemoveLectureView(primaryStage, this::showMainScreen));
        displayButton.setOnAction(e -> new DisplayTimetableView(primaryStage, this::showMainScreen));
        otherButton.setOnAction(e -> new OtherView(primaryStage, this::showMainScreen));
        themeButton.setOnAction(e -> ThemeManager.toggleTheme());

        VBox buttonLayout = new VBox(15, titleLabel, addButton, removeButton, displayButton, otherButton, themeButton);
        buttonLayout.setPadding(new Insets(30));
        buttonLayout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(buttonLayout, WIDTH, HEIGHT);
        ThemeManager.initialize(scene);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Lecture Timetable");
        primaryStage.show();


    }




    private Button createButton(String text, String color) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10px 20px; -fx-border-radius: 5px;");
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: black; -fx-font-weight: bold; -fx-padding: 10px 20px; -fx-border-radius: 5px;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10px 20px; -fx-border-radius: 5px;"));
        return button;
    }

    @Override
    public void stop() throws Exception {
        ClientConnection.getInstance().closeConnection();
        super.stop();
    }

}