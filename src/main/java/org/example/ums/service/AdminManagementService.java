package org.example.ums.service;

import org.example.ums.entity.Course;
import org.example.ums.entity.Instructor;
import org.example.ums.entity.User;

import java.util.List;
import java.util.Optional;

public class AdminManagementService extends JpaServiceSupport {

    public User addUser(User user) {
        return executeInTransaction(entityManager -> {
            entityManager.persist(user);
            return user;
        });
    }

    public boolean deleteUser(Integer userId) {
        return executeInTransaction(entityManager -> {
            User user = entityManager.find(User.class, userId);
            if (user == null) {
                return false;
            }
            entityManager.remove(user);
            return true;
        });
    }

    public Course addCourse(Course course) {
        return executeInTransaction(entityManager -> {
            entityManager.persist(course);
            return course;
        });
    }

    public boolean deleteCourse(String courseCode) {
        return executeInTransaction(entityManager -> {
            Course course = entityManager.find(Course.class, courseCode);
            if (course == null) {
                return false;
            }
            entityManager.remove(course);
            return true;
        });
    }

    public List<User> getAllUsers() {
        return execute(entityManager -> entityManager.createQuery(
                        "select u from User u order by u.id",
                        User.class)
                .getResultList());
    }

    public List<Course> getAllCourses() {
        return execute(entityManager -> entityManager.createQuery(
                        "select c from Course c order by c.code",
                        Course.class)
                .getResultList());
    }

    public List<Instructor> getAllInstructors() {
        return execute(entityManager -> entityManager.createQuery(
                        "select i from Instructor i order by i.name",
                        Instructor.class)
                .getResultList());
    }

    public Optional<Instructor> findInstructorById(Integer instructorId) {
        return execute(entityManager -> Optional.ofNullable(entityManager.find(Instructor.class, instructorId)));
    }
}

