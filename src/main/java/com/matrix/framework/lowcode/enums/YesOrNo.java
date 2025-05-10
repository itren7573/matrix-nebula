package com.matrix.framework.lowcode.enums;

import com.matrix.framework.core.common.enums.IEnumDescribable;

/**
 * 单选按钮选项枚举
 *
 * Copyright © 海平面工作室
 * <p>
 * @author  Leo
 * @create  2025/2/15
 * @since 1.0
 */
public enum YesOrNo implements IEnumDescribable {

    Y("是"),
    N("否");

    private final String description;

    YesOrNo(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
