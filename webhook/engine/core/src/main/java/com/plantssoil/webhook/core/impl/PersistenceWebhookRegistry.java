package com.plantssoil.webhook.core.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.plantssoil.common.persistence.EntityIdUtility;
import com.plantssoil.common.persistence.IPersistence;
import com.plantssoil.common.persistence.IPersistenceFactory;
import com.plantssoil.webhook.beans.DataGroup;
import com.plantssoil.webhook.beans.WebhookEvent;
import com.plantssoil.webhook.beans.WebhookEvent.EventStatus;
import com.plantssoil.webhook.core.IWebhookEvent;
import com.plantssoil.webhook.core.IWebhookPublisher;
import com.plantssoil.webhook.core.IWebhookRegistry;
import com.plantssoil.webhook.core.IWebhookSubscriber;
import com.plantssoil.webhook.core.exception.EngineException;

/**
 * The default implementation of webhook registry (with persistence)
 * 
 * @author danialdy
 * @Date 21 Nov 2024 3:21:43 pm
 */
public class PersistenceWebhookRegistry implements IWebhookRegistry {

    @Override
    public CompletableFuture<Void> addDataGroup(IWebhookPublisher publisher, String dataGroup) {
        return CompletableFuture.runAsync(() -> {
            try (IPersistence persists = IPersistenceFactory.getFactoryInstance().create()) {
                DataGroup dg = new DataGroup();
                dg.setDataGroupId(EntityIdUtility.getInstance().generateUniqueId());
                dg.setOrganizationId(publisher.getOrganizationId());
                dg.setDataGroupName(dataGroup);
                persists.create(dg);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).exceptionally(ex -> {
            throw new EngineException(EngineException.BUSINESS_EXCEPTION_CODE_20008, ex);
        });
    }

    @Override
    public CompletableFuture<Void> addDataGroup(IWebhookPublisher publisher, String[] dataGroups) {
        return CompletableFuture.runAsync(() -> {
            try (IPersistence persists = IPersistenceFactory.getFactoryInstance().create()) {
                List<DataGroup> dgs = new ArrayList<>();
                DataGroup dg = new DataGroup();
                for (String dataGroup : dataGroups) {
                    dg.setDataGroupId(EntityIdUtility.getInstance().generateUniqueId());
                    dg.setOrganizationId(publisher.getOrganizationId());
                    dg.setDataGroupName(dataGroup);
                    dgs.add(dg);
                }
                persists.create(dgs);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).exceptionally(ex -> {
            throw new EngineException(EngineException.BUSINESS_EXCEPTION_CODE_20008, ex);
        });
    }

    @Override
    public CompletableFuture<Void> publishWebhook(IWebhookEvent webhook) {
        return CompletableFuture.runAsync(() -> {
            try (IPersistence persists = IPersistenceFactory.getFactoryInstance().create()) {
                WebhookEvent event = new WebhookEvent();
                event.setEventId(EntityIdUtility.getInstance().generateUniqueId());
                event.setOrganizationId(webhook.getPublisherId());
                event.setVersion(webhook.getVersion());
                event.setEventType(webhook.getEventType());
                event.setEventTag(webhook.getEventTag());
                event.setContentType(webhook.getContentType());
                event.setCharset(webhook.getCharset());
                event.setEventStatus(EventStatus.PUBLISHED);
                event.setCreationTime(new java.util.Date());
                persists.create(event);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).exceptionally(ex -> {
            throw new EngineException(EngineException.BUSINESS_EXCEPTION_CODE_20009, ex);
        });
    }

    @Override
    public CompletableFuture<Void> retireWebhook(IWebhookEvent webhook) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public CompletableFuture<Void> subscribe(String subscriberId, List<IWebhookEvent> events) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public CompletableFuture<List<IWebhookPublisher>> findPublishers(int page, int pageSize) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public CompletableFuture<List<String>> findDataGroups(String publisherId, int page, int pageSize) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public CompletableFuture<List<IWebhookEvent>> findWebhooks(String publisherId, int page, int pageSize) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public CompletableFuture<List<IWebhookSubscriber>> findSubscribers(IWebhookEvent webhook, int page, int pageSize) {
        // TODO Auto-generated method stub
        return null;
    }

}
