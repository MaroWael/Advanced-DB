package org.example.ums.ui;

import javafx.animation.PauseTransition;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.util.Duration;

public final class UiHelpers {

    private UiHelpers() {
    }

    public static String emptyToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    public static String requireText(String value, String message) {
        String trimmed = emptyToNull(value);
        if (trimmed == null) {
            throw new IllegalArgumentException(message);
        }
        return trimmed;
    }

    public static void showError(String title, String header, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.showAndWait();
    }

    public static void showSuccessToast(Label toastLabel, String message) {
        if (toastLabel == null) {
            return;
        }
        toastLabel.setText(message);
        toastLabel.setManaged(true);
        toastLabel.setVisible(true);

        PauseTransition hideLater = new PauseTransition(Duration.seconds(2.5));
        hideLater.setOnFinished(event -> {
            toastLabel.setVisible(false);
            toastLabel.setManaged(false);
        });
        hideLater.play();
    }
}

