package org.example.ums.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.criteria.CriteriaQuery;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public abstract class AbstractJpaDao<T, ID> implements GenericDao<T, ID> {

    private final Class<T> entityClass;

    protected AbstractJpaDao(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    @Override
    public T save(T entity) {
        return executeInTransaction(entityManager -> {
            entityManager.persist(entity);
            return entity;
        });
    }

    @Override
    public T update(T entity) {
        return executeInTransaction(entityManager -> entityManager.merge(entity));
    }

    @Override
    public void delete(T entity) {
        executeInTransaction(entityManager -> {
            T managedEntity = entityManager.contains(entity) ? entity : entityManager.merge(entity);
            entityManager.remove(managedEntity);
            return null;
        });
    }

    @Override
    public Optional<T> findById(ID id) {
        return execute(entityManager -> Optional.ofNullable(entityManager.find(entityClass, id)));
    }

    @Override
    public List<T> findAll() {
        return execute(entityManager -> {
            CriteriaQuery<T> criteriaQuery = entityManager.getCriteriaBuilder().createQuery(entityClass);
            criteriaQuery.select(criteriaQuery.from(entityClass));
            return entityManager.createQuery(criteriaQuery).getResultList();
        });
    }

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


