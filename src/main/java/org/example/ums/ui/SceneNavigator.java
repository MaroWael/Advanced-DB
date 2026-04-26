package org.example.ums.ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public final class SceneNavigator {

    private static Stage primaryStage;
    private static final String THEME_STYLESHEET = "/org/example/ums/ui/ums-theme.css";

    private SceneNavigator() {
    }

    public static void init(Stage stage) {
        primaryStage = stage;
    }

    public static void switchTo(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneNavigator.class.getResource(fxmlPath));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(SceneNavigator.class.getResource(THEME_STYLESHEET).toExternalForm());

            primaryStage.setTitle(title);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to load FXML: " + fxmlPath, exception);
        }
    }
}

