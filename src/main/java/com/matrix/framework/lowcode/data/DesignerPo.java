package com.matrix.framework.lowcode.data;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;


/**
 * 低代码设计器Po实体类
 *
 * Copyright © 海平面工作室 版权所有
 *
 * @Author: Leo
 * @Create: 2024/1/30
 * @Since 1.0
 */
@Table("lowcode_designer")
public class DesignerPo {

    @Id
    private Long id;

    private String redirect;  // 主页代码（新增时后台自动生成一个uuid，表格显示但新增页和编辑页不显示）

    private String name;  // 菜单名称

    private String tableName;

    private String layout;

    private String context; // 页面设计内容，不在表格和新增与编辑页显示
    
    private String attr = "rw";  // 读写属性（不在表格和新增与编辑页显示，默认是rw）
    
    private Long createBy; // 创建人id（不在表格和新增与编辑页显示）
    
    private Long updateBy; // 修改人id （不在表格和新增与编辑页显示）

    private String description;

    private Long createTime = System.currentTimeMillis();  // 创建时间 （不在表格和新增与编辑页显示）
    
    private Long updateTime = System.currentTimeMillis();  // 修改时间 （不在表格和新增与编辑页显示）

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRedirect() {
        return redirect;
    }

    public void setRedirect(String redirect) {
        this.redirect = redirect;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getLayout() {
        return layout;
    }

    public void setLayout(String layout) {
        this.layout = layout;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getAttr() {
        return attr;
    }

    public void setAttr(String attr) {
        this.attr = attr;
    }

    public Long getCreateBy() {
        return createBy;
    }

    public void setCreateBy(Long createBy) {
        this.createBy = createBy;
    }

    public Long getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(Long updateBy) {
        this.updateBy = updateBy;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Designer{" +
                ", name='" + name + '\'';
    }
}