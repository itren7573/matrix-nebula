package com.matrix.app.mvc.partner.enums;

import com.matrix.framework.core.common.enums.IEnumDescribable;

/**
 * 权益指标状态枚举类
 * Copyright © 海平面工作室
 * <p>
 * @author  Leo
 * @create  2025/1/29
 * @since 1.0
 */

public enum Equity implements IEnumDescribable {

    Ready("待启动"),
    InProgress("进行中"),
    Finish("已完成"),
    Done("已验收");

    private final String description;

    Equity(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
