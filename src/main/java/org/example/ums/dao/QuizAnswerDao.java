package org.example.ums.dao;

import org.example.ums.entity.QuizAnswer;

public class QuizAnswerDao extends AbstractJpaDao<QuizAnswer, Integer> {

    public QuizAnswerDao() {
        super(QuizAnswer.class);
    }
}

