package com.matrix.framework.core.config;

import com.matrix.framework.core.component.MatrixJwtTokenFilter;
import org.springframework.boot.autoconfigure.security.reactive.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * 响应式安全认证配置类,细化到方法级别
 * <p>
 * Copyright © 海平面工作室 版权所有
 *
 * @Author: Leo
 * @Create: 2024/10/3 11:15
 * @Since 1.0
 */
@Configuration
@EnableReactiveMethodSecurity
public class MatrixSecurityConfiguration {

    private final ReactiveUserDetailsService userDetailsService;
    private final MatrixJwtTokenFilter jwtAuthenticationFilter;

    public MatrixSecurityConfiguration(ReactiveUserDetailsService userDetailsService, MatrixJwtTokenFilter jwtAuthenticationFilter) {
        this.userDetailsService = userDetailsService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }
    @Bean
    SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        // 定义哪些请求需要认证，哪些不需要
        http
            .csrf(ServerHttpSecurity.CsrfSpec::disable)   // 禁用CSRF(即允许跨域访问)
            .authorizeExchange(authorize -> {
                // 允许所有人访问静态资源
                authorize.matchers(PathRequest.toStaticResources().atCommonLocations()).permitAll();
                // 允许所有人访问/login页面
                authorize.pathMatchers("/auth/**").permitAll();
                // 其他请求都需要认证
                authorize.anyExchange().authenticated();
            })
            // 配置认证规则: 去数据库查询用户信息，然后进行认证
            .authenticationManager(new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService))
            // 使用 JWT 验证过滤器
            .securityContextRepository(jwtAuthenticationFilter);
        return http.build();
    }

    @Primary
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
