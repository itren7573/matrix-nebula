package com.matrix.framework.auth.data;

/**
 * Copyright © 海平面工作室 版权所有
 *
 * @Author: Leo
 * @Create: 2024/12/10 22:49
 * @Since 1.0
 */
public class AlertPwVo {

    String userId;
    String oldPassword;
    String newPassword;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
