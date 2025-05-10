package com.matrix.framework.lowcode.service;

import com.matrix.framework.core.common.global.SpringUtil;
import com.matrix.framework.lowcode.enums.EventLocation;
import com.matrix.framework.lowcode.eventintf.data.EventCoord;
import com.matrix.framework.lowcode.eventintf.event.BeforeSaveEvent;
import com.matrix.framework.lowcode.eventintf.event.AfterSaveEvent;
import com.matrix.framework.lowcode.eventintf.event.BeforeDeleteEvent;
import com.matrix.framework.lowcode.eventintf.event.AfterDeleteEvent;
import com.matrix.framework.lowcode.eventintf.event.BeforeQueryEvent;
import com.matrix.framework.lowcode.eventintf.event.AfterQueryEvent;
import com.matrix.framework.lowcode.eventintf.event.BeforeEditEvent;
import com.matrix.framework.lowcode.repositories.GenericRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * 通用低代码服务
 *
 * Copyright © 海平面工作室 版权所有
 *
 * @Author: Leo
 * @Create: 2024/1/30
 * @Since 1.0
 */
@Service
public class GenericService {

    public static final List<EventCoord> ALL_EXTEND_EVENTS = new ArrayList<>();

    private final GenericRepository genericRepository;

    public GenericService(GenericRepository genericRepository) {
        this.genericRepository = genericRepository;
    }

    public Mono<Map<String, Object>> save(String tableName, Map<String, Object> data) {
        return Mono.just(data)
            // 1. 执行保存前处理
            .flatMap(currentData -> {
                EventCoord beforeEvent = getExtendEvents(tableName, EventLocation.BEFORE_SAVE);
                if (beforeEvent != null) {
                    BeforeSaveEvent beforeSave = SpringUtil.getBean(beforeEvent.getBeanName(), BeforeSaveEvent.class);
                    return beforeSave.doBeforeSave(currentData)
                        .defaultIfEmpty(currentData);
                }
                return Mono.just(currentData);
            })
            // 2. 执行保存操作
            .flatMap(d -> genericRepository.save(tableName, d))
            // 3. 执行保存后处理
            .flatMap(savedData -> {
                EventCoord afterEvent = getExtendEvents(tableName, EventLocation.AFTER_SAVE);
                if (afterEvent != null) {
                    AfterSaveEvent afterSave = SpringUtil.getBean(afterEvent.getBeanName(), AfterSaveEvent.class);
                    return afterSave.doAfterSave(savedData)
                        .defaultIfEmpty(savedData);
                }
                return Mono.just(savedData);
            });
    }

    public Mono<Void> delete(String tableName, Long id) {
        return Mono.just(id)
            // 1. 执行删除前处理
            .flatMap(currentId -> {
                EventCoord beforeEvent = getExtendEvents(tableName, EventLocation.BEFORE_DELETE);
                if (beforeEvent != null) {
                    BeforeDeleteEvent beforeDelete = SpringUtil.getBean(beforeEvent.getBeanName(), BeforeDeleteEvent.class);
                    return beforeDelete.doBeforeDelete(currentId)
                        .defaultIfEmpty(currentId);
                }
                return Mono.just(currentId);
            })
            // 2. 执行删除操作
            .flatMap(currentId -> genericRepository.delete(tableName, currentId))
            // 3. 执行删除后处理
            .flatMap(result -> {
                EventCoord afterEvent = getExtendEvents(tableName, EventLocation.AFTER_DELETE);
                if (afterEvent != null) {
                    AfterDeleteEvent afterDelete = SpringUtil.getBean(afterEvent.getBeanName(), AfterDeleteEvent.class);
                    return afterDelete.doAfterDelete(id)
                        .then();
                }
                return Mono.empty();
            });
    }

    public Mono<Map<String, Object>> findAll(String tableName, PageRequest pageable, String search) {
        return Mono.just(new HashMap<String, Object>())
            // 1. 执行查询前处理
            .flatMap(params -> {
                EventCoord beforeEvent = getExtendEvents(tableName, EventLocation.BEFORE_QUERY);
                if (beforeEvent != null) {
                    BeforeQueryEvent beforeQuery = SpringUtil.getBean(beforeEvent.getBeanName(), BeforeQueryEvent.class);
                    return beforeQuery.doBeforeQuery(search, pageable)
                        .defaultIfEmpty(params);
                }
                return Mono.just(params);
            })
            // 2. 执行查询操作
            .flatMap(params -> genericRepository.findAll(tableName, pageable, search))
            // 3. 执行查询后处理
            .flatMap(result -> {
                EventCoord afterEvent = getExtendEvents(tableName, EventLocation.AFTER_QUERY);
                if (afterEvent != null) {
                    AfterQueryEvent afterQuery = SpringUtil.getBean(afterEvent.getBeanName(), AfterQueryEvent.class);
                    return afterQuery.doAfterQuery(result)
                        .defaultIfEmpty(result);
                }
                return Mono.just(result);
            });
    }

    public Mono<Map<String, Object>> findById(String tableName, Long id) {
        return genericRepository.findById(tableName, id)
            // 执行编辑前处理
            .flatMap(result -> {
                EventCoord afterEvent = getExtendEvents(tableName, EventLocation.BEFORE_EDIT);
                if (afterEvent != null) {
                    BeforeEditEvent beforeEdit = SpringUtil.getBean(afterEvent.getBeanName(), BeforeEditEvent.class);
                    return beforeEdit.doBeforeEdit(result)
                        .then(Mono.just(result));
                }
                return Mono.just(result);
            });
    }

    /**
     * 获取扩展事件
     * @param tableName 表名
     * @param eventLocation 事件位置
     * @return 事件坐标，如果没有找到返回null
     */
    private EventCoord getExtendEvents(String tableName, EventLocation eventLocation) {
        return ALL_EXTEND_EVENTS.stream()
                .filter(event -> event.getTableName().equals(tableName) 
                        && event.getEventLocation().equals(eventLocation.name()))
                .findFirst()
                .orElse(null);
    }
} 