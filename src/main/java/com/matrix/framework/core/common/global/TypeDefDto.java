package com.matrix.framework.core.common.global;

import java.util.Map;

/**
 * 枚举类型或数据库字典参数类型定义实体类
 * Copyright © 海平面工作室
 *
 * @author: Leo
 * @create: 2025/1/29
 * @since 1.0
 */
public class TypeDefDto {

    private boolean isEnum;   // 是否枚举类型

    private String pathOrBeanName;   // 枚举类型的包路径或数据库字典对于的服务bean名称

    private Map<String, String> filterConditions; // 数据库字典级联过滤条件

    public Map<String, String> getFilterConditions() {
        return filterConditions;
    }

    public void setFilterConditions(Map<String, String> filterConditions) {
        this.filterConditions = filterConditions;
    }

    public String getPathOrBeanName() {
        return pathOrBeanName;
    }

    public void setPathOrBeanName(String pathOrBeanName) {
        this.pathOrBeanName = pathOrBeanName;
    }

    public boolean isEnum() {
        return isEnum;
    }

    public void setEnum(boolean anEnum) {
        isEnum = anEnum;
    }

    @Override
    public String toString() {
        return "TypeDefDto{" +
                "isEnum=" + isEnum +
                ", pathOrBeanName='" + pathOrBeanName + '\'' +
                ", filterConditions=" + filterConditions +
                '}';
    }
}
