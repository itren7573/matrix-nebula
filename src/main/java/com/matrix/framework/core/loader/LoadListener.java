package com.matrix.framework.core.loader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 加载监听器, 启动时加载所有实现了 ILoader 接口的类,实现初始化或常驻服务的加载
 *
 * Copyright © 海平面工作室 版权所有
 *
 * @Author: Leo
 * @Create: 2024/12/17 22:15
 * @Since 1.0
 */
@Component
public class LoadListener {
    private static final Logger logger = LoggerFactory.getLogger(LoadListener.class);

    private final List<ILoader> memoryServices;

    @Autowired
    public LoadListener(List<ILoader> memoryServices) {
        // 根据优先级排序
        memoryServices.sort(AnnotationAwareOrderComparator.INSTANCE);
        this.memoryServices = memoryServices;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void loadServices() {
        logger.info("Start loading listener...");
        
        Flux.fromIterable(memoryServices)
            .flatMap(service -> {
                logger.info("Loading listener: {}", service.getServiceName());
                return service.load()
                    .then(Mono.fromRunnable(() -> 
                        logger.info("listener loading complete: {}", service.getServiceName())))
                    .onErrorResume(e -> {
                        logger.error("listener loading failure: {}", service.getServiceName(), e);
                        return Mono.empty();
                    });
            })
            .collectList()
            .block();

        logger.info("All listeners are loaded.");
    }
} 