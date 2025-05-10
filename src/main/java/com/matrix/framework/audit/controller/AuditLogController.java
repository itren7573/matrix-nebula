package com.matrix.framework.audit.controller;

import com.matrix.framework.audit.data.AuditLogPo;
import com.matrix.framework.audit.service.AuditLogService;
import com.matrix.framework.core.common.result.Result;
import com.matrix.framework.core.i18n.I18n;
import com.matrix.framework.core.i18n.MessageConstants;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * 审计日志控制器
 *
 * Copyright © 海平面工作室 版权所有
 *
 * @Author: Leo
 * @Create: 2024/10/3 11:15
 * @Since 1.0
 */
@RestController
@RequestMapping("/audit/logs")
public class AuditLogController {

    private final AuditLogService auditLogService;

    public AuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @GetMapping("/list")
    public Mono<Result<Map<String, Object>>> list(
            @RequestParam(value = "page") int page,
            @RequestParam(value = "pageSize") int size,
            @RequestParam(value = "search", required = false) String search) {

        PageRequest pageable = PageRequest.of(page - 1, size);

        return auditLogService.findAll(pageable, search)
                .flatMap(result -> Mono.just(Result.ok(result)));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<AuditLogPo>> detail(@PathVariable Long id) {
        return auditLogService.findById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<Result<Object>> save(@RequestBody AuditLogPo auditLog) {
        return auditLogService.save(auditLog)
                .map(d -> Result.ok())
                .onErrorResume(e -> Mono.just(Result.fail().message(e.getMessage())));
    }

    @DeleteMapping("/delete")
    public Mono<Result<Object>> delete(@RequestParam Long id) {
        return auditLogService.deleteById(id)
                .then(Mono.just(Result.ok().message(I18n.getMessage(MessageConstants.DELETE_SUCCESS))));
    }
} 