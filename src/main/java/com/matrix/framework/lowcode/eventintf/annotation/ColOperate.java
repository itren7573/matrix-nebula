package com.matrix.framework.lowcode.eventintf.annotation;

import java.lang.annotation.*;

/**
 * 编辑和删除之外的列操作按钮事件注解
 *
 * Copyright © 海平面工作室
 * <p>
 * @author  Leo
 * @create  2025/2/17
 * @since 1.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ColOperate {

    /**
     * 事件名称
     */
    String name() default "";

    /**
     * 表名
     */
    String tableName() default "";

    /**
     * 事件描述
     * @return
     */
    String description() default "";
    
} 