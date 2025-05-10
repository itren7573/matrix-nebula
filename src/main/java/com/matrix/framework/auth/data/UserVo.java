package com.matrix.framework.auth.data;

import com.matrix.framework.core.i18n.I18n;
import com.matrix.framework.core.i18n.MessageConstants;

public class UserVo extends UserPo {

    private String[] roles;

    private String accessToken;

    private String lastLoginTimeStr;

    private String createTimeStr;

    private String updateTimeStr;

    public String getLastLoginTimeStr() {
        return lastLoginTimeStr;
    }

    public void setLastLoginTimeStr(String lastLoginTimeStr) {
        this.lastLoginTimeStr = lastLoginTimeStr;
    }

    public String getCreateTimeStr() {
        return createTimeStr;
    }

    public void setCreateTimeStr(String createTimeStr) {
        this.createTimeStr = createTimeStr;
    }

    public String getUpdateTimeStr() {
        return updateTimeStr;
    }

    public void setUpdateTimeStr(String updateTimeStr) {
        this.updateTimeStr = updateTimeStr;
    }

    public String[] getRoles() {
        return roles;
    }

    public void setRoles(String[] roles) {
        this.roles = roles;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    public String toString() {
        return I18n.getMessage(getId() == null? MessageConstants.NEW_DATA : MessageConstants.MDF_DATA) + super.toString();
    }
}
