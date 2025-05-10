package com.matrix.framework.core.schedule.trigger;

import org.springframework.scheduling.support.CronExpression;
import java.time.LocalDateTime;

/**
 * Cron表达式触发器
 *
 * Copyright © 海平面工作室 版权所有
 *
 * @Author: Leo
 * @Create: 2024/10/3 11:15
 * @Since 1.0
 */
public class CronTrigger implements Trigger {
    private final CronExpression cronExpression;
    private final String expression;
    
    public CronTrigger(String cronExpression) {
        this.expression = cronExpression;
        this.cronExpression = CronExpression.parse(cronExpression);
    }
    
    @Override
    public LocalDateTime getNextExecuteTime(LocalDateTime lastExecuteTime) {
        return cronExpression.next(lastExecuteTime);
    }
    
    @Override
    public String getDescription() {
        return "Cron[" + expression + "]";
    }
} 