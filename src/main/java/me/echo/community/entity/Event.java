package me.echo.community.entity;

import java.util.HashMap;
import java.util.Map;

public class Event {
    private String topic;
    private Integer userId;
    private int entityType;
    private int entityId;
    private Integer entityUserId;
    private final Map<String, Object> data = new HashMap<>();

    public String getTopic() {
        return topic;
    }

    public Event setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public Integer getUserId() {
        return userId;
    }

    public Event setUserId(Integer userId) {
        this.userId = userId;
        return this;
    }

    public int getEntityType() {
        return entityType;
    }

    public Event setEntityType(int entityType) {
        this.entityType = entityType;
        return this;
    }

    public int getEntityId() {
        return entityId;
    }

    public Event setEntityId(int entityId) {
        this.entityId = entityId;
        return this;
    }

    public Integer getEntityUserId() {
        return entityUserId;
    }

    public Event setEntityUserId(Integer entityUserId) {
        this.entityUserId = entityUserId;
        return this;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public Event setData(String key, Object value){
        this.data.put(key, value);
        return this;
    }

}
