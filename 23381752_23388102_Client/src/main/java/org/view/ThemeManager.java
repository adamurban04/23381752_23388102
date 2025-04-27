package org.view;

import javafx.scene.Scene;

public class ThemeManager {
    private static boolean darkMode = false;
    private static Scene currentScene;

    public static void setScene(Scene scene) {
        currentScene = scene;
    }

    public static void toggleTheme() {
        darkMode = !darkMode;
        applyTheme();
    }

    public static void applyTheme() {
        if (currentScene == null) {
            System.err.println("No scene set for ThemeManager");
            return;
        }

        try {
            currentScene.getStylesheets().clear();
            String cssFile = darkMode ? "/styles/dark.css" : "/styles/light.css";
            String cssUrl = ThemeManager.class.getResource(cssFile).toExternalForm();
            currentScene.getStylesheets().add(cssUrl);
        } catch (NullPointerException e) {
            System.err.println("Failed to load CSS file: " + e.getMessage());
        }
    }
}