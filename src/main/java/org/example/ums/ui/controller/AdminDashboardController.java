package org.example.ums.ui.controller;

import org.example.ums.entity.Admin;
import org.example.ums.entity.Course;
import org.example.ums.entity.Instructor;
import org.example.ums.entity.Student;
import org.example.ums.entity.User;
import org.example.ums.entity.enums.Department;
import org.example.ums.entity.enums.Role;
import org.example.ums.service.AdminManagementService;
import org.example.ums.ui.SceneNavigator;
import org.example.ums.ui.UserSession;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import javafx.util.Duration;

import java.util.List;

public class AdminDashboardController {

    private final AdminManagementService adminService = new AdminManagementService();

    @FXML
    private TableView<User> usersTable;
    @FXML
    private TableColumn<User, Integer> userIdColumn;
    @FXML
    private TableColumn<User, String> userNameColumn;
    @FXML
    private TableColumn<User, String> userEmailColumn;
    @FXML
    private TableColumn<User, Role> userRoleColumn;

    @FXML
    private TextField userNameField;
    @FXML
    private TextField userEmailField;
    @FXML
    private TextField userPasswordField;
    @FXML
    private ComboBox<Role> userRoleCombo;
    @FXML
    private ComboBox<Department> userDepartmentCombo;
    @FXML
    private TextField userLevelField;
    @FXML
    private TextField userMajorField;
    @FXML
    private TextField userGradeField;

    @FXML
    private Label toastLabel;

    @FXML
    private TableView<Course> coursesTable;
    @FXML
    private TableColumn<Course, String> courseCodeColumn;
    @FXML
    private TableColumn<Course, String> courseNameColumn;
    @FXML
    private TableColumn<Course, String> courseLevelColumn;
    @FXML
    private TableColumn<Course, String> courseMajorColumn;
    @FXML
    private TableColumn<Course, Integer> courseInstructorIdColumn;

    @FXML
    private TextField courseCodeField;
    @FXML
    private TextField courseNameField;
    @FXML
    private TextField courseLevelField;
    @FXML
    private TextField courseMajorField;
    @FXML
    private TextField courseTimeField;
    @FXML
    private ComboBox<Instructor> courseInstructorCombo;

    @FXML
    private void initialize() {
        userIdColumn.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getId()));
        userNameColumn.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().getName()));
        userEmailColumn.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().getEmail()));
        userRoleColumn.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getRole()));

        courseCodeColumn.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().getCode()));
        courseNameColumn.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().getCourseName()));
        courseLevelColumn.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().getLevel()));
        courseMajorColumn.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().getMajor()));
        courseInstructorIdColumn.setCellValueFactory(cell -> {
            Instructor instructor = cell.getValue().getInstructor();
            return new ReadOnlyObjectWrapper<>(instructor == null ? null : instructor.getId());
        });

        userRoleCombo.setItems(FXCollections.observableArrayList(Role.values()));
        userDepartmentCombo.setItems(FXCollections.observableArrayList(Department.values()));
        courseInstructorCombo.setConverter(new StringConverter<>() {
            @Override
            public String toString(Instructor instructor) {
                if (instructor == null) {
                    return "";
                }
                return instructor.getId() + " - " + instructor.getName();
            }

            @Override
            public Instructor fromString(String string) {
                return null;
            }
        });

        userRoleCombo.getSelectionModel().selectedItemProperty().addListener((observable, oldRole, selectedRole) ->
                updateUserFormForRole(selectedRole));

        refreshUsers();
        refreshCourses();
        refreshInstructorOptions();
        updateUserFormForRole(null);
    }

    @FXML
    private void onAddUser() {
        try {
            User user = buildUserFromForm();
            adminService.addUser(user);
            refreshUsers();
            refreshInstructorOptions();
            clearUserForm();
            showSuccessToast("User added successfully.");
        } catch (RuntimeException exception) {
            showError(exception.getMessage());
        }
    }


    @FXML
    private void onDeleteUser() {
        User selected = usersTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Select a user first.");
            return;
        }
        adminService.deleteUser(selected.getId());
        refreshUsers();
        refreshInstructorOptions();
    }

    @FXML
    private void onAddCourse() {
        try {
            String code = requireText(courseCodeField.getText(), "Course code is required.");
            String name = requireText(courseNameField.getText(), "Course name is required.");

            Instructor instructor = courseInstructorCombo.getValue();

            Course course = new Course(code,
                    name,
                    emptyToNull(courseLevelField.getText()),
                    emptyToNull(courseMajorField.getText()),
                    emptyToNull(courseTimeField.getText()),
                    instructor);

            adminService.addCourse(course);
            refreshCourses();
            clearCourseForm();
            showSuccessToast("Course added successfully.");
        } catch (RuntimeException exception) {
            showError(exception.getMessage());
        }
    }

    @FXML
    private void onDeleteCourse() {
        Course selected = coursesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Select a course first.");
            return;
        }
        adminService.deleteCourse(selected.getCode());
        refreshCourses();
    }

    @FXML
    private void onLogout() {
        UserSession.clear();
        SceneNavigator.switchTo("/org/example/ums/ui/login-view.fxml", "University Management System");
    }

    private User buildUserFromForm() {
        String name = requireText(userNameField.getText(), "Name is required.");
        String email = requireText(userEmailField.getText(), "Email is required.");
        String password = requireText(userPasswordField.getText(), "Password is required.");
        Role role = userRoleCombo.getValue();
        if (role == null) {
            throw new IllegalArgumentException("Role is required.");
        }

        if (role == Role.ADMIN) {
            return new Admin(name, email, password);
        }

        Department department = userDepartmentCombo.getValue();
        if (department == null) {
            throw new IllegalArgumentException("Department is required for student/instructor.");
        }

        if (role == Role.INSTRUCTOR) {
            return new Instructor(name, email, password, department);
        }

        Integer level = parseOptionalInteger(userLevelField.getText());
        Double grade = parseOptionalDouble(userGradeField.getText());
        return new Student(name,
                email,
                password,
                level,
                emptyToNull(userMajorField.getText()),
                grade,
                department);
    }

    private void refreshUsers() {
        List<User> users = adminService.getAllUsers();
        usersTable.setItems(FXCollections.observableArrayList(users));
    }

    private void refreshCourses() {
        List<Course> courses = adminService.getAllCourses();
        coursesTable.setItems(FXCollections.observableArrayList(courses));
    }

    private Integer parseOptionalInteger(String text) {
        String value = emptyToNull(text);
        return value == null ? null : Integer.parseInt(value);
    }

    private Double parseOptionalDouble(String text) {
        String value = emptyToNull(text);
        return value == null ? null : Double.parseDouble(value);
    }

    private String requireText(String value, String message) {
        String trimmed = emptyToNull(value);
        if (trimmed == null) {
            throw new IllegalArgumentException(message);
        }
        return trimmed;
    }

    private String emptyToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private void clearUserForm() {
        userNameField.clear();
        userEmailField.clear();
        userPasswordField.clear();
        userRoleCombo.setValue(null);
        userDepartmentCombo.setValue(null);
        userLevelField.clear();
        userMajorField.clear();
        userGradeField.clear();
        updateUserFormForRole(null);
    }

    private void clearCourseForm() {
        courseCodeField.clear();
        courseNameField.clear();
        courseLevelField.clear();
        courseMajorField.clear();
        courseTimeField.clear();
        courseInstructorCombo.setValue(null);
    }

    private void refreshInstructorOptions() {
        List<Instructor> instructors = adminService.getAllInstructors();
        courseInstructorCombo.setItems(FXCollections.observableArrayList(instructors));
    }

    private void updateUserFormForRole(Role role) {
        boolean requiresDepartment = role == Role.STUDENT || role == Role.INSTRUCTOR;
        boolean studentSelected = role == Role.STUDENT;
        userDepartmentCombo.setDisable(!requiresDepartment);
        if (!requiresDepartment) {
            userDepartmentCombo.setValue(null);
        }
        userLevelField.setDisable(!studentSelected);
        userMajorField.setDisable(!studentSelected);
        userGradeField.setDisable(!studentSelected);
        if (!studentSelected) {
            userLevelField.clear();
            userMajorField.clear();
            userGradeField.clear();
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Validation Error");
        alert.setHeaderText("Action failed");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccessToast(String message) {
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

