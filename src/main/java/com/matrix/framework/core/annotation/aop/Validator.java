package com.matrix.framework.core.annotation.aop;

import com.matrix.framework.core.annotation.excepton.ValidationException;
import com.matrix.framework.core.annotation.validation.*;
import com.matrix.framework.core.i18n.I18n;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Validator {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern IP_PATTERN = Pattern.compile("^([0-9]{1,3}\\.){3}[0-9]{1,3}$");
    private static final Pattern URL_PATTERN = Pattern.compile("^(http|https)://.*$");

    public static void validate(Object object) throws ValidationException {
        List<Field> fields = getAllFields(object.getClass());
        for (Field field : fields) {
            field.setAccessible(true);
            validateField(object, field);
        }
    }

    // 获取类及其所有父类的字段
    private static List<Field> getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        while (clazz != null && clazz != Object.class) {
            for (Field field : clazz.getDeclaredFields()) {
                fields.add(field);
            }
            clazz = clazz.getSuperclass();
        }
        return fields;
    }

    private static void validateField(Object object, Field field) throws ValidationException {
        try {
            Object value = field.get(object);

            // Required 验证
            if (field.isAnnotationPresent(Required.class)) {
                Required required = field.getAnnotation(Required.class);
                if (value == null || (value instanceof String && ((String) value).trim().isEmpty())) {
                    throw new ValidationException(required.message());
                }
            }

            if (value == null) {
                return;
            }

            // Length 验证
            if (field.isAnnotationPresent(Length.class) && value instanceof String) {
                Length length = field.getAnnotation(Length.class);
                String strValue = (String) value;
                if (strValue.length() < length.min() || strValue.length() > length.max()) {
                    String message = I18n.getMessage(length.message())
                            .replace("{min}", String.valueOf(length.min()))
                            .replace("{max}", String.valueOf(length.max()));
                    throw new ValidationException(message);
                }
            }

            // Email 验证
            if (field.isAnnotationPresent(Email.class) && value instanceof String) {
                Email email = field.getAnnotation(Email.class);
                if (!EMAIL_PATTERN.matcher((String) value).matches()) {
                    throw new ValidationException(email.message());
                }
            }

            // IP 验证
            if (field.isAnnotationPresent(IP.class) && value instanceof String) {
                IP ip = field.getAnnotation(IP.class);
                if (!IP_PATTERN.matcher((String) value).matches()) {
                    throw new ValidationException(ip.message());
                }
            }

            // URL 验证
            if (field.isAnnotationPresent(URL.class) && value instanceof String) {
                URL url = field.getAnnotation(URL.class);
                if (!URL_PATTERN.matcher((String) value).matches()) {
                    throw new ValidationException(url.message());
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException("验证过程中发生错误", e);
        }
    }
}