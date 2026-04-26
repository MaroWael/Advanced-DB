package org.example.ums.dao;

import org.example.ums.entity.Question;

public class QuestionDao extends AbstractJpaDao<Question, Integer> {

    public QuestionDao() {
        super(Question.class);
    }
}

