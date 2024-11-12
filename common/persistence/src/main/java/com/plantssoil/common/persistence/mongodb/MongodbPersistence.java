package com.plantssoil.common.persistence.mongodb;

import java.util.List;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.connection.ClusterType;
import com.plantssoil.common.persistence.IEntityQuery;
import com.plantssoil.common.persistence.IPersistence;

/**
 * The IPersistence default implementation base on Mongodb<br/>
 * Could CURD all collections base on the entities provided<br/>
 * 
 * @author danialdy
 * @Date 9 Nov 2024 10:44:33 am
 */
class MongodbPersistence implements IPersistence {
    private MongoClient mongoClient;
    private ClientSession clientSession;
    private MongoDatabase mongoDatabase;

    protected MongodbPersistence(MongoClient mongoclient) {
        this.mongoClient = mongoclient;
    }

    private ClientSession getClientSession() {
        ClusterType ct = this.mongoClient.getClusterDescription().getType();
        // Mongodb transaction only can run under "replica set" or "shared cluster"
        if (!(ClusterType.REPLICA_SET == ct || ClusterType.SHARDED == ct)) {
            return null;
        }

        if (this.clientSession == null) {
            this.clientSession = this.mongoClient.startSession();
        }
        if (!this.clientSession.hasActiveTransaction()) {
            this.clientSession.startTransaction();
        }
        return this.clientSession;
    }

    private MongoDatabase getMongoDatabase() {
        if (this.mongoDatabase == null) {
            this.mongoDatabase = mongoClient.getDatabase(MongodbPersistenceFactory.DATABASE_NAME);
        }
        return this.mongoDatabase;
    }

    @Override
    public void close() throws Exception {
        if (this.clientSession != null) {
            if (!this.clientSession.hasActiveTransaction()) {
                this.clientSession.close();
                return;
            }
            try {
                this.clientSession.commitTransaction();
            } catch (Exception e) {
                if (this.clientSession.hasActiveTransaction()) {
                    this.clientSession.abortTransaction();
                }
                throw e;
            } finally {
                this.clientSession.close();
            }
        }
    }

    @Override
    public void create(Object entity) {
        if (entity == null) {
            return;
        }
        IMongodbOperation operation = IMongodbOperation.createInstance(getMongoDatabase(), entity);
        operation.create(getClientSession());
    }

    @Override
    public void create(List<?> entities) {
        if (entities == null || entities.size() == 0) {
            return;
        }

        for (Object entity : entities) {
            IMongodbOperation operation = IMongodbOperation.createInstance(getMongoDatabase(), entity);
            operation.create(getClientSession());
        }
    }

    @Override
    public <T> T update(T entity) {
        if (entity == null) {
            return null;
        }
        IMongodbOperation operation = IMongodbOperation.createInstance(getMongoDatabase(), entity);
        operation.update(getClientSession());
        return entity;
    }

    @Override
    public List<?> update(List<?> entities) {
        if (entities == null || entities.size() == 0) {
            return entities;
        }
        for (Object entity : entities) {
            IMongodbOperation operation = IMongodbOperation.createInstance(getMongoDatabase(), entity);
            operation.update(getClientSession());
        }
        return entities;
    }

    @Override
    public void remove(Object entity) {
        if (entity == null) {
            return;
        }
        IMongodbOperation operation = IMongodbOperation.createInstance(getMongoDatabase(), entity);
        operation.remove(getClientSession());
    }

    @Override
    public void remove(List<?> entities) {
        if (entities == null || entities.size() == 0) {
            return;
        }
        for (Object entity : entities) {
            IMongodbOperation operation = IMongodbOperation.createInstance(getMongoDatabase(), entity);
            operation.remove(getClientSession());
        }
    }

    @Override
    public <T> IEntityQuery<T> createQuery(Class<T> entityClass) {
        return new MongodbQuery<T>(getMongoDatabase(), entityClass);
    }

}
