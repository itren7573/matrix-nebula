package com.matrix.framework.auth.repositories;

import com.matrix.framework.auth.data.Permission;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Copyright © 雪球工作室 版权所有
 *
 * @Author: 李鹏
 * @Create: 2024/10/7 13:26
 * @Since 1.0
 */
@Repository
public interface PermissionRepository extends R2dbcRepository<Permission, Long> {
    Flux<Permission> findByParentId(Long parentId);

    @Query("SELECT * FROM auth_perm WHERE name = :name")
    Mono<Permission> findByName(String name);

    @Query("SELECT * FROM auth_perm ORDER BY sort")
    Flux<Permission> findAllByOrderByOrderAsc();

    // 查询所有带分页的记录
    Flux<Permission> findAllBy(Pageable pageable);

    // 根据 id 或 parentId 查询
    @Query("SELECT * FROM auth_perm WHERE id = :id OR parent_id = :parentId ORDER BY `sort`")
    Flux<Permission> findByIdOrParentId(Long id, Long parentId, Pageable pageable);

    // 统计 id 或 parentId 匹配的记录数
    @Query("SELECT COUNT(*) FROM auth_perm WHERE id = :id OR parent_id = :parentId")
    Mono<Long> countByIdOrParentId(Long id, Long parentId);

    @Query("DELETE FROM auth_perm WHERE parent_id = :parentId")
    Mono<Void> deleteByParentId(Long parentId);
}
