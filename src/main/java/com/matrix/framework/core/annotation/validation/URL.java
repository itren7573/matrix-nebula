package com.matrix.framework.core.annotation.validation;

import com.matrix.framework.core.i18n.MessageConstants;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * URL验证注解
 *
 * Copyright © 海平面工作室 版权所有
 *
 * @Author: Leo
 * @Create: 2024/10/3 11:15
 */
@Target(ElementType.FIELD)
public @interface URL {
    String message() default MessageConstants.VALIDATION_URL;
} 