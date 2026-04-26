package org.example.ums.service;

import org.example.ums.entity.Course;
import org.example.ums.entity.Question;
import org.example.ums.entity.Quiz;
import org.example.ums.entity.QuizResult;
import org.example.ums.entity.Student;
import org.example.ums.service.dto.StudentScoreView;

import java.util.List;

public class AcademicQueryService extends JpaServiceSupport {

    public List<Student> getAllStudentsInCourse(String courseCode) {
        return execute(entityManager -> entityManager.createQuery(
                        "select s from Course c join c.students s where c.code = :courseCode order by s.name",
                        Student.class)
                .setParameter("courseCode", courseCode)
                .getResultList());
    }

    public List<Course> getAllCoursesForStudent(Integer studentId) {
        return execute(entityManager -> entityManager.createQuery(
                        "select c from Student s join s.courses c where s.id = :studentId order by c.code",
                        Course.class)
                .setParameter("studentId", studentId)
                .getResultList());
    }

    public List<Course> getAvailableCoursesForStudent(Integer studentId) {
        return execute(entityManager -> entityManager.createQuery(
                        "select c from Course c where c.code not in " +
                                "(select ec.code from Student s join s.courses ec where s.id = :studentId) " +
                                "order by c.code",
                        Course.class)
                .setParameter("studentId", studentId)
                .getResultList());
    }

    public List<Course> getCoursesForInstructor(Integer instructorId) {
        return execute(entityManager -> entityManager.createQuery(
                        "select c from Course c join c.instructor i where i.id = :instructorId order by c.code",
                        Course.class)
                .setParameter("instructorId", instructorId)
                .getResultList());
    }

    public List<Quiz> getQuizzesForCourse(String courseCode) {
        return execute(entityManager -> entityManager.createQuery(
                        "select q from Quiz q join q.course c where c.code = :courseCode order by q.id",
                        Quiz.class)
                .setParameter("courseCode", courseCode)
                .getResultList());
    }

    public List<Quiz> getQuizzesForStudentInCourse(Integer studentId, String courseCode) {
        return execute(entityManager -> entityManager.createQuery(
                        "select q from Student s join s.courses c join c.quizzes q " +
                                "where s.id = :studentId and c.code = :courseCode order by q.id",
                        Quiz.class)
                .setParameter("studentId", studentId)
                .setParameter("courseCode", courseCode)
                .getResultList());
    }

    public List<QuizResult> getStudentQuizResults(Integer studentId) {
        return execute(entityManager -> entityManager.createQuery(
                        "select qr from QuizResult qr join qr.student s where s.id = :studentId order by qr.id desc",
                        QuizResult.class)
                .setParameter("studentId", studentId)
                .getResultList());
    }

    public List<StudentScoreView> getTopScoringStudents(int limit) {
        return execute(entityManager -> entityManager.createQuery(
                        "select new org.example.ums.service.dto.StudentScoreView(s.id, s.name, avg(qr.score), count(qr.id)) " +
                                "from QuizResult qr join qr.student s " +
                                "group by s.id, s.name " +
                                "order by avg(qr.score) desc",
                        StudentScoreView.class)
                .setMaxResults(limit)
                .getResultList());
    }

    public List<Question> getQuestionsForQuiz(Integer quizId) {
        return execute(entityManager -> entityManager.createQuery(
                        "select qn from Question qn join qn.quiz q where q.id = :quizId order by qn.id",
                        Question.class)
                .setParameter("quizId", quizId)
                .getResultList());
    }

    public List<Question> getQuestionsForStudentQuiz(Integer studentId, Integer quizId) {
        return execute(entityManager -> entityManager.createQuery(
                        "select qn from Student s " +
                                "join s.courses c " +
                                "join c.quizzes q " +
                                "join q.questions qn " +
                                "where s.id = :studentId and q.id = :quizId " +
                                "order by qn.id",
                        Question.class)
                .setParameter("studentId", studentId)
                .setParameter("quizId", quizId)
                .getResultList());
    }
}

