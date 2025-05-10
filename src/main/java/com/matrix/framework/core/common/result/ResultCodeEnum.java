package com.matrix.framework.core.common.result;

import com.matrix.framework.core.i18n.MessageConstants;
import com.matrix.framework.core.i18n.I18n;

/**
 * 结果码枚举类
 *
 * Copyright © 海平面工作室 版权所有
 *
 * @Author: Leo
 * @Create: 2024/10/3 11:15
 */
public enum ResultCodeEnum {

    SUCCESS(200, MessageConstants.RESPONSE_SUCCESS),
    FAIL(201, MessageConstants.RESPONSE_FAIL),
    SERVICE_ERROR(2012, MessageConstants.RESPONSE_SERVICE_ERROR),
    DATA_ERROR(204, MessageConstants.RESPONSE_DATA_ERROR),
    ILLEGAL_REQUEST(205, MessageConstants.RESPONSE_ILLEGAL_REQUEST),
    REPEAT_SUBMIT(206, MessageConstants.RESPONSE_REPEAT_SUBMIT),
    ARGUMENT_VALID_ERROR(210, MessageConstants.RESPONSE_ARGUMENT_ERROR),

    LOGIN_AUTH(208, MessageConstants.RESPONSE_LOGIN_AUTH),
    PERMISSION(209, MessageConstants.RESPONSE_PERMISSION),
    ACCOUNT_ERROR(214, MessageConstants.RESPONSE_ACCOUNT_ERROR),
    PASSWORD_ERROR(215, MessageConstants.RESPONSE_PASSWORD_ERROR),
    LOGIN_MOBLE_ERROR(216, MessageConstants.RESPONSE_LOGIN_MOBILE_ERROR),
    ACCOUNT_STOP(217, MessageConstants.RESPONSE_ACCOUNT_STOP),
    NODE_ERROR(218, MessageConstants.RESPONSE_NODE_ERROR),

    CHECK_FAIL(420, MessageConstants.RESPONSE_CHECK_FAIL);

    private Integer code;
    private String messageKey;

    private ResultCodeEnum(Integer code, String messageKey) {
        this.code = code;
        this.messageKey = messageKey;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return I18n.getMessage(this.messageKey);
    }
}
