package com.plantssoil.common.persistence.rdbms;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import com.plantssoil.common.persistence.IEntityQuery;
import com.plantssoil.common.persistence.IPersistence;

/**
 * IPersistence implementation of JPA<br/>
 * Can save entity, can query entity, can query database via Named Query or
 * Native Query with entity manager<br/>
 * Used for persisting entity into RDBMS<br/>
 * 
 * @author danial
 *
 */
public class JPAPersistence implements IPersistence {
    private EntityManager entityManager;

    @Override
    public void create(Object entity) {
        EntityTransaction tx = entityManager.getTransaction();
        if (!tx.isActive()) {
            tx.begin();
        }
        entityManager.persist(entity);
    }

    @Override
    public void create(List<?> entities) {
        EntityTransaction tx = entityManager.getTransaction();
        if (!tx.isActive()) {
            tx.begin();
        }
        for (Object entity : entities) {
            entityManager.persist(entity);
        }
    }

    @Override
    public <T> T update(T entity) {
        EntityTransaction tx = entityManager.getTransaction();
        if (!tx.isActive()) {
            tx.begin();
        }
        T updated = entityManager.merge(entity);
        return updated;
    }

    @Override
    public <T> List<T> update(List<T> entities) {
        EntityTransaction tx = entityManager.getTransaction();
        if (!tx.isActive()) {
            tx.begin();
        }
        List<T> updated = new ArrayList<>();
        for (T entity : entities) {
            updated.add(entityManager.merge(entity));
        }
        return updated;
    }

    @Override
    public void remove(Object entity) {
        EntityTransaction tx = entityManager.getTransaction();
        if (!tx.isActive()) {
            tx.begin();
        }
        entityManager.remove(entityManager.merge(entity));
    }

    @Override
    public void remove(List<?> entities) {
        EntityTransaction tx = entityManager.getTransaction();
        if (!tx.isActive()) {
            tx.begin();
        }
        for (Object entity : entities) {
            entityManager.remove(entityManager.merge(entity));
        }
    }

    @Override
    public void close() throws Exception {
        EntityTransaction tx = entityManager.getTransaction();
        if (tx.isActive()) {
            try {
                tx.commit();
            } finally {
                if (this.entityManager.isOpen()) {
                    this.entityManager.close();
                }
            }
        }
        if (this.entityManager.isOpen()) {
            this.entityManager.close();
        }
    }

    @Override
    public <T> IEntityQuery<T> createQuery(Class<T> entityClass) {
        return new JPAEntityQuery<T>(this.entityManager, entityClass);
    }

    /**
     * set JPA entity manager
     * 
     * @param em entity manager instance
     */
    public void setEntityManager(EntityManager em) {
        this.entityManager = em;
    }

    /**
     * get JPA entity manager instance
     * 
     * @return entity manager
     */
    public EntityManager getEntityManager() {
        return this.entityManager;
    }

}
