package com.matrix.framework.core.component;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


/**
 * Copyright © 海平面工作室 版权所有
 *
 * @Author: Leo
 * @Create: 2024/10/3 11:15
 * @Since 1.0
 */
@Component
public class MatrixJwtTokenFilter implements ServerSecurityContextRepository {

    private final Jwt jwtUtil;

    private final ReactiveUserDetailsService userDetailsService;

    public MatrixJwtTokenFilter(Jwt jwtUtil, ReactiveUserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Mono<Void> save(ServerWebExchange swe, SecurityContext sc) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Mono<SecurityContext> load(ServerWebExchange swe) {
        String authHeader = swe.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String authToken = authHeader.substring(7);

            try {
                String username = jwtUtil.extractUsername(authToken);

                if (username != null && jwtUtil.validateToken(authToken, username)) {
                    return userDetailsService.findByUsername(username)
                            .map(userDetails ->
                                    new SecurityContextImpl(new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()))
                            );
                } else {
                    // Token 无效或过期
                    return Mono.empty(); // 返回未认证状态
                }
            } catch (Exception e) {
                // 捕获解析过程中的异常，例如 Token 格式错误、签名错误等
                return Mono.empty(); // 返回未认证状态
            }
        }
        return Mono.empty(); // 没有 Authorization 头部，返回未认证状态
    }

}
