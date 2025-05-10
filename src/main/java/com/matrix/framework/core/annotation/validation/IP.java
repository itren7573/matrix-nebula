package com.matrix.framework.core.annotation.validation;

import com.matrix.framework.core.i18n.MessageConstants;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface IP {
    String message() default MessageConstants.VALIDATION_IP;
} 