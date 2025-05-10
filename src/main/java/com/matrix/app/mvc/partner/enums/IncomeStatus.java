package com.matrix.app.mvc.partner.enums;

import com.matrix.framework.core.common.enums.IEnumDescribable;

/**
 * 收益指标状态枚举类
 * Copyright © 海平面工作室
 * <p>
 * @author  Leo
 * @create  2025/2/23
 * @since 1.0
 */

public enum IncomeStatus implements IEnumDescribable {

    Purpose("意向"),
    Way("在途"),
    Arrival("到账");

    private final String description;

    IncomeStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
