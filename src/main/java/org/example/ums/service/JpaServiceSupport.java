package org.example.ums.service;

import org.example.ums.dao.JpaUtil;

import jakarta.persistence.EntityManager;

import java.util.function.Function;

public abstract class JpaServiceSupport {

    protected <R> R execute(Function<EntityManager, R> action) {
        return JpaUtil.execute(action);
    }

    protected <R> R executeInTransaction(Function<EntityManager, R> action) {
        return JpaUtil.executeInTransaction(action);
    }
}

