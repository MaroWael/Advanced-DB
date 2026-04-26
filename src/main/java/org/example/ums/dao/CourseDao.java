package org.example.ums.dao;

import org.example.ums.entity.Course;

public class CourseDao extends AbstractJpaDao<Course, String> {

    public CourseDao() {
        super(Course.class);
    }
}

