package com.matrix.framework.core.schedule.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;

/**
 * 调度服务配置类
 *
 * Copyright © 海平面工作室 版权所有
 *
 * @Author: Leo
 * @Create: 2024/10/3 11:15
 * @Since 1.0
 */
@Configuration
public class ScheduleConfig {
    
    @Bean
    public ScheduledExecutorService scheduledExecutorService() {
        // 使用虚拟线程池来提高性能
        return Executors.newScheduledThreadPool(
            Runtime.getRuntime().availableProcessors(),
            Thread.ofVirtual().factory()
        );
    }
} 