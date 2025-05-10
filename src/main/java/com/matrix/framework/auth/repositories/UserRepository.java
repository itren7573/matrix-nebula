package com.matrix.framework.auth.repositories;

import com.matrix.framework.auth.data.Permission;
import com.matrix.framework.auth.data.UserPo;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 用户管理持久化层
 * Copyright © 雪球工作室 版权所有
 *
 * @Author: 李鹏
 * @Create: 2024/10/3 11:00
 * @Since 1.0
 */
@Repository
public interface UserRepository extends R2dbcRepository<UserPo, Long> {

    Mono<UserPo> findByUsername(String username);

    @Query("SELECT " +
            "    p.code " +
            "FROM " +
            "    auth_user u " +
            "JOIN " +
            "    auth_user_role ur ON u.id = ur.user_id " +
            "JOIN  " +
            "    auth_roles r ON ur.role_id = r.id " +
            "JOIN  " +
            "    auth_role_perm rp ON r.id = rp.role_id " +
            "JOIN  " +
            "    auth_perm p ON rp.perm_id = p.id " +
            "WHERE  " +
            "    u.username = :username ")
    Flux<String> getUserPermissionCodes(@Param("username") String username);

    @Query("SELECT " +
            "    p.* " +
            "FROM " +
            "    auth_user u " +
            "JOIN " +
            "    auth_user_role ur ON u.id = ur.user_id " +
            "JOIN  " +
            "    auth_roles r ON ur.role_id = r.id " +
            "JOIN  " +
            "    auth_role_perm rp ON r.id = rp.role_id " +
            "JOIN  " +
            "    auth_perm p ON rp.perm_id = p.id " +
            "WHERE  " +
            "    u.username = :username ")
    Flux<Permission> getUserPermissionObjects(@Param("username") String username);

    Flux<UserPo> findAllBy(Pageable pageable);

    @Query("SELECT " +
            "    r.name AS roles " +
            "FROM " +
            "    auth_user u " +
            "JOIN " +
            "    auth_user_role ur ON u.id = ur.user_id " +
            "JOIN  " +
            "    auth_roles r ON ur.role_id = r.id " +
            "WHERE  " +
            "    u.username = :username ")
    Flux<String> getUserRoles(String username);

    Mono<Void> deleteByDeptId(Long deptId);

    @Query("DELETE FROM auth_user WHERE dept_id IN (:deptIds)")
    Mono<Void> deleteByDeptIdIn(@Param("deptIds") List<Long> deptIds);

    @Query("UPDATE auth_user SET password = :password, update_time = :updateTime WHERE id = :id")
    Mono<Void> updatePassword(Long id, String password, Long updateTime);

    @Query("UPDATE auth_user SET password = :newPassword, update_time = :updateTime WHERE id = :id AND password = :oldPassword")
    Mono<Boolean> updatePasswordWithCheck(Long id, String oldPassword, String newPassword, Long updateTime);
}
