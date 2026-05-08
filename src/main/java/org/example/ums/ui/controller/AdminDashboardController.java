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
import org.example.ums.ui.UiHelpers;
import org.example.ums.ui.UserSession;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

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
    private TableColumn<User, String> userLevelColumn;

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
        userLevelColumn.setCellValueFactory(cell -> {
            User user = cell.getValue();
            if (user instanceof Student student) {
                Integer level = student.getLevel();
                return new ReadOnlyStringWrapper(level == null ? "" : String.valueOf(level));
            }
            return new ReadOnlyStringWrapper("");
        });

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

        usersTable.getSelectionModel().selectedItemProperty().addListener((observable, oldUser, selectedUser) ->
                populateUserForm(selectedUser));

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
            UiHelpers.showSuccessToast(toastLabel, "User added successfully.");
        } catch (RuntimeException exception) {
            UiHelpers.showError("Validation Error", "Action failed", exception.getMessage());
        }
    }


    @FXML
    private void onDeleteUser() {
        User selected = usersTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            UiHelpers.showError("Validation Error", "Action failed", "Select a user first.");
            return;
        }
        adminService.deleteUser(selected.getId());
        refreshUsers();
        refreshInstructorOptions();
    }

    @FXML
    private void onAddCourse() {
        try {
            String code = UiHelpers.requireText(courseCodeField.getText(), "Course code is required.");
            String name = UiHelpers.requireText(courseNameField.getText(), "Course name is required.");

            Instructor instructor = courseInstructorCombo.getValue();

            Course course = new Course(code,
                    name,
                    UiHelpers.emptyToNull(courseLevelField.getText()),
                    UiHelpers.emptyToNull(courseMajorField.getText()),
                    UiHelpers.emptyToNull(courseTimeField.getText()),
                    instructor);

            adminService.addCourse(course);
            refreshCourses();
            clearCourseForm();
            UiHelpers.showSuccessToast(toastLabel, "Course added successfully.");
        } catch (RuntimeException exception) {
            UiHelpers.showError("Validation Error", "Action failed", exception.getMessage());
        }
    }

    @FXML
    private void onDeleteCourse() {
        Course selected = coursesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            UiHelpers.showError("Validation Error", "Action failed", "Select a course first.");
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
        String name = UiHelpers.requireText(userNameField.getText(), "Name is required.");
        String email = UiHelpers.requireText(userEmailField.getText(), "Email is required.");
        String password = UiHelpers.requireText(userPasswordField.getText(), "Password is required.");
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
                UiHelpers.emptyToNull(userMajorField.getText()),
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
        String value = UiHelpers.emptyToNull(text);
        return value == null ? null : Integer.parseInt(value);
    }

    private Double parseOptionalDouble(String text) {
        String value = UiHelpers.emptyToNull(text);
        return value == null ? null : Double.parseDouble(value);
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

    @FXML
    private void onUpdateStudentLevel() {
        User selected = usersTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            UiHelpers.showError("Validation Error", "Action failed", "Select a user first.");
            return;
        }
        if (!(selected instanceof Student student)) {
            UiHelpers.showError("Validation Error", "Action failed", "Selected user is not a student.");
            return;
        }
        Integer currentLevel = student.getLevel();
        if (currentLevel == null) {
            UiHelpers.showError("Validation Error", "Action failed", "Student level is missing.");
            return;
        }
        if (currentLevel >= 4) {
            UiHelpers.showError("Validation Error", "Action failed", "Student is already at the maximum level.");
            return;
        }
        try {
            Integer nextLevel = currentLevel + 1;
            adminService.updateStudentLevel(student.getId(), nextLevel);
            refreshUsers();
            UiHelpers.showSuccessToast(toastLabel, "Student level updated to " + nextLevel + ".");
        } catch (RuntimeException exception) {
            UiHelpers.showError("Validation Error", "Action failed", exception.getMessage());
        }
    }

    private void populateUserForm(User selectedUser) {
        if (selectedUser == null) {
            return;
        }
        userNameField.setText(selectedUser.getName());
        userEmailField.setText(selectedUser.getEmail());
        userPasswordField.clear();
        userRoleCombo.setValue(selectedUser.getRole());

        if (selectedUser instanceof Student student) {
            userDepartmentCombo.setValue(student.getDepartment());
            userLevelField.setText(student.getLevel() == null ? "" : String.valueOf(student.getLevel()));
            userMajorField.setText(student.getMajor() == null ? "" : student.getMajor());
            userGradeField.setText(student.getGrade() == null ? "" : String.valueOf(student.getGrade()));
            updateUserFormForRole(Role.STUDENT);
            return;
        }

        if (selectedUser instanceof Instructor instructor) {
            userDepartmentCombo.setValue(instructor.getDepartment());
            updateUserFormForRole(Role.INSTRUCTOR);
            return;
        }

        updateUserFormForRole(Role.ADMIN);
    }

}
