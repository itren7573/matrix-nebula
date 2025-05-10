package com.matrix.framework.auth.controller;

import com.matrix.framework.auth.data.DeptPo;
import com.matrix.framework.auth.data.DeptVo;
import com.matrix.framework.auth.service.DeptService;
import com.matrix.framework.core.annotation.validation.LogCollector;
import com.matrix.framework.core.common.result.Result;
import com.matrix.framework.core.i18n.I18n;
import com.matrix.framework.core.i18n.MessageConstants;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/auth/dept")
public class DeptController {

    private final DeptService deptService;

    public DeptController(DeptService deptService) {
        this.deptService = deptService;
    }

    @LogCollector
    @PostMapping("/save")
    public Mono<Result<Object>> save(@RequestBody DeptPo dept, ServerWebExchange request) {
        return deptService.save(dept)
                .map(d -> Result.ok())
                .onErrorResume(e -> {
                    if (e.getMessage().contains("idx_pid_name")) {
                        return Mono.just(Result.checkFail(dept.getName() + I18n.getMessage(MessageConstants.RESPONSE_CHECK_EXIST)));
                    }
                    Result<Object> errResult = Result.fail();
                    errResult.setMessage(e.getMessage());
                    return Mono.just(errResult);
                });
    }

    @LogCollector
    @DeleteMapping("/delete")
    public Mono<Result<Object>> delete(@RequestParam("id") String id, ServerWebExchange request) {
        return deptService.getById(Long.parseLong(id))
            .flatMap(dept -> deptService.delete(dept.getId())
                .thenReturn(Result.<Object>ok(dept)
                    .message(I18n.getMessage(MessageConstants.DELETE_SUCCESS))))
            .onErrorResume(e -> Mono.just(Result.<Object>fail()
                .message(I18n.getMessage(MessageConstants.DELETE_FAIL) + e.getMessage())));
    }

    @GetMapping("/tree")
    public Mono<ResponseEntity<Result<List<DeptVo>>>> getDeptTree() {
        return deptService.getDeptTree()
                .map(deptList -> {
                    Result<List<DeptVo>> result = Result.ok(deptList);
                    result.setCode(200);
                    result.setMessage(I18n.getMessage(MessageConstants.QUERY_SUCCESS));
                    return ResponseEntity.ok(result);
                });
    }

    @LogCollector
    @GetMapping("/search")
    public Mono<Result<List<DeptVo>>> list(@RequestParam("name") String name, ServerWebExchange request) {
        return deptService.searchDepts(name)
                .collectList()
                .map(Result::ok);
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Result<DeptPo>>> getDeptById(@PathVariable("id") String id) {
        return deptService.getDeptById(Long.parseLong(id))
                .map(dept -> {
                    Result<DeptPo> result = Result.ok(dept);
                    result.setCode(200);
                    result.setMessage(I18n.getMessage(MessageConstants.QUERY_SUCCESS));
                    return ResponseEntity.ok(result);
                })
                .defaultIfEmpty(ResponseEntity.ok(Result.<DeptPo>fail().message(I18n.getMessage(MessageConstants.DEPT_NOT_EXISTS))));
    }

    
} 