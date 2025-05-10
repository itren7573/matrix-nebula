package com.matrix.framework.lowcode.eventintf.event;

import reactor.core.publisher.Mono;
import java.util.Map;

/**
 * 保存后事件接口
 *
 * Copyright © 海平面工作室
 *
 * @Author: 李鹏
 * @Create: 2025/2/12 21:56
 * @Since 1.0
 */
public interface AfterSaveEvent {

    Mono<Map<String, Object>> doAfterSave(Map<String, Object> data);
    
}
