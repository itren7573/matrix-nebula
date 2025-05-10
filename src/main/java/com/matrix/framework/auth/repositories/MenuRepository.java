package com.matrix.framework.auth.repositories;

import com.matrix.framework.auth.data.MenuPo;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MenuRepository extends R2dbcRepository<MenuPo, Long> {
    // 可以在这里添加自定义查询方法
} 