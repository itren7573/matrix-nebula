package com.matrix.framework.lowcode.eventintf.event;

import reactor.core.publisher.Mono;
import java.util.Map;

/**
 * 查询后事件接口
 *
 * Copyright © 海平面工作室
 *
 * @Author: Leo
 * @Create: 2024/2/12
 * @Since 1.0
 */
public interface AfterQueryEvent {
    Mono<Map<String, Object>> doAfterQuery(Map<String, Object> result);
}
