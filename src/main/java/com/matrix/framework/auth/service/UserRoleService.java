package com.matrix.framework.auth.service;

import com.matrix.framework.auth.data.UserRolePo;
import com.matrix.framework.auth.data.UserRoleVo;
import com.matrix.framework.auth.repositories.UserRoleRepository;
import com.matrix.framework.core.i18n.I18n;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import com.matrix.framework.core.i18n.MessageConstants;
import java.time.Duration;

/**
 * Copyright © 雪球工作室 版权所有
 *
 * @Author: 李鹏
 * @Create: 2024/10/3 11:02
 * @Since 1.0
 */
@Service
public class UserRoleService {

    final UserRoleRepository userRoleRepository;
    final RoleService roleService;

    public UserRoleService(UserRoleRepository userRoleRepository, RoleService roleService) {
        this.userRoleRepository = userRoleRepository;
        this.roleService = roleService;
    }

    @Transactional
    public Mono<UserRolePo> save(UserRolePo userRole) {
        userRole.setCreateTime(System.currentTimeMillis());
        userRole.setUpdateTime(System.currentTimeMillis());
        return userRoleRepository.save(userRole)
            .delayElement(Duration.ofMillis(100));
    }

    @Transactional
    public Mono<Void> delete(Long id) {
        return userRoleRepository.deleteById(id)
            .delayElement(Duration.ofMillis(100));
    }

    @Transactional
    public Mono<Void> deleteByUserId(Long userId) {
        return userRoleRepository.deleteByUserId(userId)
            .delayElement(Duration.ofMillis(100));
    }

    @Transactional
    public Mono<UserRolePo> update(UserRolePo userRole) {
        userRole.setUpdateTime(System.currentTimeMillis());
        return userRoleRepository.save(userRole)
            .delayElement(Duration.ofMillis(100));
    }

    public Flux<UserRoleVo> findByUserId(Long userId) {
        return userRoleRepository.findByUserId(userId)
            .flatMap(userRole -> roleService.findById(userRole.getRoleId())
                .map(role -> {
                    UserRoleVo vo = new UserRoleVo();
                    BeanUtils.copyProperties(userRole, vo);
                    vo.setRoleName(role.getName());
                    return vo;
                }));
    }

    public Flux<UserRolePo> findByRoleId(Long roleId) {
        return userRoleRepository.findByRoleId(roleId);
    }

    public Mono<Boolean> existsByUserIdAndRoleId(Long userId, Long roleId) {
        return userRoleRepository.existsByUserIdAndRoleId(userId, roleId);
    }

    public Flux<UserRolePo> findAll() {
        return userRoleRepository.findAll();
    }

    public Mono<UserRolePo> findById(Long id) {
        return userRoleRepository.findById(id);
    }

    public Mono<UserRolePo> getById(Long id) {
        return userRoleRepository.findById(id)
            .switchIfEmpty(Mono.error(new RuntimeException(I18n.getMessage(MessageConstants.USER_ROLE_NOT_EXISTS))));
    }
}