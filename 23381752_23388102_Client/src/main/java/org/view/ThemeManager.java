package org.view;

import javafx.scene.Scene;

public class ThemeManager {
    private static boolean darkMode = false;
    private static String lightTheme;
    private static String darkTheme;
    private static Scene currentScene;

    public static void initialize(Scene scene) {
        lightTheme = ThemeManager.class.getResource("/light.css").toExternalForm();
        darkTheme = ThemeManager.class.getResource("/dark.css").toExternalForm();
        currentScene = scene;
        applyTheme();
    }

    public static void toggleTheme() {
        darkMode = !darkMode;
        applyTheme();
    }

    public static void applyTheme() {
        if (currentScene == null) return;
        currentScene.getStylesheets().clear();
        if (darkMode) {
            currentScene.getStylesheets().add(darkTheme);
        } else {
            currentScene.getStylesheets().add(lightTheme);
        }
    }

    public static void setScene(Scene scene) {
        currentScene = scene;
        applyTheme();
    }
}
