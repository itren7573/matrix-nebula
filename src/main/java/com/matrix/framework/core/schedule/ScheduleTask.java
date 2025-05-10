package com.matrix.framework.core.schedule;

import java.time.LocalDateTime;

/**
 * 调度任务接口
 *
 * Copyright © 海平面工作室 版权所有
 *
 * @Author: Leo
 * @Create: 2024/10/3 11:15
 * @Since 1.0
 */
public interface ScheduleTask {
    /**
     * 执行任务
     */
    void execute();
    
    /**
     * 获取任务名称
     */
    String getTaskName();
    
    /**
     * 获取上次执行时间
     */
    LocalDateTime getLastExecuteTime();
    
    /**
     * 获取下次执行时间
     */
    LocalDateTime getNextExecuteTime();
} 