package com.matrix.app.mvc.partner.enums;

import com.matrix.framework.core.common.enums.IEnumDescribable;

/**
 * 合伙人性别
 *
 * Copyright © 海平面工作室
 * <p>
 * @author  Leo
 * @create  2025/1/29
 * @since 1.0
 */

public enum Sex implements IEnumDescribable {

    Man("男"),
    Woman("女");

    private final String description;

    Sex(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
