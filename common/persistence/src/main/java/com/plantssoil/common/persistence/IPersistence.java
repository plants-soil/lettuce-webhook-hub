package com.plantssoil.common.persistence;

import java.util.List;

/**
 * Persistence interface<br/>
 * Could create / update / remove entity from persistence (SQL or NOSQL)<br/>
 * IPersistence extends {@link AutoCloseable}, so could put IPersistence into
 * try (...) clause, otherwise need call {@link IPersistence#close()} in finally
 * block<br/>
 * <blockquote>
 * 
 * <pre>
 * try (IPersistence persists = IPersistenceFactory.getDefaultFactory().create()) {
 *   ...do CRUD, or query data
 * }
 * </pre>
 * 
 * or
 * 
 * <pre>
 * IPersistence persists = IPersistenceFactory.getDefaultFactory().create();
 * try {
 *   ...do CRUD, or query data
 * }
 * finally {
 *   persists.close();
 * }
 * </pre>
 * 
 * </blockquote>
 * 
 * @author danialdy
 *
 */
public interface IPersistence extends AutoCloseable {
    /**
     * Create and persists entity in persistence
     * 
     * @param entity the entity to be persisted
     */
    public void create(Object entity);

    /**
     * Create and persists entities in persistence
     * 
     * @param entities the entities to be persisted
     * 
     */
    public void create(List<?> entities);

    /**
     * Update and persists entity in persistence
     * 
     * @param <T>    Entity
     * @param entity the entity to be persisted
     * @return the entity persisted
     */
    public <T> T update(T entity);

    /**
     * Update and persists entities in persistence
     * 
     * @param <T>      Entity
     * @param entities the entities to be persisted
     * @return the entities persisted
     */
    public <T> List<T> update(List<T> entities);

    /**
     * Delete entity from persistence
     * 
     * @param entity entity to be deleted
     */
    public void remove(Object entity);

    /**
     * Delete entities from persistence
     * 
     * @param entities entities to be deleted
     */
    public void remove(List<?> entities);

    /**
     * Create query to retrieve entities with filters
     * 
     * @param <T>         Entity Type
     * @param entityClass class of entity
     * @return entity query instance
     */
    public <T> IEntityQuery<T> createQuery(Class<T> entityClass);
}
