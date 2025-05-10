package com.matrix.framework.auth.service;

import com.matrix.framework.auth.data.Permission;
import com.matrix.framework.auth.data.PermissionVo;
import com.matrix.framework.auth.repositories.PermissionRepository;
import com.matrix.framework.core.i18n.I18n;
import com.matrix.framework.core.i18n.MessageConstants;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PermissionService {

    private final PermissionRepository permissionRepository;

    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    public Mono<Map<String, Object>> findAll(PageRequest pageable, Long id) {
        Flux<Permission> permissions;
        Mono<Long> total;

        // 根据是否有 id 参数，进行不同的查询和总数统计
        if (id == null) {
            permissions = permissionRepository.findAllBy(pageable);
            total = permissionRepository.count();
        } else {
            permissions = permissionRepository.findByIdOrParentId(id, id, pageable);
            total = permissionRepository.countByIdOrParentId(id, id);
        }

        return permissions.collectList()
                .flatMap(list -> total.map(count -> {
                    Map<String, Object> result = new HashMap<>();
                    result.put("items", list);
                    result.put("total", count);
                    return result;
                }));
    }

    @Transactional
    public Mono<Permission> save(Permission permission) {
        // 设置默认值,避免null值插入数据库
        if (permission.getRedirect() == null) {
            permission.setRedirect("");
        }
        if (permission.getAttr() == null) {
            permission.setAttr("rw");
        }
        if (permission.getDescription() == null) {
            permission.setDescription("");
        }

        return permissionRepository.save(permission)
                .flatMap(saved -> {
                    if (saved.getSort() == 0L) {
                        saved.setSort(saved.getId());
                        return permissionRepository.save(saved);
                    }
                    return Mono.just(saved);
                })
                .delayElement(Duration.ofMillis(100));
    }

    @Transactional
    public Mono<Void> delete(Long id) {
        // 先删除与当前记录id相关的子记录
        return permissionRepository.deleteByParentId(id)
                .then(permissionRepository.deleteById(id))
                .delayElement(Duration.ofMillis(100));
    }

    public Mono<Permission> findById(Long id) {
        return permissionRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException(I18n.getMessage(MessageConstants.PERMISSION_NOT_EXISTS))));
    }

    public Mono<Permission> getById(Long id) {
        return permissionRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException(I18n.getMessage(MessageConstants.PERMISSION_NOT_EXISTS))));
    }

    public Flux<Permission> findByParentId(Long parentId) {
        return permissionRepository.findByParentId(parentId);
    }

    public Mono<List<PermissionVo>> getPermissionTree() {
        return permissionRepository.findAllByOrderByOrderAsc()
                .collectList()
                .map(this::buildPermissionTree);
    }

    private List<PermissionVo> buildPermissionTree(List<Permission> permissions) {
        Map<Long, List<Permission>> pidMap = permissions.stream()
                .collect(Collectors.groupingBy(Permission::getParentId));
                
        return buildPermissionTreeRecursive(0L, pidMap);
    }
    
    private List<PermissionVo> buildPermissionTreeRecursive(Long pid, Map<Long, List<Permission>> pidMap) {
        List<PermissionVo> tree = new ArrayList<>();
        List<Permission> children = pidMap.get(pid);
        
        if (children != null) {
            for (Permission child : children) {
                PermissionVo node = convertToTreeNode(child);
                node.setChildren(buildPermissionTreeRecursive(child.getId(), pidMap));
                tree.add(node);
            }
        }
        
        return tree;
    }
    
    private PermissionVo convertToTreeNode(Permission permission) {
        PermissionVo node = new PermissionVo();
        // 基本信息
        node.setId(permission.getId());
        node.setParentId(permission.getParentId());
        node.setComponent(permission.getComponent());
        node.setName(permission.getName());
        node.setPath(permission.getPath());
        node.setIcon(permission.getIcon());
        node.setSort(permission.getSort());
        node.setRedirect(permission.getRedirect());
        
        // 为了与前端保持一致，设置 title 和 key
        node.setTitle(permission.getTitle());
        node.setKey(permission.getId().toString());
        
        return node;
    }

    @Transactional
    public Mono<Void> updatePosition(Long dragId, Long dropId, Long dropPosition, String dropType) {
        return Mono.zip(
            findById(dragId),
            findById(dropId)
        ).flatMap(tuple -> {
            Permission dragMenu = tuple.getT1();
            Permission dropMenu = tuple.getT2();
            
            // 保存原始的 sort 值
            Long dragSort = dragMenu.getSort();
            Long dropSort = dropMenu.getSort();
            
            // 设置新的父ID
            dragMenu.setParentId(dropPosition == 0 && !"same".equals(dropType) ? 
                dropId : dropMenu.getParentId());
            
            // 交换 sort 值
            dragMenu.setSort(dropSort);
            dropMenu.setSort(dragSort);
            
            // 保存两个节点的更改
            return Mono.when(
                permissionRepository.save(dragMenu),
                permissionRepository.save(dropMenu)
            );
        });
    }

    @Transactional
    public Mono<Void> updateOrder(List<Map<String, Long>> data) {
        return Flux.fromIterable(data)
            .flatMap(item -> {
                Long id = item.get("id");
                Long order = item.get("order");
                return permissionRepository.findById(id)
                    .flatMap(menu -> {
                        menu.setSort(order);
                        return permissionRepository.save(menu);
                    });
            })
            .then();
    }
}