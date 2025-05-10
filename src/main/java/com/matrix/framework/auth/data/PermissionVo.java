package com.matrix.framework.auth.data;

import java.util.List;

public class PermissionVo extends Permission{

    private Boolean checked;
    private String title;
    private String key;
    private String roles;
    private List<PermissionVo> children;

    public Boolean getChecked() {
        return checked;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }

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

    public List<PermissionVo> getChildren() {
        return children;
    }

    public void setChildren(List<PermissionVo> children) {
        this.children = children;
    }
} 