package com.matrix.framework.core.common.enums.auth;

import com.matrix.framework.core.common.enums.IEnumDescribable;
import com.matrix.framework.core.i18n.MessageConstants;
import com.matrix.framework.core.i18n.I18n;

/**
 * 用户锁定状态枚举
 *
 * Copyright © 海平面工作室 版权所有
 *
 * @Author: Leo
 * @Create: 2024/10/3 11:15
 * @Since 1.0
 */
public enum UserLock implements IEnumDescribable {

    NORMAL(MessageConstants.USER_LOCK_NORMAL),
    PW_LOCK(MessageConstants.USER_LOCK_PW),
    TIMEOUT_LOCK(MessageConstants.USER_LOCK_TIMEOUT),
    IP_LOCK(MessageConstants.USER_LOCK_IP),
    DISABLED(MessageConstants.USER_LOCK_DISABLED),
    FORBIDDEN(MessageConstants.USER_LOCK_FORBIDDEN);

    private final String messageKey;

    UserLock(String messageKey) {
        this.messageKey = messageKey;
    }

    @Override
    public String getDescription() {
        return I18n.getMessage(messageKey);
    }
}
