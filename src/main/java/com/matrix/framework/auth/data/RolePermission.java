package com.matrix.framework.auth.data;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "auth_role_perm")
public class RolePermission {
    @Id
    private Long id;

    private Long roleId;

    private Long permId;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRoleId() {
        return this.roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public Long getPermId() {
        return this.permId;
    }

    public void setPermId(Long permId) {
        this.permId = permId;
    }

}
