package org.example.ums.ui.controller;

import org.example.ums.entity.User;
import org.example.ums.entity.enums.Role;
import org.example.ums.service.AuthService;
import org.example.ums.ui.SceneNavigator;
import org.example.ums.ui.UserSession;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    private final AuthService authService = new AuthService();

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label messageLabel;

    @FXML
    private void onLogin() {
        String email = emailField.getText() == null ? "" : emailField.getText().trim();
        String password = passwordField.getText() == null ? "" : passwordField.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Please enter email and password.");
            return;
        }

        User user = authService.authenticate(email, password).orElse(null);
        if (user == null) {
            messageLabel.setText("Invalid credentials.");
            return;
        }

        UserSession.setCurrentUser(user);

        Role role = user.getRole();
        if (role == Role.ADMIN) {
            SceneNavigator.switchTo("/org/example/ums/ui/admin-dashboard.fxml", "Admin Dashboard");
        } else if (role == Role.STUDENT) {
            SceneNavigator.switchTo("/org/example/ums/ui/student-dashboard.fxml", "Student Dashboard");
        } else if (role == Role.INSTRUCTOR) {
            SceneNavigator.switchTo("/org/example/ums/ui/instructor-dashboard.fxml", "Instructor Dashboard");
        } else {
            messageLabel.setText("Unsupported role.");
        }
    }
}

