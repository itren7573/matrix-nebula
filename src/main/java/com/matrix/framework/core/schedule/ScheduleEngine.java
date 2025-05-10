package com.matrix.framework.core.schedule;

import com.matrix.framework.core.schedule.trigger.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.DisposableBean;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.HashSet;

/**
 * 调度引擎
 *
 * Copyright © 海平面工作室 版权所有
 *
 * @Author: Leo
 * @Create: 2024/10/3 11:15
 * @Since 1.0
 */
@Component
public class ScheduleEngine implements DisposableBean {
    private static final Logger logger = LoggerFactory.getLogger(ScheduleEngine.class);
    
    private final ScheduledExecutorService scheduler;
    private final Map<String, ScheduledTask> taskMap = new ConcurrentHashMap<>();
    private final Map<String, ScheduledFuture<?>> futureMap = new ConcurrentHashMap<>();
    
    @Autowired
    public ScheduleEngine(ScheduledExecutorService scheduler) {
        this.scheduler = scheduler;
    }
    
    /**
     * 注册调度任务
     */
    public void registerTask(String taskId, ScheduleTask task, Trigger trigger) {
        if (taskMap.containsKey(taskId)) {
            throw new IllegalArgumentException("Task " + taskId + " already exists");
        }
        
        ScheduledTask scheduledTask = new ScheduledTask(task, trigger);
        taskMap.put(taskId, scheduledTask);
        scheduleNextExecution(taskId, scheduledTask);
        
        logger.info("Task registered: {}, trigger: {}", taskId, trigger.getDescription());
    }
    
    /**
     * 取消调度任务
     */
    public void cancelTask(String taskId) {
        ScheduledFuture<?> future = futureMap.remove(taskId);
        if (future != null) {
            future.cancel(false);
        }
        taskMap.remove(taskId);
        logger.info("Task cancelled: {}", taskId);
    }
    
    private void scheduleNextExecution(String taskId, ScheduledTask scheduledTask) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextExecuteTime = scheduledTask.getNextExecuteTime();
        
        if (nextExecuteTime == null || nextExecuteTime.isBefore(now)) {
            return;
        }
        
        long delay = now.until(nextExecuteTime, java.time.temporal.ChronoUnit.MILLIS);
        ScheduledFuture<?> future = scheduler.schedule(() -> {
            try {
                scheduledTask.execute();
            } finally {
                scheduleNextExecution(taskId, scheduledTask);
            }
        }, delay, TimeUnit.MILLISECONDS);
        
        futureMap.put(taskId, future);
    }
    
    private static class ScheduledTask {
        private final ScheduleTask task;
        private final Trigger trigger;
        private LocalDateTime lastExecuteTime;
        
        public ScheduledTask(ScheduleTask task, Trigger trigger) {
            this.task = task;
            this.trigger = trigger;
        }
        
        public void execute() {
            task.execute();
            lastExecuteTime = LocalDateTime.now();
        }
        
        public LocalDateTime getNextExecuteTime() {
            return trigger.getNextExecuteTime(lastExecuteTime != null ? lastExecuteTime : LocalDateTime.now());
        }
    }
    
    @Override
    public void destroy() throws Exception {
        // 取消所有任务
        for (String taskId : new HashSet<>(taskMap.keySet())) {
            cancelTask(taskId);
        }
        
        // 关闭调度器
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(60, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

//    示例
//    @Service
//    public class YourService {
//        @Autowired
//        private ScheduleEngine scheduleEngine;
//
//        public void setupTasks() {
//            // 使用Cron表达式（每天凌晨1点执行）
//            ScheduleTask task1 = new YourTask();
//            Trigger cronTrigger = new CronTrigger("0 0 1 * * ?");
//            scheduleEngine.registerTask("dailyTask", task1, cronTrigger);
//
//            // 使用固定间隔（每5分钟执行一次）
//            ScheduleTask task2 = new YourTask();
//            Trigger fixedTrigger = new FixedRateTrigger(5, ChronoUnit.MINUTES);
//            scheduleEngine.registerTask("periodicTask", task2, fixedTrigger);
//        }
//    }
} 