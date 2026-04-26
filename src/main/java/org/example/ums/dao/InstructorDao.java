package org.example.ums.dao;

import org.example.ums.entity.Instructor;

public class InstructorDao extends AbstractJpaDao<Instructor, Integer> {

    public InstructorDao() {
        super(Instructor.class);
    }
}

