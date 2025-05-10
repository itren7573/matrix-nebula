package com.matrix.framework.lowcode.eventintf.event;

import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * 编辑前事件接口
 *
 * Copyright © 海平面工作室
 *
 * @Author: Leo
 * @Create: 2024/2/12
 * @Since 1.0
 */
public interface BeforeEditEvent {

    Mono<Map<String, Object>> doBeforeEdit(Map<String, Object> result);

}
