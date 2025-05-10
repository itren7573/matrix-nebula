package com.matrix.framework.core.loader.impl;

import com.matrix.framework.auth.service.RoleService;
import com.matrix.framework.core.loader.ILoader;
import com.matrix.framework.core.schedule.ScheduleEngine;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * 角色加载器
 *
 * Copyright © 海平面工作室 版权所有
 *
 * @Author: Leo
 * @Create: 2024/10/3 11:15
 */
@Service
@Order(1)  // 设置加载优先级
public class RoleLoader implements ILoader {
    
    private final RoleService roleService;

    private ScheduleEngine scheduleEngine;

    public RoleLoader(RoleService roleService, ScheduleEngine scheduleEngine) {
        this.roleService = roleService;
        this.scheduleEngine = scheduleEngine;
    }

    @Override
    public String getServiceName() {
        return "角色服务";
    }

    @Override
    public Mono<Void> load() {
        System.out.println("加载角色服务");
        return Mono.fromRunnable(() -> {
//            ScheduleTask task2 =  new ScheduleTask() {
//                @Override
//                public void execute() {
//                    System.out.println("测试调度器是否正常工作");
//                }
//
//                @Override
//                public String getTaskName() {
//                    return "";
//                }
//
//                @Override
//                public LocalDateTime getLastExecuteTime() {
//                    return null;
//                }
//
//                @Override
//                public LocalDateTime getNextExecuteTime() {
//                    return null;
//                }
//
//                // 实现其他必要的抽象方法
//                // 请根据 ScheduleTask 接口的具体定义添加其他方法
//            };
//            Trigger fixedTrigger = new FixedRateTrigger(1, ChronoUnit.MINUTES);
//            scheduleEngine.registerTask("periodicTask", task2, fixedTrigger);
        });
    }
} 