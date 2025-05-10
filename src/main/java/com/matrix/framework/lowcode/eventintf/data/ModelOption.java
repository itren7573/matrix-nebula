package com.matrix.framework.lowcode.eventintf.data;

import com.matrix.framework.core.common.global.Options;

/**
 * 低代码模块选项实体
 *
 * Copyright © 雪球工作室 版权所有
 *
 * @Author: 李鹏
 * @Create: 2025/2/15 20:01
 * @Since 1.0
 */
public class ModelOption {

    String tableName;

    Options options;

    public ModelOption(String tableName, Options options) {
        this.tableName = tableName;
        this.options = options;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Options getOptions() {
        return options;
    }

    public void setOptions(Options options) {
        this.options = options;
    }
}
