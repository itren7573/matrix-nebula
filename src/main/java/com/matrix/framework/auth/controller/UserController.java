package com.matrix.framework.auth.controller;

import com.matrix.framework.auth.data.AlertPwVo;
import com.matrix.framework.auth.data.UserPo;
import com.matrix.framework.auth.data.UserRoleVo;
import com.matrix.framework.auth.data.UserVo;
import com.matrix.framework.auth.service.UserRoleService;
import com.matrix.framework.auth.service.UserService;
import com.matrix.framework.core.annotation.validation.LogCollector;
import com.matrix.framework.core.annotation.validation.Valid;
import com.matrix.framework.core.common.result.Result;
import com.matrix.framework.core.i18n.I18n;
import com.matrix.framework.core.i18n.MessageConstants;
import com.matrix.framework.core.sse.SseController;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 用户管理控制器
 *
 * Copyright © 海平面工作室 版权所有
 *
 * @Author: Leo
 * @Create: 2024/10/3 6:11
 * @Since 1.0
 */
@RestController
@RequestMapping("/auth/user")
public class UserController {

    final SseController sseController;

    final UserService userService;

    final PasswordEncoder passwordEncoder;

    final UserRoleService userRoleService;

    public UserController(SseController sseController, UserService userService, PasswordEncoder passwordEncoder, UserRoleService userRoleService) {
        this.sseController = sseController;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.userRoleService = userRoleService;
    }

    @LogCollector
    @PostMapping("/save")
    public Mono<Result<Object>> save(@Valid @RequestBody UserVo userVo, ServerWebExchange request) {
        // 对密码进行加密
        userVo.setPassword(passwordEncoder.encode(userVo.getPassword() == null? userVo.getUsername() + "@123" : userVo.getPassword()));
        
        // 将UserVo转换为UserPo
        UserPo userPo = new UserPo();
        BeanUtils.copyProperties(userVo, userPo);

        // 调用服务层保存用户信息
        Mono<Result<Object>> result;
        if (userVo.getId() == null) {
            result = userService.save(userPo, userVo.getRoles())
                    .map(u -> Result.ok())
                    .onErrorResume(e -> Mono.just(Result.checkFail(userVo.getUsername() + e.getMessage())));
        } else {
            result = userService.update(userPo, userVo.getRoles())
                    .map(u -> Result.ok())
                    .onErrorResume(e -> Mono.just(Result.checkFail(userVo.getUsername() + e.getMessage())));
        }

        return result;
    }

    @GetMapping("/list")
    public Mono<Result<Map<String, Object>>> list(
            ServerWebExchange request,
            @RequestParam("page") int page,
            @RequestParam("pageSize") int size,
            @RequestParam(value = "username", required = false) String username,
            @RequestParam(value = "deptId", required = false) String deptId,
            @RequestParam(value = "realName", required = false) String realName,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "phone", required = false) String phone) {

        UserVo params = new UserVo();
        params.setUsername(username);
        params.setDeptId(deptId == null || deptId.equals("1")? null : Long.parseLong(deptId));
        params.setRealName(realName);
        params.setEmail(email);
        params.setPhone(phone);

        PageRequest pageable = PageRequest.of(page - 1, size);

        return userService.findUsers(pageable, params)
                .flatMap(result -> Mono.just(Result.ok(result)));
    }

    @DeleteMapping("/deleteByDeptId")
    public Mono<Result<Object>> deleteByDeptId(@RequestParam("deptId") String deptId) {
        return userService.deleteByDeptId(Long.parseLong(deptId))
                .then(Mono.just(Result.ok().message(I18n.getMessage(MessageConstants.DELETE_SUCCESS))))
                .onErrorResume(e -> {
                    Result<Object> errResult = Result.fail();
                    errResult.setMessage(e.getMessage());
                    return Mono.just(errResult);
                });
    }

    /**
     * 获取用户的角色列表
     */
    @GetMapping("/{userId}/roles")
    public Mono<Result<List<UserRoleVo>>> getUserRoles(@PathVariable Long userId) {
        return userRoleService.findByUserId(userId)
            .collectList()
            .map(Result::ok)
            .defaultIfEmpty(Result.ok(new ArrayList<>()));
    }

    /**
     * 重置用户密码
     * @param id 用户ID
     * @return Mono<Result<Object>>
     */
    @LogCollector(detail = MessageConstants.AUTH_USER_RESET_PASSWORD)
    @PostMapping("/{id}/reset-password")
    public Mono<Result<Object>> resetPassword(@PathVariable Long id, ServerWebExchange request) {
        return userService.getById(id)
            .flatMap(user -> userService.resetPassword(user.getId())
                .thenReturn(Result.<Object>ok(user).message(I18n.getMessage(MessageConstants.USER_PASSWORD_RESET_SUCCESS))))
            .onErrorResume(e -> Mono.just(Result.fail().message(e.getMessage())));
    }

    @LogCollector
    @DeleteMapping("/delete")
    public Mono<Result<Object>> delete(@RequestParam("id") String id, ServerWebExchange request) {
        return userService.getById(Long.parseLong(id))  // 先查询
                .flatMap(user -> userService.delete(user.getId())  // 再删除
                        .thenReturn(Result.<Object>ok(user)  // 将查询到的用户对象放入Result，明确指定泛型类型
                                .message(I18n.getMessage(MessageConstants.DELETE_SUCCESS))))
                .onErrorResume(e -> Mono.just(Result.<Object>fail()  // 错误处理时也需要明确指定泛型类型
                        .message(I18n.getMessage(MessageConstants.DELETE_FAIL) + e.getMessage())));
    }

    /**
     * 修改密码
     */
    @LogCollector(detail = MessageConstants.AUTH_USER_CHANGE_PASSWORD)
    @PostMapping("/alterPw")
    public Mono<Result<Object>> changePassword(@RequestBody AlertPwVo params, ServerWebExchange request) {
        String oldPassword = params.getOldPassword();
        String newPassword = params.getNewPassword();
        Long userId = Long.parseLong(params.getUserId());

        return userService.getById(userId)
            .flatMap(user -> userService.changePassword(user.getId(), oldPassword, newPassword)
                    .thenReturn(Result.<Object>ok(user).message(I18n.getMessage(MessageConstants.USER_PASSWORD_CHANGE_SUCCESS))))
                .onErrorResume(e -> Mono.just(Result.fail().message(e.getMessage())));
    }

    /**
     * 转移用户到新部门
     * @param userId 用户ID
     * @param deptId 新部门ID
     * @return Mono<Result<Object>>
     */
    @LogCollector(detail = MessageConstants.AUTH_USER_TRANSFER)
    @PostMapping("/{userId}/transfer")
    public Mono<Result<Object>> transferDept(
            @PathVariable Long userId,
            @RequestParam("deptId") Long deptId, ServerWebExchange request) {
        return userService.getById(userId)
            .flatMap(user -> userService.transferDept(user.getId(), deptId)
                .thenReturn(Result.<Object>ok(user).message(I18n.getMessage(MessageConstants.USER_TRANSFER_SUCCESS))))
            .onErrorResume(e -> Mono.just(Result.fail().message(e.getMessage())));
    }

    /**
     * 锁定用户
     * @param userId 用户ID
     * @return Mono<Result<Object>>
     */
    @LogCollector(detail = MessageConstants.AUTH_USER_LOCK)
    @PostMapping("/{userId}/lock")
    public Mono<Result<Object>> lockUser(@PathVariable Long userId, ServerWebExchange request) {
        return userService.getById(userId)
            .flatMap(user -> userService.lockUser(user.getId())
                .thenReturn(Result.<Object>ok(user).message(I18n.getMessage(MessageConstants.USER_LOCK_SUCCESS))))
            .onErrorResume(e -> Mono.just(Result.fail().message(e.getMessage())));
    }

    /**
     * 解锁用户
     * @param userId 用户ID
     * @return Mono<Result<Object>>
     */
    @LogCollector(detail = MessageConstants.AUTH_USER_UNLOCK)
    @PostMapping("/{userId}/unlock")
    public Mono<Result<Object>> unlockUser(@PathVariable Long userId, ServerWebExchange request) {
        return userService.getById(userId)
            .flatMap(user -> userService.unlockUser(user.getId())
                .thenReturn(Result.<Object>ok(user).message(I18n.getMessage(MessageConstants.USER_UNLOCK_SUCCESS))))
            .onErrorResume(e -> Mono.just(Result.fail().message(e.getMessage())));
    }
}
