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
class JPAPersistence implements IPersistence {
    private EntityManager entityManager;

    private EntityManager getEntityManager() {
        EntityTransaction tx = entityManager.getTransaction();
        if (!tx.isActive()) {
            tx.begin();
        }
        return entityManager;
    }

    @Override
    public void create(Object entity) {
        if (entity == null) {
            return;
        }

        EntityManager em = this.getEntityManager();
        em.persist(entity);
    }

    @Override
    public void create(List<?> entities) {
        if (entities == null || entities.size() == 0) {
            return;
        } else if (entities.size() == 1) {
            this.create(entities.get(0));
        }

        EntityManager em = this.getEntityManager();
        for (Object entity : entities) {
            em.persist(entity);
        }
    }

    @Override
    public <T> T update(T entity) {
        if (entity == null) {
            return null;
        }
        EntityManager em = this.getEntityManager();
        T updated = em.merge(entity);
        return updated;
    }

    @Override
    public List<?> update(List<?> entities) {
        if (entities == null || entities.size() == 0) {
            return entities;
        }

        EntityManager em = this.getEntityManager();
        List<Object> updated = new ArrayList<>();
        for (Object entity : entities) {
            updated.add(em.merge(entity));
        }
        return updated;
    }

    @Override
    public void remove(Object entity) {
        if (entity == null) {
            return;
        }
        EntityManager em = this.getEntityManager();
        em.remove(em.merge(entity));
    }

    @Override
    public void remove(List<?> entities) {
        if (entities == null || entities.size() == 0) {
            return;
        } else if (entities.size() == 1) {
            this.remove(entities.get(0));
        }
        EntityManager em = this.getEntityManager();
        for (Object entity : entities) {
            em.remove(em.merge(entity));
        }
    }

    @Override
    public void close() throws Exception {
        EntityTransaction tx = entityManager.getTransaction();
        if (tx.isActive()) {
            try {
                tx.commit();
            } catch (Exception e) {
                tx.rollback();
                throw e;
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
    protected void setEntityManager(EntityManager em) {
        this.entityManager = em;
    }

}
