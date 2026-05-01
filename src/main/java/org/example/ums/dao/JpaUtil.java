package org.example.ums.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

import java.util.function.Function;
public final class JpaUtil {

    private static final String DEFAULT_PERSISTENCE_UNIT = "umsPU";
    private static final String PERSISTENCE_UNIT_PROPERTY = "ums.persistence.unit";
    private static volatile EntityManagerFactory entityManagerFactory;

    private JpaUtil() {
    }

    public static EntityManager createEntityManager() {
        return getEntityManagerFactory().createEntityManager();
    }

    public static <R> R execute(Function<EntityManager, R> action) {
        EntityManager entityManager = createEntityManager();
        try {
            return action.apply(entityManager);
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    public static <R> R executeInTransaction(Function<EntityManager, R> action) {
        EntityManager entityManager = createEntityManager();
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

    public static void shutdown() {
        EntityManagerFactory factory = entityManagerFactory;
        if (factory != null && factory.isOpen()) {
            factory.close();
            entityManagerFactory = null;
        }
    }

    private static EntityManagerFactory getEntityManagerFactory() {
        if (entityManagerFactory == null) {
            synchronized (JpaUtil.class) {
                if (entityManagerFactory == null) {
                    String unitName = System.getProperty(PERSISTENCE_UNIT_PROPERTY, DEFAULT_PERSISTENCE_UNIT);
                    entityManagerFactory = Persistence.createEntityManagerFactory(unitName);
                }
            }
        }
        return entityManagerFactory;
    }
}

