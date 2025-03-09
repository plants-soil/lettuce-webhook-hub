package com.plantssoil.common.persistence.mongodb;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.connection.ConnectionPoolSettings;
import com.plantssoil.common.config.ConfigFactory;
import com.plantssoil.common.config.IConfiguration;
import com.plantssoil.common.config.LettuceConfiguration;
import com.plantssoil.common.persistence.IPersistence;
import com.plantssoil.common.persistence.IPersistenceFactory;

/**
 * 
 * Manage connection pool<br/>
 * Create persistence instance, the implementation is defined via configuration
 * {@link LettuceConfiguration.PERSISTENCE_FACTORY_CONFIGURABLE}
 * 
 * @author danialdy
 * @Date 8 Nov 2024 1:18:35 pm
 */
public class MongodbPersistenceFactory implements IPersistenceFactory {
    private final static Logger LOGGER = LoggerFactory.getLogger(MongodbPersistenceFactory.class.getName());
    public final static String DATABASE_NAME = "lettucedb";
    private MongoClient client;

    public MongodbPersistenceFactory() {
        LOGGER.info("Loading Mongodb as the persistence service...");
        IConfiguration conf = ConfigFactory.getInstance().getConfiguration();
        String url = conf.getString(LettuceConfiguration.PERSISTENCE_DATABASE_URL);
        String username = null;
        if (conf.containsKey(LettuceConfiguration.PERSISTENCE_DATABASE_USERNAME)) {
            username = conf.getString(LettuceConfiguration.PERSISTENCE_DATABASE_USERNAME);
        }
        String password = null;
        if (conf.containsKey(LettuceConfiguration.PERSISTENCE_DATABASE_PASSWORD)) {
            password = conf.getString(LettuceConfiguration.PERSISTENCE_DATABASE_PASSWORD);
        }
        int poolsize = conf.getInt(LettuceConfiguration.PERSISTENCE_DATABASE_POOLSIZE, 100);
        // Mongo DB server API
        ServerApi serverApi = ServerApi.builder().version(ServerApiVersion.V1).build();
        // Mongo DB connection pool settings
        ConnectionPoolSettings poolSettings = ConnectionPoolSettings.builder().maxSize(poolsize).minSize(5).maxWaitTime(3, TimeUnit.SECONDS).build();
        // Mongo DB client settings
        MongoClientSettings.Builder settingsBuilder = MongoClientSettings.builder().applyConnectionString(new ConnectionString(url))
                .applyToConnectionPoolSettings(builder -> builder.applySettings(poolSettings).build()).serverApi(serverApi);
        // if username is not null, add credential to settings, when credential is not
        // in URL
        if (username != null && password != null) {
            MongoCredential credential = MongoCredential.createCredential(username, DATABASE_NAME, password.toCharArray());
            settingsBuilder.credential(credential);
        }
        // Create a new client and connect to the server
        this.client = MongoClients.create(settingsBuilder.build());
        LOGGER.info("Mongodb connected and loaded.");
    }

    @Override
    public void close() throws Exception {
        if (this.client != null) {
            this.client.close();
        }
    }

    @Override
    public IPersistence create() {
        return new MongodbPersistence(this.client);
    }

}
