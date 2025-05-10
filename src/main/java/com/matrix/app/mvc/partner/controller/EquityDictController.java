package com.matrix.app.mvc.partner.controller;

import com.matrix.app.mvc.partner.data.EquityDictPo;
import com.matrix.app.mvc.partner.data.EquityDictVo;
import com.matrix.app.mvc.partner.service.EquityDictService;
import com.matrix.framework.core.annotation.validation.LogCollector;
import com.matrix.framework.core.common.result.Result;
import com.matrix.framework.core.i18n.I18n;
import com.matrix.framework.core.i18n.MessageConstants;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;

import java.util.List;
import java.util.Map;

/**
 * 权益字典控制器
 *
 * Copyright © 海平面工作室 版权所有
 *
 * @Author: Leo
 * @Create: 2024/10/3 11:15
 * @Since 1.0
 */
@RestController
@RequestMapping("/partner/equity-dic")
public class EquityDictController {

    private final EquityDictService equityDictService;

    public EquityDictController(EquityDictService equityDictService) {
        this.equityDictService = equityDictService;
    }

    @GetMapping("/list")
    public Result<Map<String, Object>> list(
            @RequestParam("page") int page,
            @RequestParam("pageSize") int size,
            @RequestParam(value = "pid", required = false) Long pid) {

        PageRequest pageable = PageRequest.of(page - 1, size);
        Map<String, Object> result = equityDictService.findAll(pageable, pid);
        return Result.ok(result);
    }

    @GetMapping("/tree")
    public ResponseEntity<Result<List<EquityDictVo>>> getTree() {
        List<EquityDictVo> tree = equityDictService.getTree();
        Result<List<EquityDictVo>> result = Result.ok(tree);
        result.setMessage(I18n.getMessage(MessageConstants.QUERY_SUCCESS));
        return ResponseEntity.ok(result);
    }

    @LogCollector
    @PostMapping("/save")
    public Result<Object> save(@RequestBody EquityDictPo equityDic, ServerWebExchange request) {
        try {
            equityDictService.save(equityDic);
            return Result.ok();
        } catch (Exception e) {
            return Result.fail().message(e.getMessage());
        }
    }

    @LogCollector
    @DeleteMapping("/delete")
    public Result<Object> delete(@RequestParam("id") Long id, ServerWebExchange request) {
        try {
            EquityDictPo equityDic = equityDictService.getById(id);
            equityDictService.delete(id);
            return Result.<Object>ok(equityDic).message(I18n.getMessage(MessageConstants.DELETE_SUCCESS));
        } catch (Exception e) {
            return Result.fail().message(I18n.getMessage(MessageConstants.DELETE_FAIL) + e.getMessage());
        }
    }

    @GetMapping("/search")
    public Result<List<EquityDictVo>> search(@RequestParam("name") String name) {
        List<EquityDictVo> results = equityDictService.searchByName(name);
        return Result.ok(results);
    }

    @PostMapping("/move")
    public Result<Object> updatePosition(@RequestBody Map<String, Object> params) {
        try {
            Long dragId = Long.parseLong(params.get("dragId").toString());
            Long dropId = Long.parseLong(params.get("dropId").toString());
            Long dropPosition = Long.parseLong(params.get("dropPosition").toString());
            String dropType = (String) params.get("dropType");
            
            equityDictService.updatePosition(dragId, dropId, dropPosition, dropType);
            return Result.ok();
        } catch (Exception e) {
            return Result.fail().message(e.getMessage());
        }
    }

    @PostMapping("/updateOrder")
    public Result<Object> updateOrder(@RequestBody List<Map<String, Long>> data) {
        try {
            equityDictService.updateOrder(data);
            return Result.ok();
        } catch (Exception e) {
            return Result.fail().message(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public Result<Object> getById(@PathVariable Long id) {
        try {
            EquityDictPo equityDic = equityDictService.getById(id);
            return Result.ok(equityDic);
        } catch (Exception e) {
            return Result.fail().message(e.getMessage());
        }
    }
} 