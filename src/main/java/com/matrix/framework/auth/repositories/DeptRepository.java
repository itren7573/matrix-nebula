package com.matrix.framework.auth.repositories;

import com.matrix.framework.auth.data.DeptPo;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface DeptRepository extends R2dbcRepository<DeptPo, Long> {
    Flux<DeptPo> findByPid(Long pid);
    
    Flux<DeptPo> findByNameContaining(String name);
} 