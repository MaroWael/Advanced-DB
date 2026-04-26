package org.example.ums.entity;

import org.example.ums.entity.enums.Department;
import org.example.ums.entity.enums.Role;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "students")
@PrimaryKeyJoinColumn(name = "user_id")
public class Student extends User {

    private Integer level;

    @Column(length = 100)
    private String major;

    private Double grade;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Department department;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<QuizResult> quizResults = new HashSet<>();

    @ManyToMany(mappedBy = "students")
    private Set<Course> courses = new HashSet<>();

    public Student() {
        setRole(Role.STUDENT);
    }

    public Student(String name, String email, String password, Integer level, String major, Double grade, Department department) {
        super(name, email, password, Role.STUDENT);
        this.level = level;
        this.major = major;
        this.grade = grade;
        this.department = department;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public Double getGrade() {
        return grade;
    }

    public void setGrade(Double grade) {
        this.grade = grade;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public Set<QuizResult> getQuizResults() {
        return quizResults;
    }

    public void setQuizResults(Set<QuizResult> quizResults) {
        this.quizResults = quizResults;
    }

    public Set<Course> getCourses() {
        return courses;
    }

    public void setCourses(Set<Course> courses) {
        this.courses = courses;
    }
}

