package com.plantssoil.common.jpa.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import com.plantssoil.common.jpa.IPersistence;

/**
 * Default implementation of IPersistence for JPA<br/>
 * Can save entity, can query entity, can query database via Named Query or
 * Native Query with entity manager<br/>
 * Used for persisting entity properties into RDBMS<br/>
 * 
 * @author danial
 *
 */
public class DefaultPersistence implements IPersistence {
	private EntityManager entityManager;

	@Override
	public void create(Object entity) {
		entityManager.getTransaction().begin();
		entityManager.persist(entity);
		entityManager.getTransaction().commit();
	}

	@Override
	public void create(List<?> entities) {
		entityManager.getTransaction().begin();
		for (Object entity : entities) {
			entityManager.persist(entity);
		}
		entityManager.getTransaction().commit();
	}

	@Override
	public <T> T update(T entity) {
		entityManager.getTransaction().begin();
		T updated = entityManager.merge(entity);
		entityManager.getTransaction().commit();
		return updated;
	}

	@Override
	public <T> List<T> update(List<T> entities) {
		List<T> updated = new ArrayList<>();
		entityManager.getTransaction().begin();
		for (T entity : entities) {
			updated.add(entityManager.merge(entity));
		}
		entityManager.getTransaction().commit();
		return updated;
	}

	@Override
	public void remove(Object entity) {
		entityManager.getTransaction().begin();
		entityManager.remove(entityManager.merge(entity));
		entityManager.getTransaction().commit();
	}

	@Override
	public void remove(List<?> entities) {
		entityManager.getTransaction().begin();
		for (Object entity : entities) {
			entityManager.remove(entityManager.merge(entity));
		}
		entityManager.getTransaction().commit();
	}

	@Override
	public void close() throws Exception {
		if (entityManager.getTransaction().isActive()) {
			entityManager.getTransaction().rollback();
		}
		if (this.entityManager.isOpen()) {
			this.entityManager.close();
		}
	}

	@Override
	public void setEntityManager(EntityManager em) {
		this.entityManager = em;
	}

	@Override
	public EntityManager getEntityManager() {
		return this.entityManager;
	}

}
