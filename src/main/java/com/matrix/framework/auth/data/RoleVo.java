package com.matrix.framework.auth.data;

import com.matrix.framework.core.i18n.I18n;
import com.matrix.framework.core.i18n.MessageConstants;

public class RoleVo {

    private Long id;

    private String name;

    private String description;

    private String attr;

    private String createTime;

    private String  updateTime;

    public String getAttr() {
        return attr;
    }

    public void setAttr(String attr) {
        this.attr = attr;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return I18n.getMessage(getId() == null? MessageConstants.NEW_DATA : MessageConstants.MDF_DATA) + name + ", " + description;
    }
}
