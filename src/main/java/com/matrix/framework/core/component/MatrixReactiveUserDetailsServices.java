package com.matrix.framework.core.component;

import com.matrix.framework.auth.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * 为LoongSecurityConfiguration提供用户详情查询服务
 * 去数据库中查询用户信息，并返回UserDetails对象
 * <p>
 * Copyright © 海平面工作室 版权所有
 *
 * @Author: Leo
 * @Create: 2024/10/3 3:05
 * @Since 1.0
 */
@Component
public class MatrixReactiveUserDetailsServices implements ReactiveUserDetailsService {

    @Autowired
    AuthService loginService;

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return loginService.getUserPermission(username)
                .map(user -> User.builder()
                            .username(user.getUsername())
                            .password(user.getPassword())  // 不需要重新编码密码
                            .roles(user.getRoles())        // 传递权限数组
                            .build()
                );
    }

}
