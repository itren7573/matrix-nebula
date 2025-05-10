package com.matrix.framework.auth.data;

import java.util.List;

public class DeptVo extends DeptPo{
    private String title;
    private String key;
    private List<DeptVo> children;

    // Getters and Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }
    public List<DeptVo> getChildren() { return children; }
    public void setChildren(List<DeptVo> children) { this.children = children; }

}