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

public class DataBootstrapService {

    public void seedIfEmpty() {
        Long userCount = JpaUtil.execute(entityManager -> entityManager.createQuery("select count(u) from User u", Long.class)
                .getSingleResult());
        if (userCount != null && userCount > 0) {
            return;
        }

        JpaUtil.executeInTransaction(entityManager -> {
            // Create Admins
            Admin admin1 = new Admin("System Admin", "admin@ums.local", "admin123");
            Admin admin2 = new Admin("Super Admin", "superadmin@ums.local", "admin456");
            entityManager.persist(admin1);
            entityManager.persist(admin2);

            // Create Instructors from different departments
            Instructor instructor1 = new Instructor("Dr. Hashim", "instructor1@ums.local", "inst123", Department.CS);
            Instructor instructor2 = new Instructor("Prof. Sara", "instructor2@ums.local", "inst456", Department.IS);
            Instructor instructor3 = new Instructor("Dr. Ahmed", "instructor3@ums.local", "inst789", Department.CS);
            Instructor instructor4 = new Instructor("Dr. Fatima", "instructor4@ums.local", "inst101", Department.IS);
            entityManager.persist(instructor1);
            entityManager.persist(instructor2);
            entityManager.persist(instructor3);
            entityManager.persist(instructor4);

            // Create Students at different levels and departments
            // Level 1 Students
            Student s1 = new Student("Ali Hassan", "student1@ums.local", "stud123", 1, "Computer Science", 3.4, Department.CS);
            Student s2 = new Student("Fatima Ahmed", "student2@ums.local", "stud123", 1, "Computer Science", 3.6, Department.CS);
            Student s3 = new Student("Omar Mohamed", "student3@ums.local", "stud123", 1, "Information Systems", 3.2, Department.IS);

            // Level 2 Students
            Student s4 = new Student("Mona Adel", "student4@ums.local", "stud123", 2, "Computer Science", 3.8, Department.CS);
            Student s5 = new Student("Hassan Ibrahim", "student5@ums.local", "stud123", 2, "Information Systems", 3.5, Department.IS);
            Student s6 = new Student("Layla Karim", "student6@ums.local", "stud123", 2, "Computer Science", 3.7, Department.CS);

            // Level 3 Students
            Student s7 = new Student("Youssef Saleh", "student7@ums.local", "stud123", 3, "Information Systems", 3.9, Department.IS);
            Student s8 = new Student("Noor Khalil", "student8@ums.local", "stud123", 3, "Computer Science", 3.3, Department.CS);
            Student s9 = new Student("Rania Samir", "student9@ums.local", "stud123", 3, "Information Systems", 3.6, Department.IS);

            // Level 4 Students
            Student s10 = new Student("Karim Hassan", "student10@ums.local", "stud123", 4, "Computer Science", 3.85, Department.CS);
            Student s11 = new Student("Maya Ahmed", "student11@ums.local", "stud123", 4, "Information Systems", 3.95, Department.IS);
            Student s12 = new Student("Tariq Fouad", "student12@ums.local", "stud123", 4, "Computer Science", 3.45, Department.CS);

            // Persist all students
            for (Student student : new Student[]{s1, s2, s3, s4, s5, s6, s7, s8, s9, s10, s11, s12}) {
                entityManager.persist(student);
            }

            // Create Courses for different levels
            Course cs101 = new Course("CS101", "Intro to Programming", "1", "CS", "Sun 10:00", instructor1);
            Course cs102 = new Course("CS102", "Web Development Basics", "1", "CS", "Tue 10:00", instructor3);
            Course is101 = new Course("IS101", "IT Fundamentals", "1", "IS", "Wed 14:00", instructor2);

            Course cs201 = new Course("CS201", "Data Structures", "2", "CS", "Mon 11:00", instructor1);
            Course cs202 = new Course("CS202", "Database Systems", "2", "CS", "Thu 14:00", instructor3);
            Course is201 = new Course("IS201", "System Analysis", "2", "IS", "Tue 13:00", instructor2);

            Course cs301 = new Course("CS301", "Algorithms", "3", "CS", "Wed 10:00", instructor1);
            Course cs302 = new Course("CS302", "Software Engineering", "3", "CS", "Fri 11:00", instructor3);
            Course is301 = new Course("IS301", "Business Systems", "3", "IS", "Mon 14:00", instructor4);

            Course cs401 = new Course("CS401", "Advanced Algorithms", "4", "CS", "Sun 11:00", instructor1);
            Course cs402 = new Course("CS402", "Machine Learning", "4", "CS", "Wed 15:00", instructor3);
            Course is401 = new Course("IS401", "Enterprise Solutions", "4", "IS", "Thu 13:00", instructor4);

            // Add courses to instructors
            for (Course course : new Course[]{cs101, cs102, cs201, cs202, cs301, cs302, cs401, cs402}) {
                instructor1.getCourses().add(course);
                instructor3.getCourses().add(course);
            }
            for (Course course : new Course[]{is101, is201, is301, is401}) {
                instructor2.getCourses().add(course);
                instructor4.getCourses().add(course);
            }

            // Persist all courses
            for (Course course : new Course[]{cs101, cs102, is101, cs201, cs202, is201, cs301, cs302, is301, cs401, cs402, is401}) {
                entityManager.persist(course);
            }

            // Enroll students in appropriate level courses
            // Level 1 students in Level 1 courses
            cs101.getStudents().addAll(java.util.Arrays.asList(s1, s2));
            s1.getCourses().add(cs101);
            s2.getCourses().add(cs101);

            cs102.getStudents().addAll(java.util.Arrays.asList(s2, s3));
            s2.getCourses().add(cs102);
            s3.getCourses().add(cs102);

            is101.getStudents().add(s3);
            s3.getCourses().add(is101);

            // Level 2 students in Level 2 courses
            cs201.getStudents().addAll(java.util.Arrays.asList(s4, s5, s6));
            s4.getCourses().add(cs201);
            s5.getCourses().add(cs201);
            s6.getCourses().add(cs201);

            cs202.getStudents().addAll(java.util.Arrays.asList(s4, s6));
            s4.getCourses().add(cs202);
            s6.getCourses().add(cs202);

            is201.getStudents().add(s5);
            s5.getCourses().add(is201);

            // Level 3 students in Level 3 courses
            cs301.getStudents().addAll(java.util.Arrays.asList(s8, s7));
            s8.getCourses().add(cs301);
            s7.getCourses().add(cs301);

            cs302.getStudents().add(s8);
            s8.getCourses().add(cs302);

            is301.getStudents().addAll(java.util.Arrays.asList(s7, s9));
            s7.getCourses().add(is301);
            s9.getCourses().add(is301);

            // Level 4 students in Level 4 courses
            cs401.getStudents().addAll(java.util.Arrays.asList(s10, s12));
            s10.getCourses().add(cs401);
            s12.getCourses().add(cs401);

            cs402.getStudents().add(s10);
            s10.getCourses().add(cs402);

            is401.getStudents().addAll(java.util.Arrays.asList(s11, s7));
            s11.getCourses().add(is401);
            s7.getCourses().add(is401);

            // Create multiple quizzes for each course
            // CS101 Quizzes
            Quiz cs101_quiz1 = new Quiz("Java Basics Quiz", cs101);
            Question q1 = new Question(cs101_quiz1, "Which keyword is used to inherit a class in Java?", "implement", "extends", "inherits", "instanceof", 2);
            Question q2 = new Question(cs101_quiz1, "Which collection stores unique values?", "List", "Map", "Set", "Queue", 3);
            Question q3 = new Question(cs101_quiz1, "What is the default value of a boolean variable?", "true", "false", "null", "0", 2);
            cs101_quiz1.getQuestions().addAll(java.util.Arrays.asList(q1, q2, q3));
            cs101.getQuizzes().add(cs101_quiz1);
            entityManager.persist(cs101_quiz1);
            entityManager.persist(q1);
            entityManager.persist(q2);
            entityManager.persist(q3);

            Quiz cs101_quiz2 = new Quiz("String Manipulation", cs101);
            Question q4 = new Question(cs101_quiz2, "Which method converts a string to uppercase?", "toUpper()", "toUpperCase()", "upper()", "convertUpper()", 2);
            Question q5 = new Question(cs101_quiz2, "What is the index of the first character in a string?", "1", "0", "-1", "first", 2);
            cs101_quiz2.getQuestions().addAll(java.util.Arrays.asList(q4, q5));
            cs101.getQuizzes().add(cs101_quiz2);
            entityManager.persist(cs101_quiz2);
            entityManager.persist(q4);
            entityManager.persist(q5);

            // CS201 Quizzes
            Quiz cs201_quiz1 = new Quiz("Linked Lists", cs201);
            Question q6 = new Question(cs201_quiz1, "What is the time complexity of accessing an element in a linked list?", "O(1)", "O(n)", "O(log n)", "O(n log n)", 2);
            Question q7 = new Question(cs201_quiz1, "Which linear data structure uses FIFO?", "Stack", "Queue", "Array", "LinkedList", 2);
            cs201_quiz1.getQuestions().addAll(java.util.Arrays.asList(q6, q7));
            cs201.getQuizzes().add(cs201_quiz1);
            entityManager.persist(cs201_quiz1);
            entityManager.persist(q6);
            entityManager.persist(q7);

            // IS201 Quiz
            Quiz is201_quiz1 = new Quiz("System Analysis Basics", is201);
            Question q8 = new Question(is201_quiz1, "What is the first phase of SDLC?", "Design", "Planning", "Analysis", "Testing", 2);
            is201_quiz1.getQuestions().add(q8);
            is201.getQuizzes().add(is201_quiz1);
            entityManager.persist(is201_quiz1);
            entityManager.persist(q8);

            // Create quiz results for students
            QuizResult result1 = new QuizResult(s1, cs101_quiz1, 85);
            QuizResult result2 = new QuizResult(s2, cs101_quiz1, 92);
            QuizResult result3 = new QuizResult(s1, cs101_quiz2, 78);
            QuizResult result4 = new QuizResult(s4, cs201_quiz1, 88);
            QuizResult result5 = new QuizResult(s5, is201_quiz1, 95);

            for (QuizResult result : new QuizResult[]{result1, result2, result3, result4, result5}) {
                entityManager.persist(result);
            }

            // Add results to students and quizzes
            s1.getQuizResults().addAll(java.util.Arrays.asList(result1, result3));
            s2.getQuizResults().add(result2);
            s4.getQuizResults().add(result4);
            s5.getQuizResults().add(result5);

            cs101_quiz1.getQuizResults().addAll(java.util.Arrays.asList(result1, result2));
            cs101_quiz2.getQuizResults().add(result3);
            cs201_quiz1.getQuizResults().add(result4);
            is201_quiz1.getQuizResults().add(result5);

            // Create quiz answers
            QuizAnswer a1 = new QuizAnswer(result1, q1, "extends");
            QuizAnswer a2 = new QuizAnswer(result1, q2, "Set");
            QuizAnswer a3 = new QuizAnswer(result1, q3, "false");
            result1.getQuizAnswers().addAll(java.util.Arrays.asList(a1, a2, a3));
            q1.getQuizAnswers().add(a1);
            q2.getQuizAnswers().add(a2);
            q3.getQuizAnswers().add(a3);
            entityManager.persist(a1);
            entityManager.persist(a2);
            entityManager.persist(a3);

            return null;
        });
    }
}

