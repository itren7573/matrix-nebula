package com.matrix.framework.lowcode.eventintf.annotation;

import java.lang.annotation.*;

/**
 * 选项接口过滤注解,将controller中的方法标记为选项接口过滤器,用于过滤选项接口的集合
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
public @interface OptionsFilter {

    /**
     * 选项接口过滤器名称,为空则为方法名
     * @return
     */
    String name() default "";

    /**
     * 选项接口过滤器所属的位置, 枚举类型填写全路径,如com.matrix.framework.lowcode.enums.EventLocation.服务类型填写bean名称
     * @return
     */
    String location() default "";

    /**
     * 低代码定义的模块所对应的数据库表名,为空则为全局Option接口
     */
    String tableName() default "Global";

    /**
     * 过滤器描述
     * @return
     */
    String description() default "";
    
} 