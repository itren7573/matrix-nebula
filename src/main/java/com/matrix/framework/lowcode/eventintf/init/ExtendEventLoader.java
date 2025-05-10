package com.matrix.framework.lowcode.eventintf.init;

import com.matrix.framework.core.loader.ILoader;
import com.matrix.framework.lowcode.eventintf.annotation.ExtendEvent;
import com.matrix.framework.lowcode.eventintf.data.EventCoord;
import com.matrix.framework.lowcode.service.GenericService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

/**
 * 扩展事件注册加载器，系统每次启动时执行一次扩展事件注册
 * 它将扫描所有实现了扩展事件接口的服务类，并根据注解将扩展
 * 事件持久化到低代码的页面注册表中，使扩展事件自动注册，低
 * 代码页面执行增删该时自动寻找扩展事件，达到减少页面定义
 * 复杂度的问题
 *
 * Copyright © 海平面工作室
 *
 * @author: Leo
 * @create: 2025/2/13
 * @since 1.0
 */
@Service
@Order(0)
public class ExtendEventLoader implements ILoader {

    private static final Logger log = LoggerFactory.getLogger(ExtendEventLoader.class);

    @Override
    public String getServiceName() {
        return "ExtendEventReg";
    }

    @Override
    public Mono<Void> load() {
        return Mono.fromRunnable(() -> {
            try {
                log.info("启动扩展事件注册同步服务...");
                List<EventCoord> eventCoords = new ArrayList<>();
                PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
                CachingMetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(resolver);

                // 扫描com.loong.app包下的所有类
                Resource[] resources = resolver.getResources("classpath*:com/loong/app/**/*.class");

                for (Resource resource : resources) {
                    MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
                    String className = metadataReader.getClassMetadata().getClassName();
                    Class<?> clazz = Class.forName(className);

                    // 只处理带有@Service注解的类
                    if (clazz.isAnnotationPresent(Service.class)) {
                        // 获取类中所有带有@ExtendEvent注解的方法
                        for (java.lang.reflect.Method method : clazz.getDeclaredMethods()) {
                            ExtendEvent extendEvent = AnnotationUtils.findAnnotation(method, ExtendEvent.class);
                            if (extendEvent != null) {
                                EventCoord coord = new EventCoord();
                                coord.setTableName(extendEvent.tableName());
                                coord.setEventLocation(extendEvent.eventLocation().name());
                                
                                // 获取bean名称
                                String beanName = clazz.getSimpleName();
                                beanName = Character.toLowerCase(beanName.charAt(0)) + beanName.substring(1);
                                coord.setBeanName(beanName);
                                
                                // 设置描述信息
                                coord.setDescription(extendEvent.description());
                                
                                eventCoords.add(coord);
                                log.info("注册扩展事件: table={}, location={}, bean={}", 
                                    coord.getTableName(),
                                    coord.getEventLocation(),
                                    coord.getBeanName());
                            }
                        }
                    }
                }

                // 更新GenericService中的事件列表
                GenericService.ALL_EXTEND_EVENTS.clear();
                GenericService.ALL_EXTEND_EVENTS.addAll(eventCoords);
                
                log.info("扩展事件注册完成，共注册{}个事件", eventCoords.size());
            } catch (Exception e) {
                log.error("扩展事件注册失败", e);
                throw new RuntimeException("扩展事件注册失败: " + e.getMessage());
            }
        });
    }

}
