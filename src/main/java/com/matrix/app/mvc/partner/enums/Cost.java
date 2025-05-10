package com.matrix.app.mvc.partner.enums;

import com.matrix.framework.core.common.enums.IEnumDescribable;

/**
 * 合伙人成本编码
 *
 * Copyright © 海平面工作室
 * <p>
 * @author  Leo
 * @create  2025/1/29
 * @since 1.0
 */

public enum Cost implements IEnumDescribable {

    Hard("硬件"),
    Soft("软件"),
    Rent("租金"),
    Utilities("水电"),
    ManHour("工时");

    private final String description;

    Cost(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
