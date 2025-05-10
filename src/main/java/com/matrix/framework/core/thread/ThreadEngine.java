package com.matrix.framework.core.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 虚拟线程工厂, 提供一个统一的入口使用虚拟线程执行任务, 并记录线程信息
 *
 * Copyright © 海平面工作室 版权所有
 *
 * @Author: Leo
 * @Create: 2024/12/17 22:10
 * @Since 1.0
 */
@Component
public class ThreadEngine {
    private static final Logger logger = LoggerFactory.getLogger(ThreadEngine.class);
    
    // 存储正在运行的线程信息
    private final Map<String, ThreadInfo> runningThreads = new ConcurrentHashMap<>();
    private final AtomicInteger threadCounter = new AtomicInteger(0);
    
    // 虚拟线程工厂
    private final ThreadFactory virtualThreadFactory = Thread.ofVirtual()
            .name("virtual-", 0)
            .factory();

    /**
     * 提交任务到虚拟线程执行
     * @param name 线程名称
     * @param task 任务
     * @return 线程ID
     */
    public String submit(String name, Runnable task) {
        String threadId = generateThreadId(name);
        Thread thread = virtualThreadFactory.newThread(() -> {
            try {
                logger.info("Thread started: {}", threadId);
                runningThreads.get(threadId).setStatus(ThreadStatus.RUNNING);
                task.run();
            } catch (Exception e) {
                logger.error("Thread error: {}", threadId, e);
                runningThreads.get(threadId).setStatus(ThreadStatus.ERROR);
                runningThreads.get(threadId).setError(e);
            } finally {
                runningThreads.get(threadId).setStatus(ThreadStatus.COMPLETED);
                runningThreads.get(threadId).setEndTime(System.currentTimeMillis());
                logger.info("Thread completed: {}", threadId);
            }
        });

        // 记录线程信息
        ThreadInfo threadInfo = new ThreadInfo(threadId, name, thread);
        runningThreads.put(threadId, threadInfo);
        
        // 启动线程
        thread.start();
        return threadId;
    }

    /**
     * 获取线程运行状态
     * @param threadId 线程ID
     * @return 线程信息
     */
    public ThreadInfo getThreadInfo(String threadId) {
        return runningThreads.get(threadId);
    }

    /**
     * 获取所有运行中的线程信息
     */
    public Map<String, ThreadInfo> getAllThreads() {
        return runningThreads;
    }

    private String generateThreadId(String name) {
        return name + "-" + threadCounter.incrementAndGet();
    }
} 