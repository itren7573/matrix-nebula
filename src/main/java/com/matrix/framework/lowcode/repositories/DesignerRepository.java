package com.matrix.framework.lowcode.repositories;

import com.matrix.framework.lowcode.data.DesignerPo;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 设计器仓库接口
 *
 * Copyright © 海平面工作室 版权所有
 *
 * @Author: Leo
 * @Create: 2024/1/3 11:15
 * @Since 1.0
 */
@Repository
public interface DesignerRepository extends R2dbcRepository<DesignerPo, Long> {
    
    @Query("SELECT * FROM lowcode_designer WHERE (:name IS NULL OR name LIKE CONCAT('%', :name, '%'))")
    Flux<DesignerPo> findByNameLike(@Param("name") String name);

    /**
     * 根据redirect查询设计器配置
     * 
     * @param redirect 重定向标识
     * @return 设计器配置
     */
    Mono<DesignerPo> findByRedirect(String redirect);
} 