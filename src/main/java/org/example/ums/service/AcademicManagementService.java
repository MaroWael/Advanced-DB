package org.example.ums.service;

import org.example.ums.entity.Course;
import org.example.ums.entity.Instructor;
import org.example.ums.entity.Question;
import org.example.ums.entity.Quiz;
import org.example.ums.entity.QuizAnswer;
import org.example.ums.entity.QuizResult;
import org.example.ums.entity.Student;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class AcademicManagementService extends JpaServiceSupport {

    public Student addNewStudent(Student student) {
        return executeInTransaction(entityManager -> {
            entityManager.persist(student);
            return student;
        });
    }

    public Course addNewCourse(Course course) {
        return executeInTransaction(entityManager -> {
            entityManager.persist(course);
            return course;
        });
    }

    public Quiz addQuizWithQuestions(String courseCode, Quiz quiz, List<Question> questions) {
        return executeInTransaction(entityManager -> {
            Course course = entityManager.find(Course.class, courseCode);
            if (course == null) {
                throw new IllegalArgumentException("Course not found for code: " + courseCode);
            }

            quiz.setCourse(course);
            course.getQuizzes().add(quiz);

            for (Question question : questions) {
                question.setQuiz(quiz);
                quiz.getQuestions().add(question);
            }

            entityManager.persist(quiz);
            return quiz;
        });
    }

    public Optional<Student> updateStudentGrade(Integer studentId, Double newGrade) {
        return executeInTransaction(entityManager -> {
            Student student = entityManager.find(Student.class, studentId);
            if (student == null) {
                return Optional.empty();
            }

            student.setGrade(newGrade);
            return Optional.of(student);
        });
    }

    public Optional<Course> updateCourseInstructor(String courseCode, Integer instructorId) {
        return executeInTransaction(entityManager -> {
            Course course = entityManager.find(Course.class, courseCode);
            if (course == null) {
                return Optional.empty();
            }

            Instructor instructor = entityManager.find(Instructor.class, instructorId);
            if (instructor == null) {
                throw new IllegalArgumentException("Instructor not found for id: " + instructorId);
            }

            course.setInstructor(instructor);
            return Optional.of(course);
        });
    }

    public boolean enrollStudentInCourse(Integer studentId, String courseCode) {
        return executeInTransaction(entityManager -> {
            Student student = entityManager.find(Student.class, studentId);
            Course course = entityManager.find(Course.class, courseCode);

            if (student == null || course == null) {
                return false;
            }

            boolean addedToCourse = course.getStudents().add(student);
            boolean addedToStudent = student.getCourses().add(course);
            return addedToCourse || addedToStudent;
        });
    }

    public boolean deleteQuiz(Integer quizId) {
        return executeInTransaction(entityManager -> {
            Quiz quiz = entityManager.find(Quiz.class, quizId);
            if (quiz == null) {
                return false;
            }

            entityManager.remove(quiz);
            return true;
        });
    }

    public boolean removeStudentFromCourse(Integer studentId, String courseCode) {
        return executeInTransaction(entityManager -> {
            Student student = entityManager.find(Student.class, studentId);
            Course course = entityManager.find(Course.class, courseCode);

            if (student == null || course == null) {
                return false;
            }

            boolean removedFromCourse = course.getStudents().remove(student);
            boolean removedFromStudent = student.getCourses().remove(course);

            return removedFromCourse || removedFromStudent;
        });
    }

    public Optional<QuizResult> submitQuizAttempt(Integer studentId, Integer quizId, Map<Integer, Integer> chosenOptionIndexes) {
        return executeInTransaction(entityManager -> {
            Student student = entityManager.find(Student.class, studentId);
            Quiz quiz = entityManager.find(Quiz.class, quizId);
            if (student == null || quiz == null) {
                return Optional.empty();
            }

            Course course = quiz.getCourse();
            if (course == null || !student.getCourses().contains(course)) {
                throw new IllegalArgumentException("Student must be enrolled in the quiz course.");
            }

            Set<Question> questions = quiz.getQuestions();
            if (questions.isEmpty()) {
                throw new IllegalArgumentException("Quiz has no questions.");
            }

            Map<Integer, Integer> answers = chosenOptionIndexes == null ? new HashMap<>() : chosenOptionIndexes;
            int correctCount = 0;

            QuizResult quizResult = new QuizResult(student, quiz, 0);
            entityManager.persist(quizResult);

            for (Question question : questions) {
                Integer selectedIndex = answers.get(question.getId());
                String chosenAnswer = optionTextByIndex(question, selectedIndex);

                QuizAnswer quizAnswer = new QuizAnswer(quizResult, question, chosenAnswer);
                quizResult.getQuizAnswers().add(quizAnswer);
                question.getQuizAnswers().add(quizAnswer);
                entityManager.persist(quizAnswer);

                if (selectedIndex != null && selectedIndex.equals(question.getCorrectOptionIndex())) {
                    correctCount++;
                }
            }

            int score = (int) Math.round((correctCount * 100.0) / questions.size());
            quizResult.setScore(score);
            student.getQuizResults().add(quizResult);
            quiz.getQuizResults().add(quizResult);

            return Optional.of(quizResult);
        });
    }

    private String optionTextByIndex(Question question, Integer index) {
        if (index == null) {
            return null;
        }
        if (index == 1) {
            return question.getOption1();
        }
        if (index == 2) {
            return question.getOption2();
        }
        if (index == 3) {
            return question.getOption3();
        }
        if (index == 4) {
            return question.getOption4();
        }
        return null;
    }
}

