package com.matrix.app.mvc.partner.enums;

import com.matrix.framework.core.common.enums.IEnumDescribable;

/**
 * 合伙人角色
 *
 * Copyright © 海平面工作室
 * <p>
 * @author  Leo
 * @create  2025/1/29
 * @since 1.0
 */
public enum Role implements IEnumDescribable {

    architect("架构师"),
    Front("前端专家"),
    Back("后端专家"),
    Market("市场专员");

    private final String description;

    Role(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
