package org.example.ums.entity;

import org.example.ums.entity.enums.Department;
import org.example.ums.entity.enums.Role;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "instructors")
@PrimaryKeyJoinColumn(name = "user_id")
public class Instructor extends User {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Department department;

    @OneToMany(mappedBy = "instructor", cascade = CascadeType.ALL)
    private Set<Course> courses = new HashSet<>();

    public Instructor() {
        setRole(Role.INSTRUCTOR);
    }

    public Instructor(String name, String email, String password, Department department) {
        super(name, email, password, Role.INSTRUCTOR);
        this.department = department;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public Set<Course> getCourses() {
        return courses;
    }

    public void setCourses(Set<Course> courses) {
        this.courses = courses;
    }
}

