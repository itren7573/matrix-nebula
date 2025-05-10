package com.matrix.framework.core.loader.impl;

import com.matrix.framework.auth.service.UserService;
import com.matrix.framework.core.loader.ILoader;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * 用户加载器
 *
 * Copyright © 海平面工作室 版权所有
 *
 * @Author: Leo
 * @Create: 2024/10/3 11:15
 */
@Service
@Order(2)
public class UserLoader implements ILoader {
    
    private final UserService userService;

    public UserLoader(UserService userService) {
        this.userService = userService;
    }

    @Override
    public String getServiceName() {
        return "用户服务";
    }

    @Override
    public Mono<Void> load() {
        System.out.println("加载用户服务");
        return Mono.fromRunnable(() -> {
            // 这里放置加载逻辑
        });
    }
} 