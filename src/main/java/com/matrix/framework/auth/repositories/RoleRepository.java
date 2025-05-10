package com.matrix.framework.auth.repositories;

import com.matrix.framework.auth.data.RolePo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
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
public interface RoleRepository extends ReactiveCrudRepository<RolePo, Long> {

    // 查询所有带分页的记录
    Flux<RolePo> findAllBy(Pageable pageable);

    // 根据名字进行模糊搜索并分页
    Flux<RolePo> findByNameContaining(String name, Pageable pageable);

    // 根据查询条件获取总数
    Mono<Long> countByNameContaining(String name);
}
