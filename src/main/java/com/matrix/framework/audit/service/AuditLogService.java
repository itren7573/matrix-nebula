package com.matrix.framework.audit.service;

import com.matrix.framework.audit.data.AuditLogPo;
import com.matrix.framework.audit.data.AuditLogVo;
import com.matrix.framework.audit.repositories.AuditLogRepository;
import com.matrix.framework.core.common.utils.DateTime;
import com.matrix.framework.core.i18n.I18n;
import org.springframework.data.domain.PageRequest;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 审计日志服务
 *
 * Copyright © 海平面工作室 版权所有
 *
 * @Author: Leo
 * @Create: 2024/10/3 11:15
 * @Since 1.0
 */
@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final DatabaseClient databaseClient;

    public AuditLogService(AuditLogRepository auditLogRepository, DatabaseClient databaseClient) {
        this.auditLogRepository = auditLogRepository;
        this.databaseClient = databaseClient;
    }

    public Mono<Map<String, Object>> findAll(PageRequest pageable, String search) {
        StringBuilder baseQueryBuilder = new StringBuilder("SELECT * FROM audit_log WHERE true");
        StringBuilder countQueryBuilder = new StringBuilder("SELECT COUNT(*) FROM audit_log WHERE true");

        List<Object> values = new ArrayList<>();

        if (search != null && !search.isEmpty()) {
            baseQueryBuilder.append(" AND (username LIKE ? OR module LIKE ? OR action LIKE ? OR details LIKE ?)");
            countQueryBuilder.append(" AND (username LIKE ? OR module LIKE ? OR action LIKE ? OR details LIKE ?)");
            String searchPattern = "%" + search + "%";
            values.add(searchPattern);
            values.add(searchPattern);
            values.add(searchPattern);
            values.add(searchPattern);
        }

        baseQueryBuilder.append(" ORDER BY timestamp DESC");
        if (pageable.isPaged()) {
            baseQueryBuilder.append(" LIMIT ").append(pageable.getPageSize())
                    .append(" OFFSET ").append(pageable.getOffset());
        }

        DatabaseClient.GenericExecuteSpec baseSpec = databaseClient.sql(baseQueryBuilder.toString());
        DatabaseClient.GenericExecuteSpec countSpec = databaseClient.sql(countQueryBuilder.toString());

        for (int i = 0; i < values.size(); i++) {
            baseSpec = baseSpec.bind(i, values.get(i));
            countSpec = countSpec.bind(i, values.get(i));
        }

        Flux<AuditLogVo> logs = baseSpec.map((row, metadata) -> {
            AuditLogVo log = new AuditLogVo();
            log.setId(row.get("id", Long.class));
            log.setUsername(row.get("username", String.class));
            log.setModule(row.get("module", String.class));
            log.setAction(row.get("action", String.class));
            log.setCreateTime(DateTime.format(row.get("timestamp", Long.class)));
            log.setDetails(row.get("details", String.class));
            return log;
        }).all();

        Mono<Long> total = countSpec.map((row, metadata) -> row.get(0, Long.class)).one();

        return logs.collectList()
                .flatMap(list -> total.map(count -> {
                    Map<String, Object> result = new HashMap<>();
                    result.put("items", list);
                    result.put("total", count);
                    return result;
                }));
    }

    public Flux<AuditLogPo> findAll() {
        return auditLogRepository.findAll()
                .map(log -> {
                    log.setDetails(I18n.getMessage(log.getAction()));
                    return log;
                });
    }

    public Mono<AuditLogPo> findById(Long id) {
        return auditLogRepository.findById(id);
    }

    public Mono<AuditLogPo> save(AuditLogPo auditLog) {
        auditLog.setTimestamp(System.currentTimeMillis());
        return auditLogRepository.save(auditLog);
    }

    public Mono<Void> deleteById(Long id) {
        return auditLogRepository.deleteById(id);
    }
} 