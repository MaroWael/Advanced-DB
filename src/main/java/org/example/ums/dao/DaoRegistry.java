package org.example.ums.dao;

public final class DaoRegistry {

    public static final UserDao USERS = new UserDao();
    public static final StudentDao STUDENTS = new StudentDao();
    public static final InstructorDao INSTRUCTORS = new InstructorDao();
    public static final AdminDao ADMINS = new AdminDao();
    public static final CourseDao COURSES = new CourseDao();
    public static final QuizDao QUIZZES = new QuizDao();
    public static final QuestionDao QUESTIONS = new QuestionDao();
    public static final QuizResultDao QUIZ_RESULTS = new QuizResultDao();
    public static final QuizAnswerDao QUIZ_ANSWERS = new QuizAnswerDao();

    private DaoRegistry() {
    }
}

