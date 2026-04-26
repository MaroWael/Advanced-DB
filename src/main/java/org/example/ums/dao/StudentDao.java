package org.example.ums.dao;

import org.example.ums.entity.Student;

public class StudentDao extends AbstractJpaDao<Student, Integer> {

    public StudentDao() {
        super(Student.class);
    }
}

