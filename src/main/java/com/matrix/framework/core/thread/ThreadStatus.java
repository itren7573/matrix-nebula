package com.matrix.framework.core.thread;

/**
 * 线程状态枚举类
 *
 * Copyright © 海平面工作室 版权所有
 *
 * @Author: Leo
 * @Create: 2024/10/3 11:15 
 * @Since 1.0
 */
public enum ThreadStatus {
    CREATED,    // 已创建
    RUNNING,    // 运行中
    COMPLETED,  // 已完成
    ERROR       // 出错
} 