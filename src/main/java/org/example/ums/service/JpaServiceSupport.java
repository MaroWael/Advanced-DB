package org.example.ums.service;

import org.example.ums.dao.JpaUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.function.Function;

public abstract class JpaServiceSupport {

    protected <R> R execute(Function<EntityManager, R> action) {
        EntityManager entityManager = JpaUtil.createEntityManager();
        try {
            return action.apply(entityManager);
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    protected <R> R executeInTransaction(Function<EntityManager, R> action) {
        EntityManager entityManager = JpaUtil.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            transaction.begin();
            R result = action.apply(entityManager);
            transaction.commit();
            return result;
        } catch (RuntimeException exception) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw exception;
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }
}

