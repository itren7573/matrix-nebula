package com.matrix.framework.lowcode.eventintf.annotation;

import com.matrix.framework.lowcode.enums.EventLocation;

import java.lang.annotation.*;

/**
 * 扩展事件注解,用于标记需要进行事件扩展处理的方法
 *
 * Copyright © 海平面工作室
 * <p>
 * @author  Leo
 * @create  2025/2/15
 * @since 1.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ExtendEvent {
    
    /**
     * 表名
     */
    String tableName() default "";
    
    /**
     * 事件位置
     */
    EventLocation eventLocation();

    /**
     * 事件描述
     * @return
     */
    String description() default "";
    
} 