package com.matrix.app.mvc.partner.data;

import java.util.List;

/**
 * 权益字典视图对象
 *
 * Copyright © 海平面工作室 版权所有
 *
 * @Author: Leo
 * @Create: 2024/10/3 11:15
 * @Since 1.0
 */
public class EquityDictVo extends EquityDictPo {
    private String title;
    private String key;

    private List<EquityDictVo> children;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public List<EquityDictVo> getChildren() {
        return children;
    }

    public void setChildren(List<EquityDictVo> children) {
        this.children = children;
    }
} 