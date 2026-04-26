package org.example.ums.ui.controller;

import org.example.ums.entity.Question;
import org.example.ums.entity.Quiz;
import org.example.ums.entity.Course;
import org.example.ums.entity.Instructor;
import org.example.ums.entity.Student;
import org.example.ums.entity.User;
import org.example.ums.service.AcademicQueryService;
import org.example.ums.service.InstructorManagementService;
import org.example.ums.ui.SceneNavigator;
import org.example.ums.ui.UserSession;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

import java.util.List;

public class InstructorDashboardController {

    private final InstructorManagementService instructorManagementService = new InstructorManagementService();
    private final AcademicQueryService queryService = new AcademicQueryService();

    @FXML
    private Label messageLabel;

    @FXML
    private ComboBox<Course> quizCourseCombo;
    @FXML
    private TextField quizTitleField;

    @FXML
    private TextField questionTextField;
    @FXML
    private TextField option1Field;
    @FXML
    private TextField option2Field;
    @FXML
    private TextField option3Field;
    @FXML
    private TextField option4Field;
    @FXML
    private TextField correctOptionIndexField;

    @FXML
    private ComboBox<Course> studentsCourseCombo;

    @FXML
    private TableView<Quiz> quizzesTable;
    @FXML
    private TableColumn<Quiz, Integer> quizIdColumn;
    @FXML
    private TableColumn<Quiz, String> quizTitleColumn;

    @FXML
    private TableView<Question> questionsTable;
    @FXML
    private TableColumn<Question, Integer> questionIdColumn;
    @FXML
    private TableColumn<Question, String> questionTextColumn;

    @FXML
    private TableView<Student> studentsTable;
    @FXML
    private TableColumn<Student, Integer> studentIdColumn;
    @FXML
    private TableColumn<Student, String> studentNameColumn;
    @FXML
    private TableColumn<Student, String> studentEmailColumn;

    @FXML
    private void initialize() {
        quizIdColumn.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getId()));
        quizTitleColumn.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().getTitle()));

        questionIdColumn.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getId()));
        questionTextColumn.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().getText()));

        studentIdColumn.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getId()));
        studentNameColumn.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().getName()));
        studentEmailColumn.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().getEmail()));

        quizCourseCombo.setConverter(new StringConverter<>() {
            @Override
            public String toString(Course course) {
                if (course == null) {
                    return "";
                }
                return course.getCode() + " - " + course.getCourseName();
            }

            @Override
            public Course fromString(String string) {
                return null;
            }
        });
        studentsCourseCombo.setConverter(new StringConverter<>() {
            @Override
            public String toString(Course course) {
                if (course == null) {
                    return "";
                }
                return course.getCode() + " - " + course.getCourseName();
            }

            @Override
            public Course fromString(String string) {
                return null;
            }
        });

        quizCourseCombo.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, selectedCourse) ->
                loadQuizzesForSelectedCourse(selectedCourse));

        quizzesTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, selectedQuiz) ->
                loadQuestionsForSelectedQuiz(selectedQuiz));

        loadInstructorCourses();
    }

    @FXML
    private void onCreateQuiz() {
        try {
            Course selectedCourse = quizCourseCombo.getValue();
            if (selectedCourse == null) {
                throw new IllegalArgumentException("Select a course first.");
            }
            String title = requireText(quizTitleField.getText(), "Quiz title is required.");

            User user = UserSession.getCurrentUser();
            if (!(user instanceof Instructor)) {
                throw new IllegalStateException("Instructor session not found.");
            }

            Instructor instructor = (Instructor) user;

            Quiz quiz = instructorManagementService.createQuiz(instructor.getId(), selectedCourse.getCode(), title);
            messageLabel.setText("Quiz created with id: " + quiz.getId());

            loadQuizzesForSelectedCourse(selectedCourse);
        } catch (RuntimeException exception) {
            showError(exception.getMessage());
        }
    }

    @FXML
    private void onAddQuestion() {
        try {
            Quiz selectedQuiz = quizzesTable.getSelectionModel().getSelectedItem();
            if (selectedQuiz == null) {
                throw new IllegalArgumentException("Select a quiz from the table first.");
            }

            String text = requireText(questionTextField.getText(), "Question text is required.");
            Integer correctOptionIndex = Integer.parseInt(requireText(correctOptionIndexField.getText(), "Correct option index is required."));
            if (correctOptionIndex < 1 || correctOptionIndex > 4) {
                throw new IllegalArgumentException("Correct option index must be between 1 and 4.");
            }

            User user = UserSession.getCurrentUser();
            if (!(user instanceof Instructor)) {
                throw new IllegalStateException("Instructor session not found.");
            }

            Instructor instructor = (Instructor) user;

            Question question = instructorManagementService.addQuestion(
                    instructor.getId(),
                    selectedQuiz.getId(),
                    text,
                    option1Field.getText(),
                    option2Field.getText(),
                    option3Field.getText(),
                    option4Field.getText(),
                    correctOptionIndex
            );

            messageLabel.setText("Question added with id: " + question.getId());
            loadQuestionsForSelectedQuiz(selectedQuiz);
        } catch (RuntimeException exception) {
            showError(exception.getMessage());
        }
    }

    @FXML
    private void onLoadStudents() {
        try {
            Course selectedCourse = studentsCourseCombo.getValue();
            if (selectedCourse == null) {
                throw new IllegalArgumentException("Select a course first.");
            }
            List<Student> students = queryService.getAllStudentsInCourse(selectedCourse.getCode());
            studentsTable.setItems(FXCollections.observableArrayList(students));
            messageLabel.setText("Loaded " + students.size() + " students.");
        } catch (RuntimeException exception) {
            showError(exception.getMessage());
        }
    }

    @FXML
    private void onLogout() {
        UserSession.clear();
        SceneNavigator.switchTo("/org/example/ums/ui/login-view.fxml", "University Management System");
    }

    private String requireText(String value, String message) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(message);
        }
        return value.trim();
    }

    private void loadInstructorCourses() {
        User user = UserSession.getCurrentUser();
        if (!(user instanceof Instructor)) {
            messageLabel.setText("Instructor session not found.");
            return;
        }

        Instructor instructor = (Instructor) user;
        List<Course> courses = queryService.getCoursesForInstructor(instructor.getId());
        quizCourseCombo.setItems(FXCollections.observableArrayList(courses));
        studentsCourseCombo.setItems(FXCollections.observableArrayList(courses));
        if (!courses.isEmpty()) {
            quizCourseCombo.getSelectionModel().selectFirst();
            studentsCourseCombo.getSelectionModel().selectFirst();
            messageLabel.setText("Loaded " + courses.size() + " assigned courses.");
        } else {
            quizzesTable.setItems(FXCollections.observableArrayList());
            questionsTable.setItems(FXCollections.observableArrayList());
            studentsTable.setItems(FXCollections.observableArrayList());
            messageLabel.setText("No courses are assigned to your account yet.");
        }
    }

    private void loadQuizzesForSelectedCourse(Course selectedCourse) {
        if (selectedCourse == null) {
            quizzesTable.setItems(FXCollections.observableArrayList());
            questionsTable.setItems(FXCollections.observableArrayList());
            return;
        }

        List<Quiz> quizzes = queryService.getQuizzesForCourse(selectedCourse.getCode());
        quizzesTable.setItems(FXCollections.observableArrayList(quizzes));
        questionsTable.setItems(FXCollections.observableArrayList());
    }

    private void loadQuestionsForSelectedQuiz(Quiz selectedQuiz) {
        if (selectedQuiz == null) {
            questionsTable.setItems(FXCollections.observableArrayList());
            return;
        }

        List<Question> questions = queryService.getQuestionsForQuiz(selectedQuiz.getId());
        questionsTable.setItems(FXCollections.observableArrayList(questions));
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Action Error");
        alert.setHeaderText("Operation failed");
        alert.setContentText(message);
        alert.showAndWait();
    }
}

