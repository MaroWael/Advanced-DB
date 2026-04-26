package org.example.ums.service;

import org.example.ums.entity.Admin;
import org.example.ums.entity.Course;
import org.example.ums.entity.Instructor;
import org.example.ums.entity.Question;
import org.example.ums.entity.Quiz;
import org.example.ums.entity.QuizAnswer;
import org.example.ums.entity.QuizResult;
import org.example.ums.entity.Student;
import org.example.ums.entity.enums.Department;

public class DataBootstrapService extends JpaServiceSupport {

    public void seedIfEmpty() {
        Long userCount = execute(entityManager -> entityManager.createQuery("select count(u) from User u", Long.class)
                .getSingleResult());
        if (userCount != null && userCount > 0) {
            return;
        }

        executeInTransaction(entityManager -> {
            Admin admin = new Admin("System Admin", "admin@ums.local", "admin123");
            Instructor instructor = new Instructor("Dr. Lina", "instructor@ums.local", "inst123", Department.CS);
            Student studentOne = new Student("Ali Hassan", "student1@ums.local", "stud123", 2, "Computer Science", 3.4, Department.CS);
            Student studentTwo = new Student("Mona Adel", "student2@ums.local", "stud123", 3, "Information Systems", 3.8, Department.IS);

            entityManager.persist(admin);
            entityManager.persist(instructor);
            entityManager.persist(studentOne);
            entityManager.persist(studentTwo);

            Course cs101 = new Course("CS101", "Intro to Programming", "1", "CS", "Sun 10:00", instructor);
            Course is201 = new Course("IS201", "Database Systems", "2", "IS", "Tue 12:00", instructor);
            instructor.getCourses().add(cs101);
            instructor.getCourses().add(is201);

            cs101.getStudents().add(studentOne);
            cs101.getStudents().add(studentTwo);
            studentOne.getCourses().add(cs101);
            studentTwo.getCourses().add(cs101);

            entityManager.persist(cs101);
            entityManager.persist(is201);

            Quiz quiz = new Quiz("Java Basics Quiz", cs101);
            cs101.getQuizzes().add(quiz);

            Question q1 = new Question(quiz,
                    "Which keyword is used to inherit a class in Java?",
                    "implement",
                    "extends",
                    "inherits",
                    "instanceof",
                    2);
            Question q2 = new Question(quiz,
                    "Which collection stores unique values?",
                    "List",
                    "Map",
                    "Set",
                    "Queue",
                    3);
            quiz.getQuestions().add(q1);
            quiz.getQuestions().add(q2);

            entityManager.persist(quiz);

            QuizResult resultOne = new QuizResult(studentOne, quiz, 85);
            QuizResult resultTwo = new QuizResult(studentTwo, quiz, 93);
            studentOne.getQuizResults().add(resultOne);
            studentTwo.getQuizResults().add(resultTwo);
            quiz.getQuizResults().add(resultOne);
            quiz.getQuizResults().add(resultTwo);

            entityManager.persist(resultOne);
            entityManager.persist(resultTwo);

            QuizAnswer answerOne = new QuizAnswer(resultOne, q1, "extends");
            QuizAnswer answerTwo = new QuizAnswer(resultOne, q2, "Set");
            QuizAnswer answerThree = new QuizAnswer(resultTwo, q1, "extends");
            QuizAnswer answerFour = new QuizAnswer(resultTwo, q2, "Set");

            resultOne.getQuizAnswers().add(answerOne);
            resultOne.getQuizAnswers().add(answerTwo);
            resultTwo.getQuizAnswers().add(answerThree);
            resultTwo.getQuizAnswers().add(answerFour);

            q1.getQuizAnswers().add(answerOne);
            q1.getQuizAnswers().add(answerThree);
            q2.getQuizAnswers().add(answerTwo);
            q2.getQuizAnswers().add(answerFour);

            entityManager.persist(answerOne);
            entityManager.persist(answerTwo);
            entityManager.persist(answerThree);
            entityManager.persist(answerFour);

            return null;
        });
    }
}

