package com.matrix.framework.core.thread;

import org.springframework.stereotype.Service;

/**
 * 示例服务
 *
 * Copyright © 海平面工作室 版权所有
 *
 * @Author: Leo
 * @Create: 2024/12/17 21:57
 * @Since 1.0
 */
@Service
public class Example {

    private final ThreadEngine threadEngine;

    public Example(ThreadEngine threadEngine) {
        this.threadEngine = threadEngine;
    }

    public void doSomething() {
        // 提交任务到虚拟线程执行
        String threadId = threadEngine.submit("DataProcessor", () -> {
            // 这里是任务逻辑
            //processData();
        });

        // 获取线程状态
        ThreadInfo info = threadEngine.getThreadInfo(threadId);
        System.out.println("Thread status: " + info.getStatus());
    }
}
