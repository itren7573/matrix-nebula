package com.matrix.framework.lowcode.eventintf.data;

/**
 * 事件坐标实体类
 *
 * Copyright © 海平面工作室
 *
 * @Author: 李鹏
 * @Create: 2025/2/12 21:21
 * @Since 1.0
 */
public class EventCoord {

    /**
     * 表名
     */
    private String tableName;

    /**
     * 服务类bean名称
     */
    private String beanName;

    /**
     * 事件位置
     */
    private String eventLocation;

    /**
     * 事件描述
     */
    private String description;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public String getEventLocation() {
        return eventLocation;
    }

    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
