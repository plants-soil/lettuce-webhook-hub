package com.plantssoil.webhook.core.registry;

import java.util.ArrayList;
import java.util.List;

import com.plantssoil.webhook.core.IEvent;
import com.plantssoil.webhook.core.IPublisher;

/**
 * The in-memory implementation of IPublisher<br/>
 * All data will be lost when JVM shutdown<br/>
 * It's only for demonstration purpose, SHOULD AVOID be used in production
 * environment<br/>
 * 
 * @author danialdy
 * @Date 2 Jan 2025 5:09:23 pm
 */
public class InMemoryPublisher implements IPublisher {
    private String publisherId;
    private boolean supportDataGroup;
    private String version;
    private List<String> dataGroups = new ArrayList<>();
    private List<IEvent> events = new ArrayList<>();

    @Override
    public void setPublisherId(String publisherId) {
        this.publisherId = publisherId;
    }

    @Override
    public void setSupportDataGroup(boolean supportDataGroup) {
        this.supportDataGroup = supportDataGroup;
    }

    @Override
    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public void addDataGroup(String dataGroup) {
        this.dataGroups.add(dataGroup);
    }

    @Override
    public void addDataGroup(List<String> dataGroups) {
        this.dataGroups.addAll(dataGroups);
    }

    @Override
    public void addEvent(IEvent event) {
        this.events.add(event);
    }

    @Override
    public void addEvent(List<IEvent> events) {
        this.events.addAll(events);
    }

    @Override
    public String getPublisherId() {
        return this.publisherId;
    }

    @Override
    public boolean isSupportDataGroup() {
        return this.supportDataGroup;
    }

    @Override
    public String getVersion() {
        return this.version;
    }

    @Override
    public List<String> findDataGroups(int page, int pageSize) {
        List<String> list = new ArrayList<>();
        int beginIndex = page * pageSize;
        int endIndex = beginIndex + pageSize;

        for (int i = beginIndex; i < this.dataGroups.size() && i < endIndex; i++) {
            list.add(this.dataGroups.get(i));
        }
        return list;
    }

    @Override
    public List<IEvent> findEvents(int page, int pageSize) {
        List<IEvent> list = new ArrayList<>();
        int beginIndex = page * pageSize;
        int endIndex = beginIndex + pageSize;

        for (int i = beginIndex; i < this.events.size() && i < endIndex; i++) {
            list.add(this.events.get(i));
        }
        return list;
    }

}
