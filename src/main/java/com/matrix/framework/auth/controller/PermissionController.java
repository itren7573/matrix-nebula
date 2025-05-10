package com.matrix.framework.auth.controller;

import com.matrix.framework.auth.data.Permission;
import com.matrix.framework.auth.data.PermissionVo;
import com.matrix.framework.auth.service.PermissionService;
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

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 权限管理控制器
 *
 * Copyright © 海平面工作室 版权所有
 *
 * @Author: Leo
 * @Create: 2024/10/3 11:15
 * @Since 1.0
 */
@RestController
@RequestMapping("/auth/permission")
public class PermissionController {

    private final PermissionService permissionService;
    private final SseController sseController;
    private final RoleService roleService;

    public PermissionController(PermissionService permissionService, SseController sseController, RoleService roleService) {
        this.permissionService = permissionService;
        this.sseController = sseController;
        this.roleService = roleService;
    }

    @GetMapping("/list")
    public Mono<Result<Map<String, Object>>> list(
            @RequestParam("page") int page,
            @RequestParam("pageSize") int size,
            @RequestParam(value = "id", required = false) Long id) {

        PageRequest pageable = PageRequest.of(page - 1, size);

        return permissionService.findAll(pageable, id)
                .flatMap(result -> Mono.just(Result.ok(result)));
    }

    @GetMapping("/tree")
    public Mono<ResponseEntity<Result<List<PermissionVo>>>> getPermissionTree() {
        return permissionService.getPermissionTree()
                .map(permissionList -> {
                    Result<List<PermissionVo>> result = Result.ok(permissionList);
                    result.setCode(200);
                    result.setMessage(I18n.getMessage(MessageConstants.QUERY_SUCCESS));
                    return ResponseEntity.ok(result);
                });
    }

    @LogCollector
    @PostMapping("/save")
    public Mono<ResponseEntity<Result<Object>>> save(@RequestBody PermissionVo permissionVo, ServerWebExchange request) {
        // 转换 PermissionVo 到 Permission
        Permission permission = new Permission();
        permission.setId(permissionVo.getId());
        permission.setParentId(permissionVo.getParentId());
        permission.setComponent(permissionVo.getComponent());
        permission.setName(permissionVo.getName());
        permission.setCode(permissionVo.getCode());
        permission.setPath(permissionVo.getPath());
        permission.setIcon(permissionVo.getIcon());
        permission.setTitle(permissionVo.getTitle());
        permission.setSort(permissionVo.getSort());
        permission.setRedirect(permissionVo.getRedirect());
        permission.setAttr(permissionVo.getAttr());
        permission.setDescription(permissionVo.getDescription());

        return permissionService.save(permission)
                .flatMap(saved -> {
                    if (permissionVo.getRoles() != null && !permissionVo.getRoles().isEmpty()) {
                        // 如果指定了角色，直接绑定
                        return roleService.saveWithPermissions(saved.getId(), permissionVo.getRoles())
                                .thenReturn(saved);
                    } else if (permissionVo.getParentId() != null) {
                        // 如果没有指定角色且有父节点，继承父节点的角色
                        return permissionService.findById(permissionVo.getParentId())
                                .flatMap(parent -> roleService.findRolesByPermissionId(parent.getId())
                                        .collectList()
                                        .flatMap(roles -> {
                                            String roleIds = roles.stream()
                                                    .map(role -> role.getId().toString())
                                                    .collect(Collectors.joining(","));
                                            return roleService.saveWithPermissions(saved.getId(), roleIds)
                                                    .thenReturn(saved);
                                        }));
                    }
                    return Mono.just(saved);
                })
                .map(saved -> {
                    // 发送 sse 消息
                    //sseController.notifyClients("permissions", saved);
                    return ResponseEntity.ok(Result.ok());
                })
                .onErrorResume(e -> {
                    Result<Object> errResult = Result.fail();
                    errResult.setMessage(e.getMessage().contains("nameIndex") ? 
                        I18n.getMessage(MessageConstants.PERMISSION_NAME_EXISTS) : e.getMessage());
                    return Mono.just(ResponseEntity
                            .status(HttpStatus.BAD_REQUEST)
                            .body(errResult));
                });
    }

    @LogCollector
    @DeleteMapping("/delete")
    public Mono<Result<Object>> delete(@RequestParam("id") String id, ServerWebExchange request) {
        Long permId = Long.parseLong(id);
        // 先查询权限
        return permissionService.getById(permId)
                // 删除权限与角色的关联关系
                .flatMap(permission -> roleService.saveWithPermissions(permission.getId(), "")
                        // 然后删除权限本身
                        .then(permissionService.delete(permission.getId()))
                        .thenReturn(Result.<Object>ok(permission)
                                .message(I18n.getMessage(MessageConstants.DELETE_SUCCESS))))
                .onErrorResume(e -> Mono.just(Result.<Object>fail()
                        .message(I18n.getMessage(MessageConstants.DELETE_FAIL) + e.getMessage())));
    }

    @GetMapping("/children")
    public Mono<Result<Map<String, Object>>> children(@RequestParam("parentId") Long parentId) {
        return permissionService.findByParentId(parentId)
                .collectList()
                .map(permissions -> {
                    Map<String, Object> result = new HashMap<>();
                    result.put("items", permissions);
                    result.put("total", permissions.size());
                    return Result.ok(result);
                });
    }

    @GetMapping("/{id}")
    public Mono<Result<Permission>> getById(@PathVariable("id")  String id) {
        return permissionService.getById(Long.parseLong(id))
                .map(Result::ok)
                .onErrorResume(e -> Mono.just(Result.<Permission>fail()
                        .message(e.getMessage())));
    }

    @PostMapping("/move")
    public Mono<Result<Object>> updateMenuPosition(@RequestBody Map<String, Object> params) {
        Long dragId = Long.parseLong(params.get("dragId").toString());
        Long dropId = Long.parseLong(params.get("dropId").toString());
        Long dropPosition = Long.parseLong(params.get("dropPosition").toString());
        String dropType = (String) params.get("dropType");
        
        return permissionService.updatePosition(dragId, dropId, dropPosition, dropType)
                .thenReturn(Result.ok())
                .onErrorResume(e -> Mono.just(Result.fail().message(e.getMessage())));
    }

    @PostMapping("/updateOrder")
    public Mono<Result<Object>> updateMenuOrder(@RequestBody List<Map<String, Long>> data) {
        return permissionService.updateOrder(data)
                .thenReturn(Result.ok());
    }

    @GetMapping("/getRole/{permissionId}")
    public Mono<Result<List<Map<String, Object>>>> getRolesByPermissionId(@PathVariable("permissionId") String permissionId) {
        return roleService.findRolesByPermissionId(Long.parseLong(permissionId))
                .map(role -> {
                    Map<String, Object> roleMap = new HashMap<>();
                    roleMap.put("id", role.getId());
                    roleMap.put("name", role.getName());
                    roleMap.put("desc", role.getDescription());
                    return roleMap;
                })
                .collectList()
                .map(Result::ok)
                .onErrorResume(e -> Mono.just(Result.<List<Map<String, Object>>>fail()
                        .message(e.getMessage())));
    }
} 