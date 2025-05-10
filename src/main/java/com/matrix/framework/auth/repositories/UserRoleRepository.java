package com.matrix.framework.auth.repositories;

import com.matrix.framework.auth.data.UserRolePo;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Copyright © 雪球工作室 版权所有
 *
 * @Author: 李鹏
 * @Create: 2024/10/3 11:02
 * @Since 1.0
 */
public interface UserRoleRepository extends ReactiveCrudRepository<UserRolePo, Long> {
    
    Flux<UserRolePo> findByUserId(Long userId);
    
    Flux<UserRolePo> findByRoleId(Long roleId);
    
    @Query("DELETE FROM auth_user_role WHERE user_id = :userId")
    Mono<Void> deleteByUserId(Long userId);
    
    Mono<Boolean> existsByUserIdAndRoleId(Long userId, Long roleId);
} 