package com.plantssoil.common.persistence.mongodb;

import org.bson.Document;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;

/**
 * Mongodb operation: the operation base on the specific Mongodb Document in
 * Mongodb Collection
 * 
 * @author danialdy
 * @Date 9 Nov 2024 12:19:39 pm
 */
public interface IMongodbOperation {

    /**
     * Get the collection which document belongs to
     * 
     * @return Mongodb collection
     */
    public MongoCollection<Document> getMongoCollection();

    /**
     * Get the document to operate
     * 
     * @return Mongodb document
     */
    public Document getDocument();

    /**
     * Create document
     * 
     * @param clientSession not null - will put this transaction into the
     *                      transaction, null - keep this transaction standalone
     * @return result of insert operation
     */
    public InsertOneResult create(ClientSession clientSession);

    /**
     * Update document
     * 
     * @param clientSession not null - will put this transaction into the
     *                      transaction, null - keep this transaction standalone
     * @return result of update operation
     */
    public UpdateResult update(ClientSession clientSession);

    /**
     * Delete document
     * 
     * @param clientSession not null - will put this transaction into the
     *                      transaction, null - keep this transaction standalone
     * @return result of delete operation
     */
    public DeleteResult remove(ClientSession clientSession);

    /**
     * Create default instance of IMongodbOperation
     * 
     * @param mongoDatabase Mongodb database instance
     * @param entity        entity object which will operate
     * @param operationType the operation type (create, update,delete)
     * @return IMongodbOperation instance
     */
    public static IMongodbOperation createInstance(MongoDatabase mongoDatabase, Object entity) {
        return new MongodbOperation(mongoDatabase, entity);
    }
}
