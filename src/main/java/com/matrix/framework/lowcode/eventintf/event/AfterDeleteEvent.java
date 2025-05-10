package com.matrix.framework.lowcode.eventintf.event;

import reactor.core.publisher.Mono;

/**
 * 删除后事件接口
 *
 * Copyright © 海平面工作室
 *
 * @Author: Leo
 * @Create: 2024/2/12
 * @Since 1.0
 */
public interface AfterDeleteEvent {
    Mono<Void> doAfterDelete(Long id);
}
