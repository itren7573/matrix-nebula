package com.matrix.framework.core.schedule.trigger;

import java.time.LocalDateTime;

/**
 * 触发器接口
 *
 * Copyright © 海平面工作室 版权所有
 *
 * @Author: Leo
 * @Create: 2024/10/3 11:15
 * @Since 1.0
 */
public interface Trigger {
    /**
     * 计算下次执行时间
     * @param lastExecuteTime 上次执行时间
     * @return 下次执行时间
     */
    LocalDateTime getNextExecuteTime(LocalDateTime lastExecuteTime);
    
    /**
     * 获取触发器描述
     */
    String getDescription();
} 