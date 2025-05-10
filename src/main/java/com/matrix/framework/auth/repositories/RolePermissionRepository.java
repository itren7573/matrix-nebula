package com.matrix.framework.auth.repositories;

import com.matrix.framework.auth.data.RolePermission;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.data.r2dbc.repository.Query;

public interface RolePermissionRepository extends ReactiveCrudRepository<RolePermission, Long> {
    Mono<Void> deleteByRoleId(Long roleId);
    @Query("SELECT * FROM auth_role_perm WHERE role_id = :roleId")
    Flux<RolePermission> findByRoleId(Long roleId);
    @Query("DELETE FROM auth_role_perm WHERE perm_id = :permId")
    Mono<Void> deleteByPermId(Long permId);
    @Query("SELECT * FROM auth_role_perm WHERE perm_id = :permId")
    Flux<RolePermission> findByPermId(Long permId);
} 