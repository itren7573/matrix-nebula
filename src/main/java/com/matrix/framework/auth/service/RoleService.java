package com.matrix.framework.auth.service;

import com.matrix.framework.auth.data.*;
import com.matrix.framework.auth.repositories.PermissionRepository;
import com.matrix.framework.auth.repositories.RolePermissionRepository;
import com.matrix.framework.auth.repositories.RoleRepository;
import com.matrix.framework.core.common.utils.DateTime;
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

/**
 * Copyright © 雪球工作室 版权所有
 *
 * @Author: 李鹏
 * @Create: 2024/10/3 11:02
 * @Since 1.0
 */
@Service
public class RoleService {

    final
    RoleRepository roleRepository;
    final PermissionRepository permissionRepository;
    final RolePermissionRepository rolePermissionRepository;

    public RoleService(RoleRepository roleRepository, PermissionRepository permissionRepository, RolePermissionRepository rolePermissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.rolePermissionRepository = rolePermissionRepository;
    }

    @Transactional
    public Mono<RolePo> saveWithPermissions(RoleSaveDto saveDto) {
        return roleRepository.save(saveDto.getRole())
                .flatMap(savedRole -> {
                    // 删除原有的角色权限关系
                    return rolePermissionRepository.deleteByRoleId(savedRole.getId())
                            .then(Mono.just(savedRole));
                })
                .flatMap(savedRole -> {
                    // 获取所有权限数据，用于构建父子关系
                    return permissionRepository.findAll()
                            .collectList()
                            .flatMap(allPermissions -> {
                                // 构建权限ID的父子关系映射
                                Map<Long, Long> childToParent = new HashMap<>();
                                for (Permission perm : allPermissions) {
                                    if (perm.getParentId() != null && perm.getParentId() != 0) {
                                        childToParent.put(perm.getId(), perm.getParentId());
                                    }
                                }

                                // 获取所有选中节点的父节点
                                Set<Long> allPermissionIds = new LinkedHashSet<>();
                                for (Long permId : saveDto.getPermissionIds()) {
                                    // 添加父节点（递归向上）
                                    Long currentId = permId;
                                    while (currentId != null) {
                                        allPermissionIds.add(currentId);
                                        currentId = childToParent.get(currentId);
                                    }
                                }

                                // 创建新的角色权限关系（按照父节点在前，子节点在后的顺序）
                                List<RolePermission> rolePermissions = allPermissionIds.stream()
                                        .map(permId -> {
                                            RolePermission rp = new RolePermission();
                                            rp.setRoleId(savedRole.getId());
                                            rp.setPermId(permId);
                                            return rp;
                                        })
                                        .collect(Collectors.toList());

                                return rolePermissionRepository.saveAll(rolePermissions)
                                        .collectList()
                                        .thenReturn(savedRole);
                            });
                });
    }

    @Transactional
    public Mono<Void> delete(long id) {
        return rolePermissionRepository.deleteByRoleId(id)
                .then(roleRepository.deleteById(id))
        .delayElement(Duration.ofMillis(500));
    }

    public Mono<Map<String, Object>> findRoles(PageRequest pageable, String search) {
        Flux<RolePo> roles;
        Mono<Long> total;

        // 根据是否有搜索条件，进行不同的查询和总数统计
        if (search == null || search.isEmpty()) {
            roles = roleRepository.findAllBy(pageable);
            total = roleRepository.count();
        } else {
            roles = roleRepository.findByNameContaining(search, pageable);
            total = roleRepository.countByNameContaining(search);
        }

        return roles.collectList()
                .flatMap(list -> total.map(count -> {
                    List<RoleVo> dtoList = list.stream().map(this::convertToVo).collect(Collectors.toList());
                    Map<String, Object> result = new HashMap<>();
                    result.put("items", dtoList);
                    result.put("total", count);
                    return result;
                }));
    }

    private RoleVo convertToVo(RolePo rolePo) {
        RoleVo vo = new RoleVo();
        vo.setId(rolePo.getId());
        
        if ("r".equals(rolePo.getAttr())) {
            switch (rolePo.getId().toString()) {
                case "1":
                    vo.setName(I18n.getMessage(MessageConstants.ROLE_SUPER_ADMIN_NAME));
                    vo.setDescription(I18n.getMessage(MessageConstants.ROLE_SUPER_ADMIN_DESCRIPTION));
                    break;
                case "2":
                    vo.setName(I18n.getMessage(MessageConstants.ROLE_SYS_ADMIN_NAME));
                    vo.setDescription(I18n.getMessage(MessageConstants.ROLE_SYS_ADMIN_DESCRIPTION));
                    break;
                case "3":
                    vo.setName(I18n.getMessage(MessageConstants.ROLE_USER_ADMIN_NAME));
                    vo.setDescription(I18n.getMessage(MessageConstants.ROLE_USER_ADMIN_DESCRIPTION));
                    break;
                case "4":
                    vo.setName(I18n.getMessage(MessageConstants.ROLE_AUDIT_ADMIN_NAME));
                    vo.setDescription(I18n.getMessage(MessageConstants.ROLE_AUDIT_ADMIN_DESCRIPTION));
                    break;
                default:
                    vo.setName(rolePo.getName());
                    vo.setDescription(rolePo.getDescription());
                    break;
            }
        } else {
            vo.setName(rolePo.getName());
            vo.setDescription(rolePo.getDescription());
        }

        vo.setAttr(rolePo.getAttr());
        vo.setCreateTime(DateTime.format(rolePo.getCreateTime()));
        vo.setUpdateTime(DateTime.format(rolePo.getUpdateTime()));
        return vo;
    }

    public Mono<RolePo> findById(Long id) {
        return roleRepository.findById(id);
    }

    public Mono<List<PermissionVo>> getPermissionTree(Long roleId) {
        if (roleId == null) {
            return permissionRepository.findAll()
                    .collectList()
                    .map(permissions -> buildPermissionTree(permissions, new HashSet<>()));
        }

        return Mono.zip(
                permissionRepository.findAll().collectList(),
                rolePermissionRepository.findByRoleId(roleId)
                        .map(RolePermission::getPermId)
                        .collect(Collectors.toSet())
        ).map(tuple -> buildPermissionTree(tuple.getT1(), tuple.getT2()));
    }

    private List<PermissionVo> buildPermissionTree(List<Permission> permissions, Set<Long> checkedIds) {
        Map<Long, PermissionVo> nodeMap = new HashMap<>();
        List<PermissionVo> roots = new ArrayList<>();

        // 首先转换所有权限为树节点
        for (Permission permission : permissions) {
            PermissionVo node = convertToTreeNode(permission);
            node.setChecked(checkedIds.contains(node.getId()));
            nodeMap.put(node.getId(), node);
        }

        // 构建树结构
        for (PermissionVo node : nodeMap.values()) {
            if (node.getParentId() == null || node.getParentId() == 0) {
                roots.add(node);
            } else {
                PermissionVo parent = nodeMap.get(node.getParentId());
                if (parent != null) {
                    if (parent.getChildren() == null) {
                        parent.setChildren(new ArrayList<>());
                    }
                    parent.getChildren().add(node);
                }
            }
        }

        // 对每个节点的子节点按order排序
        sortTreeNodes(roots);
        return roots;
    }

    private void sortTreeNodes(List<PermissionVo> nodes) {
        if (nodes == null || nodes.isEmpty()) {
            return;
        }
        
        nodes.sort(Comparator.comparing(PermissionVo::getSort, Comparator.nullsLast(Comparator.naturalOrder())));
        
        for (PermissionVo node : nodes) {
            if (node.getChildren() != null) {
                sortTreeNodes(node.getChildren());
            }
        }
    }

    private PermissionVo convertToTreeNode(Permission permission) {
        PermissionVo node = new PermissionVo();
        node.setId(permission.getId());
        node.setParentId(permission.getParentId());
        node.setComponent(permission.getComponent());
        node.setName(permission.getName());
        node.setPath(permission.getPath());
        node.setIcon(permission.getIcon());
        node.setTitle(permission.getTitle());
        node.setSort(permission.getSort());
        node.setRedirect(permission.getRedirect());
        return node;
    }

    public Mono<RolePo> getById(Long id) {
        return roleRepository.findById(id)
            .switchIfEmpty(Mono.error(new RuntimeException(I18n.getMessage(MessageConstants.ROLE_NOT_EXISTS))));
    }

    @Transactional
    public Mono<Void> saveWithPermissions(Long permId, String roleIds) {
        // 1. 获取所有权限数据，用于构建父子关系
        return permissionRepository.findAll()
                .collectList()
                .flatMap(allPermissions -> {
                    // 构建父子关系映射
                    Map<Long, List<Permission>> parentToChildren = allPermissions.stream()
                            .filter(p -> p.getParentId() != null)
                            .collect(Collectors.groupingBy(Permission::getParentId));
                    
                    // 递归获取所有子权限ID
                    List<Long> allPermIds = new ArrayList<>();
                    allPermIds.add(permId);
                    getAllChildrenPermIds(permId, parentToChildren, allPermIds);

                    // 2. 删除当前权限及其所有子权限的角色关系
                    return Flux.fromIterable(allPermIds)
                            .flatMap(id -> rolePermissionRepository.deleteByPermId(id))
                            .then(Mono.just(allPermIds));
                })
                .flatMap(allPermIds -> {
                    if (roleIds == null || roleIds.isEmpty()) {
                        return Mono.empty();
                    }

                    // 3. 为当前权限及其所有子权限重新绑定角色
                    return Flux.fromIterable(allPermIds)
                            .flatMap(id -> Flux.fromArray(roleIds.split(","))
                                    .map(Long::parseLong)
                                    .flatMap(roleId -> {
                                        RolePermission rolePermission = new RolePermission();
                                        rolePermission.setRoleId(roleId);
                                        rolePermission.setPermId(id);
                                        return rolePermissionRepository.save(rolePermission);
                                    }))
                            .then();
                });
    }

    private void getAllChildrenPermIds(Long parentId, Map<Long, List<Permission>> parentToChildren, List<Long> result) {
        List<Permission> children = parentToChildren.get(parentId);
        if (children != null) {
            for (Permission child : children) {
                result.add(child.getId());
                getAllChildrenPermIds(child.getId(), parentToChildren, result);
            }
        }
    }

    public Flux<RolePo> findRolesByPermissionId(Long permId) {
        return rolePermissionRepository.findByPermId(permId)
                .flatMap(rp -> roleRepository.findById(rp.getRoleId()));
    }
}
