package com.matrix.framework.core.thread;

/**
 * 线程信息类
 *
 * Copyright © 海平面工作室 版权所有
 *
 * @Author: Leo 
 * @Create: 2024/10/3 11:15
 * @Since 1.0
 */
public class ThreadInfo {
    private final String threadId;
    private final String name;
    private final Thread thread;
    private ThreadStatus status;
    private final long startTime;

    private long endTime;
    private Exception error;

    public ThreadInfo(String threadId, String name, Thread thread) {
        this.threadId = threadId;
        this.name = name;
        this.thread = thread;
        this.status = ThreadStatus.CREATED;
        this.startTime = System.currentTimeMillis();
    }

    // Getters
    public String getThreadId() {
        return threadId;
    }

    public String getName() {
        return name;
    }

    public Thread getThread() {
        return thread;
    }

    public ThreadStatus getStatus() {
        return status;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public Exception getError() {
        return error;
    }

    // Setters
    public void setStatus(ThreadStatus status) {
        this.status = status;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public void setError(Exception error) {
        this.error = error;
    }

    public long getDuration() {
        if (endTime == 0) {
            return System.currentTimeMillis() - startTime;
        }
        return endTime - startTime;
    }
} 