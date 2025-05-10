package com.matrix.framework.lowcode.data;

/**
 * 低代码设计器Vo实体类
 *
 * Copyright © 海平面工作室 版权所有
 *
 * @Author: Leo
 * @Create: 2024/1/30
 * @Since 1.0
 */
public class DesignerVo extends DesignerPo {

    private String createByName;
    
    private String updateByName;
    
    private String createTimeFormat;
    
    private String updateTimeFormat;

    public String getCreateByName() {
        return createByName;
    }

    public void setCreateByName(String createByName) {
        this.createByName = createByName;
    }

    public String getUpdateByName() {
        return updateByName;
    }

    public void setUpdateByName(String updateByName) {
        this.updateByName = updateByName;
    }

    public String getCreateTimeFormat() {
        return createTimeFormat;
    }

    public void setCreateTimeFormat(String createTimeFormat) {
        this.createTimeFormat = createTimeFormat;
    }

    public String getUpdateTimeFormat() {
        return updateTimeFormat;
    }

    public void setUpdateTimeFormat(String updateTimeFormat) {
        this.updateTimeFormat = updateTimeFormat;
    }

    @Override
    public String toString() {
        return super.toString() +
                "createByName='" + createByName + '\'' +
                ", updateByName='" + updateByName + '\'' +
                ", createTimeFormat='" + createTimeFormat + '\'' +
                ", modifyTimeFormat='" + updateTimeFormat + '\'' +
                "}";
    }
}