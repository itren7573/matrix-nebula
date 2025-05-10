package com.matrix.framework.core.i18n;

/**
 * 国际化常量类
 *
 * Copyright © 海平面工作室 版权所有
 *
 * @Author: Leo
 * @Create: 2024/10/3 11:15
 * @Since 1.0
 */
public class MessageConstants {
    // 通用消息
    public static final String DELETE_SUCCESS = "delete.success";

    public static final String DELETE_FAIL = "delete.fail";
    public static final String SAVE_SUCCESS = "save.success";
    public static final String SAVE_FAIL = "save.fail";
    public static final String UPDATE_SUCCESS = "update.success";
    public static final String UPDATE_FAIL = "update.fail";
    public static final String QUERY_SUCCESS = "query.success";
    public static final String NEW_DATA = "new.data.log.heard";
    public static final String MDF_DATA = "modify.data.log.heard";

    // 用户锁定状态相关
    public static final String USER_LOCK_NORMAL = "user.lock.normal";
    public static final String USER_LOCK_PW = "user.lock.pw";
    public static final String USER_LOCK_TIMEOUT = "user.lock.timeout";
    public static final String USER_LOCK_IP = "user.lock.ip";
    public static final String USER_LOCK_DISABLED = "user.lock.disabled";
    public static final String USER_LOCK_FORBIDDEN = "user.lock.forbidden";
    
    // 用户相关消息
    public static final String USER_NOT_EXIST = "user.not.exist";
    public static final String USER_PASSWORD_OLD_INCORRECT = "user.password.old.incorrect";
    public static final String USER_PASSWORD_CHANGE_FAILED = "user.password.change.failed";
    public static final String USER_PASSWORD_EMPTY = "user.password.empty";
    public static final String USER_PASSWORD_FORMAT_INVALID = "user.password.format.invalid";
    public static final String USER_PASSWORD_CHANGE_SUCCESS = "user.password.change.success";
    public static final String USER_PASSWORD_RESET_SUCCESS = "user.password.reset.success";
    public static final String USER_PASSWORD_SAME = "user.password.same.as.old";
    public static final String USER_ROLE_EXISTS = "user.role.exists";
    public static final String USER_TRANSFER_SUCCESS = "user.transfer.success";
    public static final String USER_LOCK_SUCCESS = "user.lock.success";
    public static final String USER_UNLOCK_SUCCESS = "user.unlock.success";
    public static final String USER_NOT_EXISTS = "user.not.exists";

    // 角色相关消息
    public static final String ROLE_NAME_EXISTS = "role.name.exists";
    public static final String ROLE_DELETE_SUCCESS = "role.delete.success";
    public static final String ROLE_SAVE_SUCCESS = "role.save.success";
    public static final String ROLE_SUPER_ADMIN_NAME = "role.super_admin.name";
    public static final String ROLE_SUPER_ADMIN_DESCRIPTION = "role.super_admin.description";
    public static final String ROLE_SYS_ADMIN_NAME = "role.sys_admin.name";
    public static final String ROLE_SYS_ADMIN_DESCRIPTION = "role.sys_admin.description";
    public static final String ROLE_USER_ADMIN_NAME = "role.user_admin.name";
    public static final String ROLE_USER_ADMIN_DESCRIPTION = "role.user_admin.description";
    public static final String ROLE_AUDIT_ADMIN_NAME = "role.audit_admin.name";
    public static final String ROLE_AUDIT_ADMIN_DESCRIPTION = "role.audit_admin.description";
    public static final String ROLE_NOT_EXISTS = "role.not.exists";

    // 部门相关消息
    public static final String DEPT_DELETE_SUCCESS = "dept.delete.success";
    public static final String DEPT_NOT_EXISTS = "dept.not.exists";

    // 认证相关消息
    public static final String AUTH_NOT_LOGIN = "auth.not.login";
    public static final String AUTH_USERNAME_PASSWORD_ERROR = "auth.username.password.error";
    public static final String AUTH_LOGOUT_SUCCESS = "auth.logout.success";

    // 系统响应消息
    public static final String RESPONSE_SUCCESS = "response.success";
    public static final String RESPONSE_FAIL = "response.fail";
    public static final String RESPONSE_SERVICE_ERROR = "response.service.error";
    public static final String RESPONSE_DATA_ERROR = "response.data.error";
    public static final String RESPONSE_ILLEGAL_REQUEST = "response.illegal.request";
    public static final String RESPONSE_REPEAT_SUBMIT = "response.repeat.submit";
    public static final String RESPONSE_ARGUMENT_ERROR = "response.argument.error";
    public static final String RESPONSE_LOGIN_AUTH = "response.login.auth";
    public static final String RESPONSE_PERMISSION = "response.permission";
    public static final String RESPONSE_ACCOUNT_ERROR = "response.account.error";
    public static final String RESPONSE_PASSWORD_ERROR = "response.password.error";
    public static final String RESPONSE_LOGIN_MOBILE_ERROR = "response.login.mobile.error";
    public static final String RESPONSE_ACCOUNT_STOP = "response.account.stop";
    public static final String RESPONSE_NODE_ERROR = "response.node.error";
    public static final String RESPONSE_CHECK_FAIL = "response.check.fail";
    public static final String RESPONSE_CHECK_EXIST = "response.check.exist";

    // 在 MessageConstants 类中添加以下常量
    public static final String ADMIN_REAL_NAME_SUPER = "admin.real.name.super";
    public static final String ADMIN_REAL_NAME_SYS = "admin.real.name.sys";
    public static final String ADMIN_REAL_NAME_USER = "admin.real.name.user";
    public static final String ADMIN_REAL_NAME_AUDIT = "admin.real.name.audit";

    // 验证相关消息
    public static final String VALIDATION_REQUIRED = "validation.required";
    public static final String VALIDATION_LENGTH = "validation.length";
    public static final String VALIDATION_EMAIL = "validation.email";
    public static final String VALIDATION_IP = "validation.ip";
    public static final String VALIDATION_URL = "validation.url";
    public static final String AUTH_USER_UNLOCK_TIME_LIMIT = "auth.user.unlock.time.limit";

    // 菜单相关消息
    public static final String MENU_NOT_EXISTS = "menu.not.exists";

    // 用户角色关联相关消息
    public static final String USER_ROLE_NOT_EXISTS = "user.role.not.exists";

    // 用户操作相关消息
    public static final String AUTH_USER_RESET_PASSWORD = "auth.user.reset.password";
    public static final String AUTH_USER_CHANGE_PASSWORD = "auth.user.change.password";
    public static final String AUTH_USER_TRANSFER = "auth.user.transfer";
    public static final String AUTH_USER_LOCK = "auth.user.lock";
    public static final String AUTH_USER_UNLOCK = "auth.user.unlock";

    // 权限相关消息
    public static final String PERMISSION_NAME_EXISTS = "permission.name.exists";
    public static final String PERMISSION_NOT_EXISTS = "permission.not.exists";
    public static final String PERMISSION_DELETE_SUCCESS = "permission.delete.success";
    public static final String PERMISSION_SAVE_SUCCESS = "permission.save.success";

}