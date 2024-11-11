package com.plantssoil.common.persistence;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.Id;

import org.bson.types.ObjectId;

import com.plantssoil.common.persistence.exception.PersistenceException;

/**
 * To get entity declared field which annotated with Id
 * 
 * @see javax.persistence.Id
 * @author danialdy
 * @Date 11 Nov 2024 9:34:21 am
 */
public class EntityIdUtility {
    private static volatile EntityIdUtility instance;
    private Map<String, String> idFieldMap;

    private EntityIdUtility() {
        this.idFieldMap = new ConcurrentHashMap<>();
    }

    /**
     * Get the instance of this utility
     * 
     * @return Entity Id Getter instance
     */
    public static EntityIdUtility getInstance() {
        if (instance == null) {
            synchronized (EntityIdUtility.class) {
                if (instance == null) {
                    instance = new EntityIdUtility();
                }
            }
        }
        return instance;
    }

    /**
     * Get the ID field name of one entity class
     * 
     * @param entityClass the entity class
     * @return ID field name
     */
    public String getIdField(Class<?> entityClass) {
        String entityClassName = entityClass.getName();
        String idField = idFieldMap.get(entityClassName);
        if (idField == null) {
            synchronized (this) {
                idField = idFieldMap.get(entityClassName);
                if (idField == null) {
                    Field[] fields = entityClass.getDeclaredFields();
                    for (Field field : fields) {
                        if (field.isAnnotationPresent(Id.class)) {
                            idField = field.getName();
                            idFieldMap.put(entityClassName, idField);
                            break;
                        }
                    }
                }
            }
        }
        if (idField == null) {
            String err = String.format("The entity %s should define Id (%s) annotated field!", entityClassName, Id.class.getName());
            throw new PersistenceException(PersistenceException.BUSINESS_EXCEPTION_CODE_13015, err);
        }
        return idField;
    }

    /**
     * Generate unique id, which will be UNIQUE within the whole world (just like
     * UUID)
     * 
     * @return unique entity id string
     */
    public String generateUniqueId() {
        return new ObjectId().toHexString();
    }
}
