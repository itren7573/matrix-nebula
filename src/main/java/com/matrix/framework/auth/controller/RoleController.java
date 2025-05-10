package com.matrix.framework.auth.controller;

import com.matrix.framework.auth.data.PermissionVo;
import com.matrix.framework.auth.data.RoleSaveDto;
import com.matrix.framework.auth.data.RoleVo;
import com.matrix.framework.auth.service.RoleService;
import com.matrix.framework.core.annotation.validation.LogCollector;
import com.matrix.framework.core.common.result.Result;
import com.matrix.framework.core.i18n.I18n;
import com.matrix.framework.core.i18n.MessageConstants;
import com.matrix.framework.core.sse.SseController;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 用户管理控制器
 *
 * Copyright © 海平面工作室 版权所有
 *
 * @Author: Leo
 * @Create: 2024/10/3 6:11
 * @Since 1.0
 */
@RestController
@RequestMapping("/auth/role")
public class RoleController {

    final SseController sseController;

    final RoleService roleService;


    public RoleController(SseController sseController, RoleService userService) {
        this.sseController = sseController;
        this.roleService = userService;
    }

    @LogCollector
    @PostMapping("/save")
    public Mono<ResponseEntity<Result<Object>>> save(@RequestBody RoleSaveDto saveDto, ServerWebExchange request) {
        // 调用服务层保存角色信息
        return roleService.saveWithPermissions(saveDto)
                .map(u -> {
                    // 发送sse消息
                    //sseController.notifyClients("users", role);
                    return ResponseEntity.ok(Result.ok());
                })
                .onErrorResume(e -> {
                    Result<Object> errResult = Result.fail();
                    errResult.setMessage(e.getMessage().contains("nameIndex") ? I18n.getMessage(MessageConstants.ROLE_NAME_EXISTS) : e.getMessage());
                    return Mono.just(ResponseEntity
                            .status(HttpStatus.BAD_REQUEST)
                            .body(errResult));
                });
    }

    @GetMapping("/list")
    public Mono<Result<Map<String, Object>>> list(
            @RequestParam("page") int page,
            @RequestParam("pageSize") int size,
            @RequestParam(value = "search", required = false) String search,
            ServerWebExchange request) {

        PageRequest pageable = PageRequest.of(page - 1, size);

        return roleService.findRoles(pageable, search)
                .flatMap(result -> Mono.just(Result.ok(result)));
    }

    @GetMapping("/no-super-list")
    public Mono<Result<Map<String, Object>>> userListNoSuper(
            @RequestParam(value = "search", required = false) String search) {

        return roleService.findRoles(null, search)
                .flatMap(result -> {
                    Map<String, Object> filteredResult = new HashMap<>();
                    List<RoleVo> roles = (List<RoleVo>) result.get("items");
                    roles = roles.stream()
                            .filter(role -> role.getId() != 1)
                            .collect(Collectors.toList());
                    filteredResult.put("items", roles);
                    filteredResult.put("total", roles.size());
                    return Mono.just(Result.ok(filteredResult));
                });
    }

    @GetMapping("/system-roles")
    public Mono<Result<Map<String, Object>>> getSystemRoles() {
        return roleService.findRoles(null, null)
                .flatMap(result -> {
                    Map<String, Object> filteredResult = new HashMap<>();
                    List<RoleVo> roles = (List<RoleVo>) result.get("items");
                    roles = roles.stream()
                            .filter(role -> role.getId() >= 2 && role.getId() <= 4)
                            .collect(Collectors.toList());
                    filteredResult.put("items", roles);
                    filteredResult.put("total", roles.size());
                    return Mono.just(Result.ok(filteredResult));
                });
    }

    @LogCollector
    @DeleteMapping("/delete")
    public Mono<Result<Object>> delete(@RequestParam("id") String id, ServerWebExchange request) {
        return roleService.getById(Long.parseLong(id))  // 先查询
                .flatMap(role -> roleService.delete(role.getId())  // 再删除
                        .thenReturn(Result.<Object>ok(role)  // 将查询到的用户对象放入Result，明确指定泛型类型
                                .message(I18n.getMessage(MessageConstants.DELETE_SUCCESS))))
                .onErrorResume(e -> Mono.just(Result.<Object>fail()  // 错误处理时也需要明确指定泛型类型
                        .message(I18n.getMessage(MessageConstants.DELETE_FAIL) + e.getMessage())));
    }

    @GetMapping("/permission-tree")
    public Mono<Result<List<PermissionVo>>> getPermissionTree(
            @RequestParam(value = "roleId", required = false) Long roleId) {
        return roleService.getPermissionTree(roleId)
                .map(Result::ok);
    }
}
