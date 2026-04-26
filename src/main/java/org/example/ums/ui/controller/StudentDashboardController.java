package org.example.ums.ui.controller;

import org.example.ums.entity.Course;
import org.example.ums.entity.Question;
import org.example.ums.entity.Quiz;
import org.example.ums.entity.QuizResult;
import org.example.ums.entity.Student;
import org.example.ums.entity.User;
import org.example.ums.service.AcademicManagementService;
import org.example.ums.service.AcademicQueryService;
import org.example.ums.ui.SceneNavigator;
import org.example.ums.ui.UserSession;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class StudentDashboardController {

    private final AcademicQueryService queryService = new AcademicQueryService();
    private final AcademicManagementService managementService = new AcademicManagementService();
    private Student currentStudent;

    @FXML
    private Label welcomeLabel;

    @FXML
    private Label toastLabel;

    @FXML
    private TableView<Course> availableCoursesTable;
    @FXML
    private TableColumn<Course, String> availableCourseCodeColumn;
    @FXML
    private TableColumn<Course, String> availableCourseNameColumn;

    @FXML
    private TableView<Course> coursesTable;
    @FXML
    private TableColumn<Course, String> courseCodeColumn;
    @FXML
    private TableColumn<Course, String> courseNameColumn;
    @FXML
    private TableColumn<Course, String> courseTimeColumn;

    @FXML
    private TableView<Quiz> quizzesTable;
    @FXML
    private TableColumn<Quiz, Integer> quizIdColumn;
    @FXML
    private TableColumn<Quiz, String> quizTitleColumn;

    @FXML
    private TableView<QuizResult> resultsTable;
    @FXML
    private TableColumn<QuizResult, Integer> resultIdColumn;
    @FXML
    private TableColumn<QuizResult, String> resultQuizTitleColumn;
    @FXML
    private TableColumn<QuizResult, Integer> resultScoreColumn;

    @FXML
    private void initialize() {
        availableCourseCodeColumn.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().getCode()));
        availableCourseNameColumn.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().getCourseName()));

        courseCodeColumn.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().getCode()));
        courseNameColumn.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().getCourseName()));
        courseTimeColumn.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().getLectureTime()));

        quizIdColumn.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getId()));
        quizTitleColumn.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().getTitle()));

        resultIdColumn.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getId()));
        resultQuizTitleColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().getQuiz() == null ? "" : cell.getValue().getQuiz().getTitle()));
        resultScoreColumn.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getScore()));

        User user = UserSession.getCurrentUser();
        if (!(user instanceof Student)) {
            welcomeLabel.setText("Student not found in session.");
            return;
        }

        currentStudent = (Student) user;
        welcomeLabel.setText("Welcome, " + currentStudent.getName());

        coursesTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, selectedCourse) ->
                loadQuizzesForCourse(selectedCourse));

        refreshDashboard();
    }

    @FXML
    private void onEnrollSelectedCourse() {
        if (currentStudent == null) {
            return;
        }

        Course selectedCourse = availableCoursesTable.getSelectionModel().getSelectedItem();
        if (selectedCourse == null) {
            showError("Select a course to enroll.");
            return;
        }

        boolean enrolled = managementService.enrollStudentInCourse(currentStudent.getId(), selectedCourse.getCode());
        if (!enrolled) {
            showError("Enrollment failed.");
            return;
        }

        refreshDashboard();
    }

    @FXML
    private void onTakeSelectedQuiz() {
        if (currentStudent == null) {
            return;
        }

        Quiz selectedQuiz = quizzesTable.getSelectionModel().getSelectedItem();
        if (selectedQuiz == null) {
            showError("Select a quiz first.");
            return;
        }

        List<Question> questions = queryService.getQuestionsForStudentQuiz(currentStudent.getId(), selectedQuiz.getId());
        if (questions.isEmpty()) {
            showError("No questions found for this quiz in your enrolled courses.");
            return;
        }

        Optional<Map<Integer, Integer>> answers = askForAnswers(selectedQuiz.getTitle(), questions);
        if (answers.isEmpty()) {
            return;
        }

        QuizResult quizResult = managementService.submitQuizAttempt(currentStudent.getId(), selectedQuiz.getId(), answers.get())
                .orElseThrow(() -> new IllegalStateException("Could not submit quiz."));

        showSuccessToast("Quiz submitted. Score: " + quizResult.getScore());

        refreshDashboard();
    }

    private Optional<Map<Integer, Integer>> askForAnswers(String quizTitle, List<Question> questions) {
        Dialog<Map<Integer, Integer>> dialog = new Dialog<>();
        dialog.setTitle("Take Quiz");
        dialog.setHeaderText("Quiz: " + quizTitle + " | Questions: " + questions.size());
        dialog.getDialogPane().setPrefWidth(720);

        ButtonType submitType = new ButtonType("Submit Quiz", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(submitType, ButtonType.CANCEL);

        Map<Integer, ToggleGroup> answerGroups = new LinkedHashMap<>();
        Map<Integer, VBox> questionBlocks = new LinkedHashMap<>();
        List<Integer> orderedQuestionIds = new java.util.ArrayList<>();
        int index = 1;
        for (Question question : questions) {
            ToggleGroup toggleGroup = new ToggleGroup();
            Integer questionId = question.getId();
            answerGroups.put(questionId, toggleGroup);
            VBox questionBlock = buildQuestionBlock(index, question, toggleGroup);
            questionBlocks.put(questionId, questionBlock);
            orderedQuestionIds.add(questionId);
            index++;
        }

        VBox cardHost = new VBox();
        cardHost.getStyleClass().add("quiz-card-host");

        ScrollPane scrollPane = new ScrollPane(cardHost);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefViewportHeight(430);
        scrollPane.getStyleClass().add("quiz-dialog-scroll");

        Label progressLabel = new Label("Answered 0 / " + questions.size());
        progressLabel.getStyleClass().add("quiz-progress-label");
        Label stepLabel = new Label("Question 1 of " + questions.size());
        stepLabel.getStyleClass().add("quiz-step-label");
        stepLabel.setContentDisplay(ContentDisplay.LEFT);
        Label validationLabel = new Label();
        validationLabel.getStyleClass().add("validation-text");

        Label helperLabel = new Label("Select one answer for each question, then submit once.");
        helperLabel.getStyleClass().add("hint-label");
        helperLabel.getStyleClass().add("quiz-helper-label");

        Button previousButton = new Button("Previous");
        Button nextButton = new Button("Next");
        previousButton.getStyleClass().add("secondary-button");
        nextButton.getStyleClass().add("primary-button");

        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
        HBox navigationRow = new HBox(8, previousButton, spacer, nextButton);
        navigationRow.getStyleClass().add("quiz-nav-actions");

        HBox statusRow = new HBox(8, stepLabel, progressLabel);
        statusRow.getStyleClass().add("quiz-status-row");

        VBox topSection = new VBox(6, helperLabel, statusRow);
        topSection.getStyleClass().add("quiz-top-section");

        VBox content = new VBox(10,
                topSection,
                navigationRow,
                validationLabel,
                scrollPane);
        content.getStyleClass().add("quiz-dialog-content");
        dialog.getDialogPane().setContent(content);

        Node submitButton = dialog.getDialogPane().lookupButton(submitType);
        submitButton.setDisable(true);

        int[] activeQuestionId = new int[]{questions.get(0).getId()};
        int[] currentIndex = new int[]{0};

        Runnable renderCurrentQuestion = () -> {
            Integer currentQuestionId = orderedQuestionIds.get(currentIndex[0]);
            activeQuestionId[0] = currentQuestionId;
            VBox currentCard = questionBlocks.get(currentQuestionId);
            cardHost.getChildren().setAll(currentCard);
            stepLabel.setText("Question " + (currentIndex[0] + 1) + " of " + questions.size());
            previousButton.setDisable(currentIndex[0] == 0);
            nextButton.setDisable(currentIndex[0] == orderedQuestionIds.size() - 1);
            updateQuizDialogState(answerGroups,
                    questions.size(),
                    progressLabel,
                    validationLabel,
                    submitButton,
                    questionBlocks,
                    activeQuestionId[0]);
        };

        previousButton.setOnAction(event -> {
            if (currentIndex[0] > 0) {
                currentIndex[0]--;
                renderCurrentQuestion.run();
            }
        });
        nextButton.setOnAction(event -> {
            if (currentIndex[0] < orderedQuestionIds.size() - 1) {
                currentIndex[0]++;
                renderCurrentQuestion.run();
            }
        });

        answerGroups.values().forEach(toggleGroup -> toggleGroup.selectedToggleProperty().addListener((observable, oldValue, selected) ->
                updateQuizDialogState(answerGroups,
                        questions.size(),
                        progressLabel,
                        validationLabel,
                        submitButton,
                        questionBlocks,
                        activeQuestionId[0])));

        renderCurrentQuestion.run();

        submitButton.addEventFilter(ActionEvent.ACTION, event -> {
            if (hasUnansweredQuestion(answerGroups)) {
                validationLabel.setText("Please answer all questions before submitting.");
                event.consume();
            }
        });

        dialog.setResultConverter(buttonType -> {
            if (buttonType != submitType) {
                return null;
            }
            Map<Integer, Integer> answers = new LinkedHashMap<>();
            answerGroups.forEach((questionId, toggleGroup) ->
                    answers.put(questionId, (Integer) toggleGroup.getSelectedToggle().getUserData()));
            return answers;
        });

        return dialog.showAndWait();
    }

    private VBox buildQuestionBlock(int questionNumber, Question question, ToggleGroup toggleGroup) {
        Label questionLabel = new Label(questionNumber + ") " + safeText(question.getText()));
        questionLabel.getStyleClass().add("quiz-question-title");
        questionLabel.setWrapText(true);

        RadioButton optionOne = new RadioButton("1) " + safeText(question.getOption1()));
        optionOne.setUserData(1);
        optionOne.setToggleGroup(toggleGroup);
        optionOne.getStyleClass().add("quiz-option");

        RadioButton optionTwo = new RadioButton("2) " + safeText(question.getOption2()));
        optionTwo.setUserData(2);
        optionTwo.setToggleGroup(toggleGroup);
        optionTwo.getStyleClass().add("quiz-option");

        RadioButton optionThree = new RadioButton("3) " + safeText(question.getOption3()));
        optionThree.setUserData(3);
        optionThree.setToggleGroup(toggleGroup);
        optionThree.getStyleClass().add("quiz-option");

        RadioButton optionFour = new RadioButton("4) " + safeText(question.getOption4()));
        optionFour.setUserData(4);
        optionFour.setToggleGroup(toggleGroup);
        optionFour.getStyleClass().add("quiz-option");

        VBox questionBlock = new VBox(8, questionLabel, optionOne, optionTwo, optionThree, optionFour);
        questionBlock.getStyleClass().add("quiz-question-block");
        return questionBlock;
    }

    private void updateQuizDialogState(Map<Integer, ToggleGroup> answerGroups,
                                       int totalQuestions,
                                       Label progressLabel,
                                       Label validationLabel,
                                       Node submitButton,
                                       Map<Integer, VBox> questionBlocks,
                                       Integer activeQuestionId) {
        int answeredCount = countAnsweredQuestions(answerGroups);
        progressLabel.setText("Answered " + answeredCount + " / " + totalQuestions);
        boolean completed = answeredCount == totalQuestions;
        submitButton.setDisable(!completed);
        if (completed) {
            validationLabel.setText("");
        }

        questionBlocks.forEach((questionId, block) -> {
            block.getStyleClass().removeAll("quiz-question-block-active", "quiz-question-block-answered");
            if (activeQuestionId != null && activeQuestionId.equals(questionId)) {
                block.getStyleClass().add("quiz-question-block-active");
            }
            ToggleGroup answerGroup = answerGroups.get(questionId);
            if (answerGroup != null && answerGroup.getSelectedToggle() != null) {
                block.getStyleClass().add("quiz-question-block-answered");
            }
        });
    }

    private int countAnsweredQuestions(Map<Integer, ToggleGroup> answerGroups) {
        int answered = 0;
        for (ToggleGroup toggleGroup : answerGroups.values()) {
            if (toggleGroup.getSelectedToggle() != null) {
                answered++;
            }
        }
        return answered;
    }

    private boolean hasUnansweredQuestion(Map<Integer, ToggleGroup> answerGroups) {
        for (ToggleGroup toggleGroup : answerGroups.values()) {
            if (toggleGroup.getSelectedToggle() == null) {
                return true;
            }
        }
        return false;
    }

    private void refreshDashboard() {
        List<Course> enrolledCourses = queryService.getAllCoursesForStudent(currentStudent.getId());
        List<Course> availableCourses = queryService.getAvailableCoursesForStudent(currentStudent.getId());
        List<QuizResult> results = queryService.getStudentQuizResults(currentStudent.getId());

        coursesTable.setItems(FXCollections.observableArrayList(enrolledCourses));
        availableCoursesTable.setItems(FXCollections.observableArrayList(availableCourses));
        resultsTable.setItems(FXCollections.observableArrayList(results));

        quizzesTable.setItems(FXCollections.observableArrayList());
    }

    private void loadQuizzesForCourse(Course course) {
        if (course == null || currentStudent == null) {
            quizzesTable.setItems(FXCollections.observableArrayList());
            return;
        }

        List<Quiz> quizzes = queryService.getQuizzesForStudentInCourse(currentStudent.getId(), course.getCode());
        quizzesTable.setItems(FXCollections.observableArrayList(quizzes));
    }

    private String safeText(String value) {
        return value == null ? "-" : value;
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.setTitle("Action Error");
        alert.setHeaderText("Operation failed");
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

    @FXML
    private void onLogout() {
        UserSession.clear();
        SceneNavigator.switchTo("/org/example/ums/ui/login-view.fxml", "University Management System");
    }
}

