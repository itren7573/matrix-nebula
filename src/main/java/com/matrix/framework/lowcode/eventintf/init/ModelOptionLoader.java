package com.matrix.framework.lowcode.eventintf.init;

import com.matrix.framework.core.common.global.Options;
import com.matrix.framework.core.loader.ILoader;
import com.matrix.framework.global.controller.GlobalController;
import com.matrix.framework.lowcode.eventintf.annotation.OptionsFilter;
import com.matrix.framework.lowcode.eventintf.data.ModelOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.aop.support.AopUtils;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;

/**
 * 模型选项加载器
 * Copyright © 雪球工作室 版权所有
 *
 * @Author: 李鹏
 * @Create: 2025/2/15 20:05
 * @Since 1.0
 */
@Service
@Order(0)
public class ModelOptionLoader implements ILoader {

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public String getServiceName() {
        return "ModelOptionReg";
    }

    @Override
    public Mono<Void> load() {
        return Mono.fromRunnable(() -> {
            // 获取所有带有Service注解的bean
            Map<String, Object> services = applicationContext.getBeansWithAnnotation(Service.class);

            // 遍历所有service
            services.forEach((beanName, bean) -> {
                // 获取原始类（如果是代理类则获取目标类）
                Class<?> targetClass = AopUtils.isAopProxy(bean) ? 
                    AopUtils.getTargetClass(bean) : bean.getClass();
                
                // 获取类中的所有方法
                Method[] methods = targetClass.getDeclaredMethods();
                
                // 遍历方法查找带有OptionsFilter注解的方法
                Arrays.stream(methods).forEach(method -> {
                    OptionsFilter annotation = method.getAnnotation(OptionsFilter.class);
                    if (annotation != null) {
                        // 获取注解中的信息
                        String name = annotation.name().isEmpty() ? method.getName() : annotation.name();
                        String location = annotation.location();
                        String tableName = annotation.tableName();
                        
                        // 如果location为空，则使用service的bean名称
                        if (location.isEmpty()) {
                            location = beanName;
                        }

                        // 构造Options实体
                        Options options = new Options(name, location);

                        // 构造ModelOption实体
                        ModelOption modelOption = new ModelOption(tableName, options);

                        // 将ModelOption添加到GlobalController的集合中
                        GlobalController.ALL_OPTIONS.add(modelOption);
                        System.out.println("ModelOptionLoader load option: " + modelOption.getTableName() + " - " + modelOption.getOptions().getLabel());

                    }
                });
            });
        });
    }
}
