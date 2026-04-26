package org.example.ums.dao;

import org.example.ums.entity.QuizResult;

public class QuizResultDao extends AbstractJpaDao<QuizResult, Integer> {

    public QuizResultDao() {
        super(QuizResult.class);
    }
}

