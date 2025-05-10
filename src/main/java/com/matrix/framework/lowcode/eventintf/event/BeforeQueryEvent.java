package com.matrix.framework.lowcode.eventintf.event;

import org.springframework.data.domain.PageRequest;
import reactor.core.publisher.Mono;
import java.util.Map;

/**
 * 查询前事件接口
 *
 * Copyright © 海平面工作室
 *
 * @Author: Leo
 * @Create: 2024/2/12
 * @Since 1.0
 */
public interface BeforeQueryEvent {
    Mono<Map<String, Object>> doBeforeQuery(String search, PageRequest pageable);
}
