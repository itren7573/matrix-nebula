package com.matrix.framework.audit.repositories;

import com.matrix.framework.audit.data.AuditLogPo;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

/**
 * 审计日志仓库
 *
 * Copyright © 海平面工作室 版权所有
 *
 * @Author: Leo
 * @Create: 2024/10/3 11:15
 * @Since 1.0
 */
@Repository
public interface AuditLogRepository extends R2dbcRepository<AuditLogPo, Long> {
} 