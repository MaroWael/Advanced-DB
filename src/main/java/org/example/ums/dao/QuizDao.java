package org.example.ums.dao;

import org.example.ums.entity.Quiz;

public class QuizDao extends AbstractJpaDao<Quiz, Integer> {

    public QuizDao() {
        super(Quiz.class);
    }
}

