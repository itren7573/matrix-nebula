package com.matrix.framework.auth.service;

import com.matrix.framework.auth.data.Permission;
import com.matrix.framework.auth.data.UserPo;
import com.matrix.framework.auth.data.UserVo;
import com.matrix.framework.auth.repositories.PermissionRepository;
import com.matrix.framework.auth.repositories.UserRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 登录服务
 * Copyright © 雪球工作室 版权所有
 *
 * @Author: 李鹏
 * @Create: 2024/10/3 13:38
 * @Since 1.0
 */
@Service
public class AuthService {

    final UserRepository userRepository;

    final PermissionRepository permissionRepository;

    public AuthService(UserRepository userRepository, PermissionRepository permissionRepository) {
        this.userRepository = userRepository;
        this.permissionRepository = permissionRepository;
    }

    public Mono<UserVo> getUserPermission(String username) {
        // 查询用户信息
        return userRepository.findByUsername(username)
                .flatMap(userPo -> {
                    // 查询用户权限
                    return userRepository.getUserPermissionCodes(username)
                            .collectList()  // 将 Flux<String> 转为 List<String>
                            .map(permissions -> {
                                // 构建 UserVo 对象
                                UserVo userVo = new UserVo();
                                userVo.setId(userPo.getId());
                                userVo.setUsername(userPo.getUsername());
                                userVo.setPassword(userPo.getPassword());
                                userVo.setRealName(userPo.getRealName());
                                userVo.setRoles(permissions.toArray(new String[0]));  // 将 List 转为 String[]
                                return userVo;
                            });
                });
    }

    public Mono<UserPo> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Flux<String> getUserPermissionCodes(String username) {
        return userRepository.getUserPermissionCodes(username);
    }

    public Mono<UserVo> getUserRoles(String username) {
        // 查询用户信息
        return userRepository.findByUsername(username)
                .flatMap(userPo -> {
                    // 查询用户权限
                    return userRepository.getUserRoles(username)
                            .collectList()  // 将 Flux<String> 转为 List<String>
                            .map(roles -> {
                                // 构建 UserVo 对象
                                UserVo userVo = new UserVo();
                                userVo.setId(userPo.getId());
                                userVo.setUsername(userPo.getUsername());
                                userVo.setRealName(userPo.getRealName());
                                userVo.setRoles(roles.toArray(new String[0]));
                                return userVo;
                            });
                });
    }

    public Mono<List<Map<String, Object>>> getUserMenus(String username) {
        // 如果用户是super角色，则返回所有菜单, 否则返回用户有权限的菜单
        return getUserRoles(username).flatMap(userVo -> {
            Flux<Permission> userPermissionObjects;
            if (Arrays.asList(userVo.getRoles()).contains("超级管理员")) {
                userPermissionObjects = permissionRepository.findAll().sort(Comparator.comparing(Permission::getSort));
            } else {
                userPermissionObjects = userRepository.getUserPermissionObjects(username).sort(Comparator.comparing(Permission::getSort));;
            }
            return userPermissionObjects.collectList()
                    .flatMap(permissions -> {
                        List<Map<String, Object>> permissionTree = buildPermissionTree(permissions);
                        return Mono.just(permissionTree);
                    });
        });
    }

    public List<Map<String, Object>> buildPermissionTree(List<Permission> permissions) {
        // 将 permission 列表转换为 parentId->children 的映射
        Map<Long, List<Permission>> groupedByParent = permissions.stream()
                .collect(Collectors.groupingBy(Permission::getParentId));

        // 开始构建以根节点为起点的树结构
        return groupedByParent.getOrDefault(0L, Collections.emptyList()).stream()
                .map(root -> buildPermissionNode(root, groupedByParent))
                .collect(Collectors.toList());
    }

    private Map<String, Object> buildPermissionNode(Permission permission, Map<Long, List<Permission>> groupedByParent) {
        Map<String, Object> node = new HashMap<>();
        node.put("component", permission.getComponent());
        node.put("name", permission.getName());
        node.put("path", permission.getPath());

        // 构建 meta
        Map<String, Object> meta = new HashMap<>();
        meta.put("icon", permission.getIcon());
        meta.put("title", permission.getTitle());
        
        // 处理redirect逻辑
        if (permission.getRedirect() != null && !permission.getRedirect().isEmpty()) {
            Map<String, String> lowcode = new HashMap<>();
            lowcode.put("redirect", permission.getRedirect());
            meta.put("lowcode", lowcode);
        }
        
        node.put("meta", meta);

        // 查找并构建子节点
        List<Permission> children = groupedByParent.getOrDefault(permission.getId(), Collections.emptyList());
        if (!children.isEmpty()) {
            List<Map<String, Object>> childNodes = children.stream()
                    .map(child -> buildPermissionNode(child, groupedByParent))
                    .collect(Collectors.toList());
            node.put("children", childNodes);
        }

        return node;
    }

    public Mono<Void> updateLastLoginTime(UserPo user) {
        return userRepository.save(user)
                .then();
    }
}
