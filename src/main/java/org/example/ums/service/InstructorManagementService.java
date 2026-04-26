package org.example.ums.service;

import org.example.ums.entity.Course;
import org.example.ums.entity.Question;
import org.example.ums.entity.Quiz;

public class InstructorManagementService extends JpaServiceSupport {

    public Quiz createQuiz(String courseCode, String title) {
        return createQuiz(null, courseCode, title);
    }

    public Quiz createQuiz(Integer instructorId, String courseCode, String title) {
        return executeInTransaction(entityManager -> {
            Course course = entityManager.find(Course.class, courseCode);
            if (course == null) {
                throw new IllegalArgumentException("Course not found: " + courseCode);
            }

            if (instructorId != null) {
                if (course.getInstructor() == null || !instructorId.equals(course.getInstructor().getId())) {
                    throw new IllegalArgumentException("You can only create quizzes for your assigned courses.");
                }
            }

            Quiz quiz = new Quiz();
            quiz.setTitle(title);
            quiz.setCourse(course);
            course.getQuizzes().add(quiz);

            entityManager.persist(quiz);
            return quiz;
        });
    }

    public Question addQuestion(Integer quizId,
                                String text,
                                String option1,
                                String option2,
                                String option3,
                                String option4,
                                Integer correctOptionIndex) {
        return addQuestion(null, quizId, text, option1, option2, option3, option4, correctOptionIndex);
    }

    public Question addQuestion(Integer instructorId,
                                Integer quizId,
                                String text,
                                String option1,
                                String option2,
                                String option3,
                                String option4,
                                Integer correctOptionIndex) {
        return executeInTransaction(entityManager -> {
            Quiz quiz = entityManager.find(Quiz.class, quizId);
            if (quiz == null) {
                throw new IllegalArgumentException("Quiz not found: " + quizId);
            }

            if (instructorId != null) {
                Course course = quiz.getCourse();
                if (course == null || course.getInstructor() == null || !instructorId.equals(course.getInstructor().getId())) {
                    throw new IllegalArgumentException("You can only add questions to quizzes in your courses.");
                }
            }

            Question question = new Question();
            question.setQuiz(quiz);
            question.setText(text);
            question.setOption1(option1);
            question.setOption2(option2);
            question.setOption3(option3);
            question.setOption4(option4);
            question.setCorrectOptionIndex(correctOptionIndex);
            quiz.getQuestions().add(question);

            entityManager.persist(question);
            return question;
        });
    }
}

