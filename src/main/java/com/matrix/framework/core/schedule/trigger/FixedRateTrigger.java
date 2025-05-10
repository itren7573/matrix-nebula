package com.matrix.framework.core.schedule.trigger;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * 固定间隔触发器
 *
 * Copyright © 海平面工作室 版权所有
 *
 * @Author: Leo
 * @Create: 2024/10/3 11:15
 * @Since 1.0
 */
public class FixedRateTrigger implements Trigger {
    private final long interval;
    private final ChronoUnit timeUnit;
    
    public FixedRateTrigger(long interval, ChronoUnit timeUnit) {
        this.interval = interval;
        this.timeUnit = timeUnit;
    }
    
    @Override
    public LocalDateTime getNextExecuteTime(LocalDateTime lastExecuteTime) {
        return lastExecuteTime.plus(interval, timeUnit);
    }
    
    @Override
    public String getDescription() {
        return "FixedRate[" + interval + " " + timeUnit + "]";
    }
} 