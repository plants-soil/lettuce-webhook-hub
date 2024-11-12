package com.plantssoil.common.persistence.mongodb;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.plantssoil.common.persistence.EntityIdUtility;
import com.plantssoil.common.persistence.IEntityQuery;
import com.plantssoil.common.persistence.SimpleFilter;
import com.plantssoil.common.persistence.exception.PersistenceException;

/**
 * Mongodb Query, used for Entity Query in Mongodb
 * 
 * @param <T> Entity to be query
 * @author danialdy
 * @Date 11 Nov 2024 9:18:52 am
 */
class MongodbQuery<T> implements IEntityQuery<T> {
    private MongoDatabase mongoDatabase;
    private Class<T> entityClass;
    private String mongoCollectionName;
    private List<SimpleFilter> filters = new ArrayList<>();
    private int startPosition = 0;
    private int maxResult = 20;

    public MongodbQuery(MongoDatabase mongoDatabase, Class<T> entityClass) {
        this.mongoDatabase = mongoDatabase;
        this.entityClass = entityClass;
        this.mongoCollectionName = entityClass.getName();
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

    @Override
    public CompletableFuture<T> singleResult(Object primaryKey) {
        return CompletableFuture.supplyAsync(() -> {
            String idField = EntityIdUtility.getInstance().getIdField(entityClass);
            Document doc = mongoDatabase.getCollection(mongoCollectionName).find(Filters.eq(idField, primaryKey)).first();
            return transform(doc, new ObjectMapper());
        }).exceptionally(ex -> {
            throw new PersistenceException(PersistenceException.BUSINESS_EXCEPTION_CODE_13012, ex);
        });
    }

    private T transform(Document document, ObjectMapper om) {
        if (document == null) {
            return null;
        }
        try {
            document.remove("_id");
            return om.readValue(document.toJson(), entityClass);
        } catch (JsonProcessingException e) {
            throw new PersistenceException(e);
        }
    }

    private FindIterable<Document> findMongoDocuments() {
        MongoCollection<Document> collection = this.mongoDatabase.getCollection(this.mongoCollectionName);
        Bson bson = null;
        for (int i = 0; i < this.filters.size(); i++) {
            SimpleFilter filter = this.filters.get(i);
            if (i == 0) {
                if (filter.getOperator().equals(FilterOperator.equals)) {
                    bson = Filters.eq(filter.getAttributeName(), filter.getFilterValue());
                } else if (filter.getOperator().equals(FilterOperator.like)) {
                    bson = Filters.regex(filter.getAttributeName(), filter.getFilterValue().toString(), "i");
                }
            } else {
                if (filter.getOperator().equals(FilterOperator.equals)) {
                    bson = Filters.and(bson, Filters.eq(filter.getAttributeName(), filter.getFilterValue()));
                } else if (filter.getOperator().equals(FilterOperator.like)) {
                    bson = Filters.and(bson, Filters.regex(filter.getAttributeName(), filter.getFilterValue().toString(), "i"));
                }
            }
        }
        if (bson == null) {
            return collection.find();
        } else {
            return collection.find(bson);
        }
    }

    @Override
    public CompletableFuture<T> singleResult() {
        return CompletableFuture.supplyAsync(() -> {
            Document doc = findMongoDocuments().first();
            return transform(doc, new ObjectMapper());
        }).exceptionally(ex -> {
            throw new PersistenceException(PersistenceException.BUSINESS_EXCEPTION_CODE_13012, ex);
        });
    }

    @Override
    public CompletableFuture<List<T>> resultList() {
        return CompletableFuture.supplyAsync(() -> {
            FindIterable<Document> fi = findMongoDocuments().skip(this.startPosition).limit(this.maxResult);
            MongoCursor<Document> i = fi.iterator();
            List<T> list = new ArrayList<>();
            ObjectMapper om = new ObjectMapper();
            while (i.hasNext()) {
                Document doc = i.next();
                list.add(this.transform(doc, om));
            }
            return list;
        }).exceptionally(ex -> {
            throw new PersistenceException(PersistenceException.BUSINESS_EXCEPTION_CODE_13012, ex);
        });
    }

}
