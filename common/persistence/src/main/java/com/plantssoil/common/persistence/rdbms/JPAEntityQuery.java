package com.plantssoil.common.persistence.rdbms;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.plantssoil.common.persistence.IEntityQuery;
import com.plantssoil.common.persistence.SimpleFilter;
import com.plantssoil.common.persistence.exception.PersistenceException;

/**
 * JPA implementation for IEntityQuery
 * 
 * @param <T> entity type
 * @author danialdy
 * @Date 31 Oct 2024 7:31:56 pm
 */
class JPAEntityQuery<T> implements IEntityQuery<T> {
    private List<SimpleFilter> filters = new ArrayList<>();
    private Class<?> entityClass;
    private int startPosition = 0;
    private int maxResult = 20;
    private EntityManager entityManager;

    protected JPAEntityQuery(EntityManager entityManager, Class<T> entityClass) {
        this.entityManager = entityManager;
        this.entityClass = entityClass;
    }

    @Override
    public IEntityQuery<T> filter(String attributeName, FilterOperator operator, Object filterValue) {
        filters.add(new SimpleFilter(attributeName, operator, filterValue));
        return this;
    }

    @Override
    public IEntityQuery<T> firstResult(int startPosition) {
        this.startPosition = startPosition;
        return this;
    }

    @Override
    public IEntityQuery<T> maxResults(int maxResult) {
        this.maxResult = maxResult;
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public CompletableFuture<T> singleResult(Object primaryKey) {
        CompletableFuture<T> future = CompletableFuture.supplyAsync(() -> {
            return (T) entityManager.find(this.entityClass, primaryKey);
        }).exceptionally(ex -> {
            throw new PersistenceException(PersistenceException.BUSINESS_EXCEPTION_CODE_13012, ex);
        });
        return future;
    }

    @Override
    public CompletableFuture<T> singleResult() {
        CompletableFuture<T> future = CompletableFuture.supplyAsync(() -> {
            TypedQuery<T> query = getTypedQuery();
            query.setFirstResult(0).setMaxResults(1);
            List<T> results = query.getResultList();
            if (results.size() == 0) {
                return null;
            } else {
                return results.get(0);
            }
        }).exceptionally(ex -> {
            throw new PersistenceException(PersistenceException.BUSINESS_EXCEPTION_CODE_13012, ex);
        });
        return future;
    }

    @Override
    public CompletableFuture<List<T>> resultList() {
        CompletableFuture<List<T>> future = CompletableFuture.supplyAsync(() -> {
            TypedQuery<T> query = getTypedQuery();
            query.setFirstResult(this.startPosition).setMaxResults(this.maxResult);
            return query.getResultList();
        }).exceptionally(ex -> {
            throw new PersistenceException(PersistenceException.BUSINESS_EXCEPTION_CODE_13012, ex);
        });
        return future;
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public <D> List<D> distinct(Class<D> clazz, String fieldName) {
        CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<D> q = cb.createQuery(clazz);
        Root<T> r = (Root<T>) q.from(this.entityClass);
        Predicate predicateAll = null;
        for (int i = 0; i < this.filters.size(); i++) {
            SimpleFilter filter = this.filters.get(i);
            Path<Object> path = r.get(filter.getAttributeName());
            ParameterExpression<?> param = cb.parameter(filter.getFilterValue().getClass(), filter.getAttributeName());
            Predicate predicate = null;
            if (filter.getOperator() == FilterOperator.equals) {
                predicate = cb.equal(path, param);
            } else if (filter.getOperator() == FilterOperator.like) {
                predicate = cb.like((Expression) path, (Expression) param);
            } else if (filter.getOperator() == FilterOperator.in) {
                predicate = path.in(param);
            }
            if (i == 0) {
                predicateAll = predicate;
            } else {
                predicateAll = cb.and(predicateAll, predicate);
            }
        }
        if (predicateAll != null) {
            q.where(predicateAll);
        }
        q.select(r.get(fieldName)).distinct(true);
        TypedQuery<D> query = this.entityManager.createQuery(q);
        for (int i = 0; i < this.filters.size(); i++) {
            SimpleFilter filter = this.filters.get(i);
            query.setParameter(filter.getAttributeName(), filter.getFilterValue());
        }
        query.setFirstResult(this.startPosition).setMaxResults(this.maxResult);
        return query.getResultList();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private TypedQuery<T> getTypedQuery() {
        CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<T> q = (CriteriaQuery<T>) cb.createQuery(this.entityClass);
        Root<T> r = (Root<T>) q.from(this.entityClass);
        Predicate predicateAll = null;
        for (int i = 0; i < this.filters.size(); i++) {
            SimpleFilter filter = this.filters.get(i);
            Path<Object> path = r.get(filter.getAttributeName());
            ParameterExpression<?> param = cb.parameter(filter.getFilterValue().getClass(), filter.getAttributeName());
            Predicate predicate = null;
            if (filter.getOperator() == FilterOperator.equals) {
                predicate = cb.equal(path, param);
            } else if (filter.getOperator() == FilterOperator.like) {
                predicate = cb.like((Expression) path, (Expression) param);
            } else if (filter.getOperator() == FilterOperator.in) {
                predicate = path.in(param);
            }
            if (i == 0) {
                predicateAll = predicate;
            } else {
                predicateAll = cb.and(predicateAll, predicate);
            }
        }
        if (predicateAll != null) {
            q.where(predicateAll);
        }

        q.select(r);
        TypedQuery<T> query = this.entityManager.createQuery(q);
        for (int i = 0; i < this.filters.size(); i++) {
            SimpleFilter filter = this.filters.get(i);
            query.setParameter(filter.getAttributeName(), filter.getFilterValue());
        }
        return query;
    }

}
