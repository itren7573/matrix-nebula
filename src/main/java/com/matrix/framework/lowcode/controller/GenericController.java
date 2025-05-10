package com.matrix.framework.lowcode.controller;

import com.matrix.framework.core.annotation.validation.LogCollector;
import com.matrix.framework.core.common.result.Result;
import com.matrix.framework.lowcode.service.GenericService;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * 通用低代码控制器
 *
 * Copyright © 海平面工作室 版权所有
 *
 * @Author: Leo
 * @Create: 2024/1/30
 * @Since 1.0
 */
@RestController
@RequestMapping("/lowcode/{tableName}")
public class GenericController {

    private final GenericService genericService;

    public GenericController(GenericService genericService) {
        this.genericService = genericService;
    }

    @LogCollector
    @PostMapping("/save")
    public Mono<Result<Map<String, Object>>> save(
            @PathVariable String tableName,
            @RequestBody Map<String, Object> data, ServerWebExchange request) {
        return genericService.save(tableName, data)
                .map(Result::ok)
                .onErrorResume(e -> Mono.just(Result.<Map<String, Object>>fail().message(e.getMessage())));
    }

    @LogCollector
    @DeleteMapping("/delete")
    public Mono<Result<Map<String, Object>>> delete(
            @PathVariable String tableName,
            @RequestParam("id") Long id, ServerWebExchange request) {
        return genericService.findById(tableName, id)
                .flatMap(data -> genericService.delete(tableName, id)
                        .thenReturn(Result.ok(data)))
                .onErrorResume(e -> Mono.just(Result.<Map<String, Object>>fail().message(e.getMessage())));
    }

    @GetMapping("/list")
    public Mono<Result<Map<String, Object>>> list(
            @PathVariable String tableName,
            @RequestParam("page") int page,
            @RequestParam("pageSize") int size,
            @RequestParam(value = "search", required = false) String search) {
        PageRequest pageable = PageRequest.of(page - 1, size);
        return genericService.findAll(tableName, pageable, search)
                .map(Result::ok)
                .onErrorResume(e -> Mono.just(Result.<Map<String, Object>>fail().message(e.getMessage())));
    }

    @GetMapping("/{id}")
    public Mono<Result<Map<String, Object>>> getById(
            @PathVariable String tableName,
            @PathVariable Long id) {
        return genericService.findById(tableName, id)
                .map(Result::ok)
                .onErrorResume(e -> Mono.just(Result.<Map<String, Object>>fail().message(e.getMessage())));
    }
} 