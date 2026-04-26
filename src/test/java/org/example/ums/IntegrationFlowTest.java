package org.example.ums;

import org.example.ums.dao.DaoRegistry;
import org.example.ums.dao.JpaUtil;
import org.example.ums.entity.Course;
import org.example.ums.entity.Instructor;
import org.example.ums.entity.Question;
import org.example.ums.entity.Quiz;
import org.example.ums.entity.QuizResult;
import org.example.ums.entity.Student;
import org.example.ums.entity.enums.Department;
import org.example.ums.service.AcademicManagementService;
import org.example.ums.service.AcademicQueryService;
import org.example.ums.service.DataBootstrapService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

public class IntegrationFlowTest {

    private static final AcademicManagementService managementService = new AcademicManagementService();
    private static final AcademicQueryService queryService = new AcademicQueryService();

    @BeforeAll
    static void setup() {
        System.setProperty("ums.persistence.unit", "umsTestPU");
        new DataBootstrapService().seedIfEmpty();
    }

    @AfterAll
    static void shutdown() {
        JpaUtil.shutdown();
    }

    @Test
    void shouldExecuteCrudAndQueriesAcrossLayers() {
        Instructor instructor = DaoRegistry.INSTRUCTORS.findAll().stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("Missing seeded instructor."));

        Student student = managementService.addNewStudent(
                new Student("Flow Student", "flow.student@ums.local", "flow123", 1, "CS", 2.9, Department.CS));
        Assertions.assertNotNull(student.getId());

        Course course = managementService.addNewCourse(
                new Course("TST01", "Testing Course", "1", "CS", "Mon 08:00", instructor));
        Assertions.assertEquals("TST01", course.getCode());

        Quiz quiz = managementService.addQuizWithQuestions(
                "TST01",
                new Quiz("Testing Quiz", null),
                Arrays.asList(
                        new Question(null, "JPA stands for?", "Java Persistence API", "Java Program API", "JSON Persistence API", "None", 1),
                        new Question(null, "JPQL is based on?", "Tables", "Entities", "Files", "XML", 2)
                ));
        Assertions.assertNotNull(quiz.getId());

        Assertions.assertTrue(managementService.enrollStudentInCourse(student.getId(), "TST01"));
        Assertions.assertTrue(managementService.updateStudentGrade(student.getId(), 3.7).isPresent());
        Assertions.assertTrue(managementService.updateCourseInstructor("TST01", instructor.getId()).isPresent());
        Assertions.assertTrue(managementService.removeStudentFromCourse(student.getId(), "TST01"));
        Assertions.assertTrue(managementService.deleteQuiz(quiz.getId()));

        Course seededCourse = DaoRegistry.COURSES.findAll().stream()
                .filter(existing -> !"TST01".equals(existing.getCode()))
                .findFirst()
                .orElse(course);

        List<Student> studentsInCourse = queryService.getAllStudentsInCourse(seededCourse.getCode());
        List<Course> coursesForStudent = queryService.getAllCoursesForStudent(student.getId());
        List<Quiz> quizzesForCourse = queryService.getQuizzesForCourse(seededCourse.getCode());

        Integer seededStudentId = DaoRegistry.STUDENTS.findAll().stream().findFirst().map(Student::getId)
                .orElseThrow(() -> new IllegalStateException("Missing seeded student."));
        List<QuizResult> results = queryService.getStudentQuizResults(seededStudentId);

        Assertions.assertNotNull(studentsInCourse);
        Assertions.assertNotNull(coursesForStudent);
        Assertions.assertNotNull(quizzesForCourse);
        Assertions.assertFalse(results.isEmpty());
        Assertions.assertFalse(queryService.getTopScoringStudents(5).isEmpty());

        if (!quizzesForCourse.isEmpty()) {
            Assertions.assertNotNull(queryService.getQuestionsForQuiz(quizzesForCourse.get(0).getId()));
        }
    }
}

