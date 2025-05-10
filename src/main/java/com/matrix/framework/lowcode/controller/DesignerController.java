package com.matrix.framework.lowcode.controller;

import com.matrix.framework.core.annotation.validation.LogCollector;
import com.matrix.framework.core.common.result.Result;

import com.matrix.framework.core.i18n.I18n;
import com.matrix.framework.core.i18n.MessageConstants;
import com.matrix.framework.lowcode.data.DesignerPo;
import com.matrix.framework.lowcode.service.DesignerService;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * 设计器控制器
 *
 * Copyright © 海平面工作室 版权所有
 *
 * @Author: Leo
 * @Create: 2024/1/3 11:15
 * @Since 1.0
 */
@RestController
@RequestMapping("/lowcode/designer")
public class DesignerController {

    private final DesignerService designerService;

    public DesignerController(DesignerService designerService) {
        this.designerService = designerService;
    }

    @LogCollector
    @PostMapping("/save")
    public Mono<Result<Object>> save(@RequestBody DesignerPo designer, ServerWebExchange request) {
        return designerService.save(designer, request)
                .map(d -> Result.ok())
                .onErrorResume(e -> {
                    if (e.getMessage().contains("nameIndex")) {
                        return Mono.just(Result.checkFail(designer.getName() + 
                            I18n.getMessage(MessageConstants.RESPONSE_CHECK_EXIST)));
                    }
                    return Mono.just(Result.fail().message(e.getMessage()));
                });
    }

    @GetMapping("/list")
    public Mono<Result<Map<String, Object>>> list(
            @RequestParam("page") int page,
            @RequestParam("pageSize") int size,
            @RequestParam(value = "search", required = false) String search) {

        PageRequest pageable = PageRequest.of(page - 1, size);
        return designerService.findAll(pageable, search)
                .map(Result::ok);
    }

    @LogCollector
    @DeleteMapping("/delete")
    public Mono<Result<Object>> delete(@RequestParam("id") String id, ServerWebExchange request) {
        return designerService.getById(Long.parseLong(id))
                .flatMap(designer -> designerService.delete(designer.getId())
                        .thenReturn(Result.<Object>ok(designer)
                                .message(I18n.getMessage(MessageConstants.DELETE_SUCCESS))))
                .onErrorResume(e -> Mono.just(Result.<Object>fail()
                        .message(I18n.getMessage(MessageConstants.DELETE_FAIL) + e.getMessage())));
    }

    @GetMapping("/schema")
    public Mono<Result<Map<String, Object>>> getSchema(@RequestParam("redirect") String redirect) {
        return designerService.getSchema(redirect)
                .map(Result::ok)
                .onErrorResume(e -> Mono.just(Result.<Map<String, Object>>fail().message(e.getMessage())));
    }
} 