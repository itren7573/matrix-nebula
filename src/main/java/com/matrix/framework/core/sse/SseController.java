package com.matrix.framework.core.sse;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 通用的 SSE 控制器
 *
 * Copyright © 海平面工作室 版权所有
 *
 * @Author: Leo
 * @Create: 2024/10/3 11:15
 * @Since 1.0
 */
@RestController
public class SseController {

    // 存储多个不同类型事件的Sinks
    private final Map<String, Sinks.Many<Object>> sinkMap = new ConcurrentHashMap<>();

    // 通用的SSE端点，客户端通过 {type} 订阅不同类型的数据
    @GetMapping(value = "/sse/{type}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Object> streamEvents(@PathVariable String type) {
        Sinks.Many<Object> sink = sinkMap.computeIfAbsent(type, key -> Sinks.many().multicast().onBackpressureBuffer());
        return sink.asFlux().delayElements(Duration.ofMillis(1000));
    }

    // 通知某个类型的客户端有更新
    public void notifyClients(String type, Object data) {
        Sinks.Many<Object> sink = sinkMap.get(type);
        if (sink != null) {
            sink.tryEmitNext(data);
        }
    }
}
