package com.plantssoil.common.persistence.mongodb;

import org.bson.Document;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import com.plantssoil.common.persistence.EntityUtils;
import com.plantssoil.common.persistence.exception.PersistenceException;

/**
 * The mongodb operation
 * 
 * @author danialdy
 * @Date 9 Nov 2024 12:32:14 pm
 */
class MongodbOperation implements IMongodbOperation {
    private Object entity;
    private MongoCollection<Document> mongoCollection;
    private Document mongoDocument;

    public MongodbOperation(MongoDatabase mongoDatabase, Object entity) {
        this.entity = entity;
        this.mongoCollection = mongoDatabase.getCollection(this.entity.getClass().getName());
        try {
            this.mongoDocument = Document.parse(new ObjectMapper().writeValueAsString(entity));
        } catch (JsonProcessingException e) {
            throw new PersistenceException(PersistenceException.BUSINESS_EXCEPTION_CODE_13013, e);
        }
    }

    @Override
    public MongoCollection<Document> getMongoCollection() {
        return this.mongoCollection;
    }

    @Override
    public Document getDocument() {
        return this.mongoDocument;
    }

    @Override
    public InsertOneResult create(ClientSession clientSession) {
        if (clientSession == null) {
            return this.getMongoCollection().insertOne(this.getDocument());
        } else {
            return this.getMongoCollection().insertOne(clientSession, this.getDocument());
        }
    }

    @Override
    public UpdateResult update(ClientSession clientSession) {
        String id = EntityUtils.getInstance().getEntityIdField(this.entity.getClass());
        if (clientSession == null) {
            return this.getMongoCollection().replaceOne(Filters.eq(id, mongoDocument.get(id)), mongoDocument);
        } else {
            return this.getMongoCollection().replaceOne(clientSession, Filters.eq(id, mongoDocument), mongoDocument);
        }
    }

    @Override
    public DeleteResult remove(ClientSession clientSession) {
        String id = EntityUtils.getInstance().getEntityIdField(this.entity.getClass());
        if (clientSession == null) {
            return this.getMongoCollection().deleteOne(Filters.eq(id, mongoDocument.get(id)));
        } else {
            return this.getMongoCollection().deleteOne(clientSession, Filters.eq(id, mongoDocument.get(id)));
        }
    }

}
