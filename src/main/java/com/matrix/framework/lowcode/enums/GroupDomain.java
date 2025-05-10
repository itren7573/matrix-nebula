package com.matrix.framework.lowcode.enums;

import com.matrix.framework.core.common.enums.IEnumDescribable;

/**
 * 单选按钮或复选框作用域枚举, Private表示仅对某模块可见, Public表示全局可见
 *
 * Copyright © 海平面工作室
 * <p>
 * @author  Leo
 * @create  2025/2/15
 * @since 1.0
 */

public enum GroupDomain implements IEnumDescribable {

    Private("私有"),
    Public("公共");

    private final String description;

    GroupDomain(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
