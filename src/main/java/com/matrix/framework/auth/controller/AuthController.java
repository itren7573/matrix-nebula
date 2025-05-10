package com.matrix.framework.auth.controller;

import com.matrix.framework.auth.data.UserPo;
import com.matrix.framework.auth.data.UserVo;
import com.matrix.framework.auth.service.AuthService;
import com.matrix.framework.core.annotation.validation.LogCollector;
import com.matrix.framework.core.common.enums.auth.UserLock;
import com.matrix.framework.core.common.result.Result;
import com.matrix.framework.core.component.Jwt;
import com.matrix.framework.core.i18n.I18n;
import com.matrix.framework.core.i18n.MessageConstants;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.function.Function;

/**
 * 用户登录认证控制器
 *
 * Copyright © 海平面工作室 版权所有
 *
 * @Author: Leo
 * @Create: 2024/10/3 11:15
 * @Since 1.0
 */
//@Api(tags = "用户认证接口")
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    private final PasswordEncoder passwordEncoder;

    private final Jwt jwt;


    public AuthController(AuthService authService, PasswordEncoder passwordEncoder, Jwt jwt) {
        this.authService = authService;
        this.passwordEncoder = passwordEncoder;
        this.jwt = jwt;
    }

    @LogCollector
    @PostMapping("/login")
    public Mono<ResponseEntity<Result<UserVo>>> login(@RequestBody UserVo loginRequest, ServerWebExchange request) {
        return authService.findByUsername(loginRequest.getUsername())
                .publishOn(Schedulers.boundedElastic())
                .flatMap(user -> handleLogin(user, loginRequest.getPassword()))
                .switchIfEmpty(Mono.defer(this::createLoginFailResponse));
    }

    /**
     * 处理登录逻辑
     */
    private Mono<ResponseEntity<Result<UserVo>>> handleLogin(UserPo user, String password) {
        // 检查用户状态
        if (!UserLock.NORMAL.name().equals(user.getStatus())) {
            return createLockedUserResponse(user.getStatus());
        }

        // 验证密码
        if (!passwordEncoder.matches(password, user.getPassword())) {
            return createLoginFailResponse();
        }

        // 处理登录成功
        return handleSuccessfulLogin(user);
    }

    /**
     * 处理登录成功的情况
     */
    private Mono<ResponseEntity<Result<UserVo>>> handleSuccessfulLogin(UserPo user) {
        user.setLastLoginTime(System.currentTimeMillis());
        return authService.updateLastLoginTime(user)
                .then(Mono.defer(() -> createSuccessfulLoginResponse(user)));
    }

    /**
     * 创建成功登录响应
     */
    private Mono<ResponseEntity<Result<UserVo>>> createSuccessfulLoginResponse(UserPo user) {
        String token = jwt.generateToken("" + user.getId(), user.getUsername());
        UserVo userVo = new UserVo();
        userVo.setAccessToken(token);

        return authService.getUserRoles(user.getUsername())
                .map(roleResult -> {
                    userVo.setId(user.getId());
                    userVo.setUsername(user.getUsername());
                    userVo.setRealName(user.getRealName());
                    userVo.setPassword("******");
                    userVo.setRoles(roleResult.getRoles());
                    return ResponseEntity.ok(Result.ok(userVo));
                });
    }

    /**
     * 创建锁定用户响应
     */
    private Mono<ResponseEntity<Result<UserVo>>> createLockedUserResponse(String status) {
        UserLock lockStatus = UserLock.valueOf(status);
        String message = I18n.getMessage(lockStatus.getDescription()) +
                "，" + I18n.getMessage(MessageConstants.AUTH_USER_UNLOCK_TIME_LIMIT);
        return Mono.just(ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(Result.fail(new UserVo())
                        .code(HttpStatus.FORBIDDEN.value())
                        .message(message)));
    }

    /**
     * 创建登录失败响应
     */
    private Mono<ResponseEntity<Result<UserVo>>> createLoginFailResponse() {
        return Mono.just(ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(Result.fail(new UserVo())
                        .code(HttpStatus.FORBIDDEN.value())
                        .message(I18n.getMessage(MessageConstants.AUTH_USERNAME_PASSWORD_ERROR))));
    }

    /**
     * 获取 Authorization 并提取用户名
     */
    private Mono<String> extractUsername(ServerHttpRequest request) {
        try {
            String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String authToken = authHeader.substring(7);
                return Mono.justOrEmpty(jwt.extractUsername(authToken));
            }
        } catch (Exception e) {
            return Mono.empty();
        }
        return Mono.empty();
    }

    /**
     * 通用处理授权和执行操作的方法
     */
    private Mono<ResponseEntity<Result<Object>>> handleAuthorizedRequest(ServerHttpRequest request, Function<String, Mono<Result<Object>>> action) {

        return extractUsername(request)
                .flatMap(username -> action.apply(username)
                        
                        .map(ResponseEntity::ok))
                        .defaultIfEmpty(ResponseEntity
                                .status(HttpStatus.UNAUTHORIZED)
                                .body(Result.fail()
                                        .code(HttpStatus.UNAUTHORIZED.value())
                                        .message(I18n.getMessage(MessageConstants.AUTH_NOT_LOGIN))));
    }

    /**
     * 获取用户信息
     */
    @GetMapping("user/info")
    public Mono<ResponseEntity<Result<Object>>> info(ServerHttpRequest request) {
        return handleAuthorizedRequest(request, username ->
                authService.getUserRoles(username)
                        .map(user -> {
                            user.setPassword("******"); // 隐藏用户密码
                            return Result.ok(user);
                        })
        );
    }

    /**
     * 获取用户页面操作的权限码
     */
    @GetMapping("codes")
    public Mono<ResponseEntity<Result<Object>>> codes(ServerHttpRequest request) {
        return handleAuthorizedRequest(request, username ->
                authService.getUserPermissionCodes(username)
                        .collectList()
                        .map(codesList -> Result.ok(codesList.toArray(new String[0]))) // 返回权限码数组
        );
    }

    /**
     * 获取用户菜单列表
     */
    @GetMapping("menu/all")
    public Mono<ResponseEntity<Result<Object>>> getUserMenus(ServerHttpRequest request) {
        return handleAuthorizedRequest(request, username ->
                authService.getUserMenus(username).map(Result::ok)
        );
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public Mono<ResponseEntity<Result<Object>>> logout(ServerWebExchange request) {
        return Mono.just(ResponseEntity.ok(Result.ok()
                .message(I18n.getMessage(MessageConstants.AUTH_LOGOUT_SUCCESS))));
    }

}
