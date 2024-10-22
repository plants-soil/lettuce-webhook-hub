package com.plantssoil.common.jpa;

import java.util.List;

import javax.persistence.EntityManager;

import com.plantssoil.common.config.IConfigurable;

/**
 * Persistence via JPA technical stack<br/>
 * Could create / update / remove JPA entity<br/>
 * Could create EntityManager for entity query, or more complex transaction
 * management
 * 
 * @author danialdy
 *
 */
public interface IPersistence extends IConfigurable, AutoCloseable {
	/**
	 * Create and persists entity in database
	 * 
	 * @param entity the entity to be persisted
	 */
	public void create(Object entity);

	/**
	 * Create and persists entities in database
	 * 
	 * @param entities the entities to be persisted
	 * 
	 */
	public void create(List<?> entities);

	/**
	 * Update and persists entity in database
	 * 
	 * @param <T>    Entity
	 * @param entity the entity to be persisted
	 * @return the entity persisted
	 */
	public <T> T update(T entity);

	/**
	 * Update and persists entities in database
	 * 
	 * @param <T>      Entity
	 * @param entities the entities to be persisted
	 * @return the entities persisted
	 */
	public <T> List<T> update(List<T> entities);

	/**
	 * Delete entity from database
	 * 
	 * @param entity entity to be deleted
	 */
	public void remove(Object entity);

	/**
	 * Delete entities from database
	 * 
	 * @param entities entities to be deleted
	 */
	public void remove(List<?> entities);

	/**
	 * give entity manager instance to the persistence<br/>
	 * persistence factory will call this method, persistence could implement
	 * complex logic via entity manager
	 * 
	 * @param em entity manager
	 */
	public void setEntityManager(EntityManager em);

	/**
	 * Get entity manager
	 * 
	 * @return the entity manager
	 */
	public EntityManager getEntityManager();
}
