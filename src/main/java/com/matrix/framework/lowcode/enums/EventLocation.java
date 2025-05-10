package com.matrix.framework.lowcode.enums;

/**
 * 扩展事件枚举
 *
 * Copyright © 海平面工作室
 *
 * @Author: Leo
 * @Create: 2025/2/12 21:21
 * @Since 1.0
 */
public enum EventLocation {
    /**
     * 保存前
     */
    BEFORE_SAVE,
    
    /**
     * 保存后
     */
    AFTER_SAVE,

    /**
     * 删除前
     */
    BEFORE_DELETE,
    
    /**
     * 删除后
     */
    AFTER_DELETE,

    /**
     * 查询前
     */
    BEFORE_QUERY,

    /**
     * 编辑前
     */
    BEFORE_EDIT,

    /**
     * 查询后
     */
    AFTER_QUERY
} 