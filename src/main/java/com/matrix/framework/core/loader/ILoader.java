package com.matrix.framework.core.loader;

import reactor.core.publisher.Mono;

/**
 * 加载器接口
 *
 * Copyright © 海平面工作室 版权所有
 *
 * @Author: Leo
 * @Create: 2024/10/3 11:15
 */
public interface ILoader {
    /**
     * 获取服务名称
     */
    String getServiceName();
    
    /**
     * 加载服务
     */
    Mono<Void> load();
    
    /**
     * 获取加载优先级，数字越小优先级越高
     */
    default int getOrder() {
        return 0;
    }
} 