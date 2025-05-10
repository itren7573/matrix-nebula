package com.matrix.framework.core.i18n;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

/**
 * 国际化工具类
 *
 * Copyright © 海平面工作室 版权所有
 *
 * @Author: Leo
 * @Create: 2024/10/3 11:15
 * @Since 1.0
 */
@Component
public class I18n {

    private static MessageSource messageSource;

    @Autowired
    public I18n(MessageSource messageSource) {
        I18n.messageSource = messageSource;
    }

    public static String getMessage(String code, Object... args) {
        try {
            return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
        } catch (Exception e) {
            return code;
        }
    }
} 