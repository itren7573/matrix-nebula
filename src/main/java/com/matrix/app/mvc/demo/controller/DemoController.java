package com.matrix.app.mvc.demo.controller;

import com.matrix.app.mvc.demo.data.Demo;
import com.matrix.app.mvc.demo.service.DemoService;
import com.matrix.framework.core.annotation.validation.LogCollector;
import com.matrix.framework.core.common.result.Result;
import com.matrix.framework.core.i18n.I18n;
import com.matrix.framework.core.i18n.MessageConstants;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;

import java.util.Map;

/**
 * 测试控制器
 *
 * Copyright © 海平面工作室 版权所有
 *
 * @Author: Leo
 * @Create: 2024/10/3 11:15
 */
@RestController
@RequestMapping("/demo")
public class DemoController {

    private final DemoService demoService;

    public DemoController(DemoService demoService) {
        this.demoService = demoService;
    }

    @GetMapping("/list")
    public Result<Map<String, Object>> list(
            @RequestParam("page") int page,
            @RequestParam("pageSize") int size,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "sex", required = false) String sex,
            @RequestParam(value = "level", required = false) String level) {
        
        PageRequest pageable = PageRequest.of(page - 1, size);
        Map<String, Object> result = demoService.findDemos(pageable, name, sex, level);
        return Result.ok(result);
    }

    @LogCollector
    @PostMapping("/save")
    public ResponseEntity<Result<Demo>> save(@RequestBody Demo demo, ServerWebExchange request) {
        Demo savedDemo = demoService.save(demo);
        return ResponseEntity.ok(Result.ok(savedDemo)
                .message(I18n.getMessage(MessageConstants.SAVE_SUCCESS)));
    }

    @LogCollector
    @DeleteMapping("/delete")
    public Result<Object> delete(@RequestParam("id") Integer id, ServerWebExchange request) {
        Demo demo = demoService.getById(id);
        demoService.delete(id);
        return Result.ok(demo);

    }

    @GetMapping("/get")
    public Result<Demo> getById(@RequestParam("id") Integer id) {
        Demo demo = demoService.getById(id);
        return Result.ok(demo);
    }
} 