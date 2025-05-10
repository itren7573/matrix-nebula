package com.matrix.framework.auth.data;

import com.matrix.framework.core.i18n.I18n;
import com.matrix.framework.core.i18n.MessageConstants;

import java.util.List;

public class RoleSaveDto {
    private RolePo role;
    private List<Long> permissionIds;

    public RolePo getRole() {
        return role;
    }

    public void setRole(RolePo role) {
        this.role = role;
    }

    public List<Long> getPermissionIds() {
        return permissionIds;
    }

    public void setPermissionIds(List<Long> permissionIds) {
        this.permissionIds = permissionIds;
    }

    @Override
    public String toString() {
        return I18n.getMessage(role.getId() == null? MessageConstants.NEW_DATA : MessageConstants.MDF_DATA) + role.getName() + ", " + role.getDescription();
    }
}