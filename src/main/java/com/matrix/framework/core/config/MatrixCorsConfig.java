package com.matrix.framework.core.config;

import org.springframework.lang.NonNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

/**
 * 设置前端配置,允许跨域访问
 *
 * Copyright © 海平面工作室 版权所有
 *
 * @Author: Leo
 * @Create: 2024/10/3 11:15
 * @Since 1.0
 */
@Configuration
public class MatrixCorsConfig implements WebFluxConfigurer {

    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        registry.addMapping("/**")  // 对所有路径生效
                .allowedOrigins("http://localhost:5666")  // 允许的前端地址
                .allowedMethods("*")  // 允许所有 HTTP 方法
                .allowedHeaders("*")  // 允许所有请求头
                .allowCredentials(true);  // 允许携带凭证（如 Cookie）
    }

}
