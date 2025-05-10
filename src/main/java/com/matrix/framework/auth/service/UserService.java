package com.matrix.framework.auth.service;

import com.matrix.framework.auth.data.UserPo;
import com.matrix.framework.auth.data.UserRolePo;
import com.matrix.framework.auth.data.UserVo;
import com.matrix.framework.auth.repositories.UserRepository;
import com.matrix.framework.auth.repositories.UserRoleRepository;
import com.matrix.framework.core.common.enums.auth.UserLock;
import com.matrix.framework.core.component.Jwt;
import com.matrix.framework.core.i18n.MessageConstants;
import com.matrix.framework.core.common.utils.DateTime;
import com.matrix.framework.core.i18n.I18n;
import org.springframework.data.domain.Pageable;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Copyright © 雪球工作室 版权所有
 *
 * @Author: 李鹏
 * @Create: 2024/10/3 11:02
 * @Since 1.0
 */
@Service
public class UserService {

    final UserRepository userRepository;

    final UserRoleRepository userRoleRepository;

    private final DatabaseClient databaseClient;

    private final DeptService deptService;

    private final PasswordEncoder passwordEncoder;

    private final Jwt jwt;

    public UserService(UserRepository userRepository,
                       UserRoleRepository userRoleRepository,
                       DatabaseClient databaseClient,
                       DeptService deptService,
                       PasswordEncoder passwordEncoder, Jwt jwt) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.databaseClient = databaseClient;
        this.deptService = deptService;
        this.passwordEncoder = passwordEncoder;
        this.jwt = jwt;
    }

    // 提取处理用户角色关联的公共方法
    private <T> Mono<T> handleUserRoles(Long userId, T returnValue, String[] roleIds) {
        if (roleIds != null && roleIds.length > 0) {
            return Flux.fromArray(roleIds)
                .flatMap(roleId -> {
                    UserRolePo userRole = new UserRolePo();
                    userRole.setUserId(userId);
                    userRole.setRoleId(Long.parseLong(roleId));
                    return userRoleRepository.save(userRole);
                })
                .then(Mono.just(returnValue));
        }
        return Mono.just(returnValue);
    }

    // 创建用户并关联角色
    @Transactional
    public Mono<UserPo> save(UserPo user, String[] roles) {
        return userRepository.findByUsername(user.getUsername())
            .flatMap(existingUser -> Mono.<UserPo>error(new RuntimeException(I18n.getMessage(MessageConstants.RESPONSE_CHECK_EXIST))))
            .switchIfEmpty(
                userRepository.save(user)
                    .flatMap(savedUser -> handleUserRoles(savedUser.getId(), savedUser, roles))
                    .delayElement(Duration.ofMillis(100))
            );
    }

    // 更新用户并更新角色关联
    @Transactional
    public Mono<UserPo> update(UserPo user, String[] roleIds) {
        return userRepository.findByUsername(user.getUsername())
            .flatMap(existingUser -> {
                if (!existingUser.getId().equals(user.getId())) {
                    return Mono.error(new RuntimeException(I18n.getMessage(MessageConstants.RESPONSE_CHECK_EXIST)));
                }
                return userRepository.save(user)
                    .flatMap(updatedUser -> 
                        userRoleRepository.deleteByUserId(updatedUser.getId())
                            .then(handleUserRoles(updatedUser.getId(), updatedUser, roleIds))
                    )
                    .delayElement(Duration.ofMillis(100));
            });
    }
    
    // 删除用户及其角色关联
    @Transactional
    public Mono<Void> delete(long id) {
        return userRoleRepository.deleteByUserId(id)
            .then(userRepository.deleteById(id))
            .delayElement(Duration.ofMillis(500));
    }

    // 为用户添加角色
    @Transactional
    public Mono<UserRolePo> addRoleToUser(Long userId, Long roleId) {
        return userRoleRepository.existsByUserIdAndRoleId(userId, roleId)
            .flatMap(exists -> {
                if (exists) {
                    return Mono.error(new RuntimeException(I18n.getMessage(MessageConstants.USER_ROLE_EXISTS)));
                }
                UserRolePo userRole = new UserRolePo();
                userRole.setUserId(userId);
                userRole.setRoleId(roleId);
                return userRoleRepository.save(userRole);
            });
    }

    // 获取用户的所有角色ID
    public Flux<Long> getUserRoleIds(Long userId) {
        return userRoleRepository.findByUserId(userId)
            .map(UserRolePo::getRoleId);
    }

    public Mono<Map<String, Object>> findUsers(Pageable pageable, UserVo params) {
        // 添加延迟确保在查询前数据库操作已完成
        return Mono.delay(Duration.ofMillis(100))
            .then(Mono.defer(() -> {
                StringBuilder baseQueryBuilder = new StringBuilder("SELECT * FROM auth_user WHERE true");
                StringBuilder countQueryBuilder = new StringBuilder("SELECT COUNT(*) FROM auth_user WHERE true");

                List<String> conditions = new ArrayList<>();
                List<Object> values = new ArrayList<>();

                if (params.getUsername() != null) {
                    conditions.add("username LIKE ?");
                    values.add("%" + params.getUsername() + "%");
                }
                if (params.getRealName() != null) {
                    conditions.add("real_name LIKE ?");
                    values.add("%" + params.getRealName() + "%");
                }
                if (params.getEmail() != null) {
                    conditions.add("email LIKE ?");
                    values.add("%" + params.getEmail() + "%");
                }
                if (params.getPhone() != null) {
                    conditions.add("phone LIKE ?");
                    values.add("%" + params.getPhone() + "%");
                }
                if (params.getDeptId() != null) {
                    conditions.add("dept_id = ?");
                    values.add(params.getDeptId());
                }

                for (String condition : conditions) {
                    baseQueryBuilder.append(" AND ").append(condition);
                    countQueryBuilder.append(" AND ").append(condition);
                }
                baseQueryBuilder.append(" ORDER BY id");
                // Adding pagination to the query
                if (pageable.isPaged()) {
                    baseQueryBuilder.append(" LIMIT ").append(pageable.getPageSize()).append(" OFFSET ").append(pageable.getOffset());
                }

                DatabaseClient.GenericExecuteSpec baseSpec = databaseClient.sql(baseQueryBuilder.toString());
                DatabaseClient.GenericExecuteSpec countSpec = databaseClient.sql(countQueryBuilder.toString());

                for (int i = 0; i < values.size(); i++) {
                    baseSpec = baseSpec.bind(i, values.get(i));
                    countSpec = countSpec.bind(i, values.get(i));
                }

                Flux<UserPo> users = baseSpec.map((row, metadata) -> {
                    UserVo user = new UserVo();
                    user.setId(row.get("id", Long.class));
                    user.setUsername(row.get("username", String.class));
                    user.setRealName(row.get("real_name", String.class));
                    user.setEmail(row.get("email", String.class));
                    user.setPhone(row.get("phone", String.class));
                    user.setAttr(row.get("attr", String.class));
                    user.setStatus(row.get("status", String.class));
                    user.setDeptId(row.get("dept_id", Long.class));
                    user.setCreateTime(row.get("create_time", Long.class));
                    user.setUpdateTime(row.get("update_time", Long.class));
                    user.setLastLoginTime(row.get("last_login_time", Long.class));
                    return user;
                })
                .all()
                .flatMap(user -> getUserRoleIds(user.getId())
                    .map(String::valueOf)
                    .collectList()
                    .map(roleIds -> roleIds.toArray(new String[0]))
                    .map(roles -> {
                        user.setRoles(roles);
                        return user;
                    }));

                Mono<Long> total = countSpec.map((row, metadata) -> row.get("COUNT(*)", Long.class)).one();

                return users.collectList()
                        .flatMap(list -> total.map(count -> {
                            List<UserVo> dtoList = list.stream().map(this::convertToVo).collect(Collectors.toList());
                            Map<String, Object> result = new HashMap<>();
                            result.put("items", dtoList);
                            result.put("total", count);
                            return result;
                        }));
            }));
    }

    private UserVo convertToVo(UserPo userPo) {
        UserVo vo = new UserVo();
        vo.setId(userPo.getId());
        vo.setUsername(userPo.getUsername());
        if (userPo.getAttr().equals("r") && userPo.getRealName().startsWith("admin.real.name.")) {
            vo.setRealName(I18n.getMessage(userPo.getRealName()));
        } else {
            vo.setRealName(userPo.getRealName());
        }
        vo.setEmail(userPo.getEmail());
        vo.setPhone(userPo.getPhone());
        vo.setAttr(userPo.getAttr());
        vo.setStatus(UserLock.valueOf(userPo.getStatus()).getDescription());
        vo.setLastLoginTimeStr(userPo.getLastLoginTime() == 0L ? "" : DateTime.format(userPo.getLastLoginTime()));
        vo.setCreateTimeStr(DateTime.format(userPo.getCreateTime()));
        vo.setUpdateTimeStr(DateTime.format(userPo.getUpdateTime()));
        return vo;
    }

    public Mono<Void> deleteByDeptId(Long deptId) {
        return deptService.getAllSubDeptIds(deptId)
                .collectList()
                .flatMap(deptIds -> {
                    deptIds.add(deptId);
                    return userRepository.deleteByDeptIdIn(deptIds);
                });
    }

    /**
     * 重置用户密码
     * @param id 用户ID
     * @return Mono<Void>
     */
    public Mono<Void> resetPassword(Long id) {
        return userRepository.findById(id)
            .flatMap(user -> {
                String newPassword = passwordEncoder.encode(user.getAttr().equals("r") ? "Admin@123" : user.getUsername() + "@123");
                return userRepository.updatePassword(id, newPassword, System.currentTimeMillis())
                    .then();
            });
    }

    /**
     * 修改密码
     * @param id 用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return Mono<Void>
     */
    @Transactional
    public Mono<Void> changePassword(Long id, String oldPassword, String newPassword) {
        if (oldPassword == null || newPassword == null) {
            return Mono.<Void>error(new RuntimeException(I18n.getMessage(MessageConstants.USER_PASSWORD_EMPTY)));
        }

        if (oldPassword.equals(newPassword)) {
            return Mono.<Void>error(new RuntimeException(I18n.getMessage(MessageConstants.USER_PASSWORD_SAME)));
        }

        // 验证新密码格式
        if (!newPassword.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$")) {
            return Mono.<Void>error(new RuntimeException(I18n.getMessage(MessageConstants.USER_PASSWORD_FORMAT_INVALID)));
        }

        return userRepository.findById(id)
            .<Void>flatMap(user -> {
                if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
                    return Mono.<Void>error(new RuntimeException(I18n.getMessage(MessageConstants.USER_PASSWORD_OLD_INCORRECT)));
                }
                String encodedNewPassword = passwordEncoder.encode(newPassword);
                return userRepository.updatePasswordWithCheck(
                    id, 
                    user.getPassword(), 
                    encodedNewPassword, 
                    System.currentTimeMillis()
                )
                .flatMap(updated -> {
                    if (!updated) {
                        return Mono.<Void>error(new RuntimeException(I18n.getMessage(MessageConstants.USER_PASSWORD_CHANGE_FAILED)));
                    }
                    return Mono.empty();
                });
            })
            .delayElement(Duration.ofMillis(100));
    }

    /**
     * 转移用户到新部门
     * @param userId 用户ID
     * @param newDeptId 新部门ID
     * @return Mono<Void>
     */
    @Transactional
    public Mono<Void> transferDept(Long userId, Long newDeptId) {
        return userRepository.findById(userId)
            .switchIfEmpty(Mono.error(new RuntimeException(I18n.getMessage(MessageConstants.USER_NOT_EXIST, userId))))
            .flatMap(user -> {
                user.setDeptId(newDeptId);
                user.setUpdateTime(System.currentTimeMillis());
                return userRepository.save(user).then();
            });
    }

    /**
     * 锁定用户
     * @param userId 用户ID
     * @return Mono<Void>
     */
    @Transactional
    public Mono<Void> lockUser(Long userId) {
        return userRepository.findById(userId)
            .switchIfEmpty(Mono.error(new RuntimeException(I18n.getMessage(MessageConstants.USER_NOT_EXIST, userId))))
            .flatMap(user -> {
                user.setStatus(UserLock.FORBIDDEN.name());
                user.setUpdateTime(System.currentTimeMillis());
                return userRepository.save(user).then();
            });
    }

    /**
     * 解锁用户
     * @param userId 用户ID
     * @return Mono<Void>
     */
    @Transactional
    public Mono<Void> unlockUser(Long userId) {
        return userRepository.findById(userId)
            .switchIfEmpty(Mono.error(new RuntimeException(I18n.getMessage(MessageConstants.USER_NOT_EXIST, userId))))
            .flatMap(user -> {
                user.setStatus(UserLock.NORMAL.name());
                user.setUpdateTime(System.currentTimeMillis());
                return userRepository.save(user).then();
            });
    }

    public Mono<UserPo> getById(Long id) {
        return userRepository.findById(id)
            .switchIfEmpty(Mono.error(new RuntimeException(I18n.getMessage(MessageConstants.USER_NOT_EXIST, id))));
    }

    public Mono<UserPo> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public long getUserIdByToken(String token) {
        return Long.parseLong(jwt.extractUserId(token));
    }
}
