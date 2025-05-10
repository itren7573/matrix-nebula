/*
 Navicat MySQL Data Transfer

 Source Server         : localhost
 Source Server Type    : MariaDB
 Source Server Version : 100618
 Source Host           : localhost:3308
 Source Schema         : matrix

 Target Server Type    : MariaDB
 Target Server Version : 100618
 File Encoding         : 65001

 Date: 23/02/2025 12:26:58
*/

DROP
DATABASE IF EXISTS `matrix`;
CREATE
DATABASE IF NOT EXISTS `matrix` CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE
`matrix`;

SET NAMES utf8mb4;
SET
FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for audit_log
-- ----------------------------
DROP TABLE IF EXISTS `audit_log`;
CREATE TABLE `audit_log`
(
    `id`        bigint(20) NOT NULL AUTO_INCREMENT,
    `username`  varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户ID',
    `action`    varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '操作',
    `module`    varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
    `timestamp` bigint(20) NOT NULL DEFAULT current_timestamp COMMENT '操作时间',
    `details`   text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '操作详情',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for auth_dept
-- ----------------------------
DROP TABLE IF EXISTS `auth_dept`;
CREATE TABLE `auth_dept`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT,
    `pid`         bigint(20) NULL DEFAULT NULL,
    `name`        varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
    `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
    `create_time` bigint(20) NULL DEFAULT NULL,
    `update_time` bigint(20) NULL DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE,
    INDEX         `idx_pid`(`pid`) USING BTREE,
    INDEX         `idx_name`(`name`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of auth_dept
-- ----------------------------
INSERT INTO `auth_dept`
VALUES (1, 0, '用户组', '系统用户组，不可删除', 1732933809000, 1732933809000);
INSERT INTO `auth_dept`
VALUES (2, 1, '管理员组', '系统初始默认的系统管理员账号', 1732933809000, 1732933809000);

-- ----------------------------
-- Table structure for auth_perm
-- ----------------------------
DROP TABLE IF EXISTS `auth_perm`;
CREATE TABLE `auth_perm`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT,
    `parent_id`   bigint(20) NULL DEFAULT NULL COMMENT '父id',
    `name`        varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL COMMENT '菜单名称',
    `title`       varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
    `code`        varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL COMMENT '菜单代号',
    `component`   varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
    `path`        varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
    `icon`        varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
    `sort`        bigint(255) NOT NULL COMMENT '排序',
    `redirect`    varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
    `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '资源描述',
    `attr`        varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 26 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of auth_perm
-- ----------------------------
INSERT INTO `auth_perm`
VALUES (1, 0, 'Dashboard', '主页', 'home', 'BasicLayout', '/', 'lucide:layout-dashboard', 1, '', '主页一级菜单', 'r');
INSERT INTO `auth_perm`
VALUES (2, 1, 'Analytics', '分析页', 'chart', '/dashboard/analytics/index', '/Analytics', 'lucide:area-chart', 2, '',
        '常用统计图表', 'r');
INSERT INTO `auth_perm`
VALUES (3, 1, 'Workspace', '工作台', 'work', '/dashboard/workspace/index', '/workspace', 'carbon:workspace', 14, '',
        '个人工作台', 'r');
INSERT INTO `auth_perm`
VALUES (4, 0, 'Auth', '权限', 'auth', 'BasicLayout', '/auth', 'mdi:shield-key-outline', 4, '', '权限一级菜单', 'r');
INSERT INTO `auth_perm`
VALUES (5, 4, 'UserIndex', '用户管理', 'user', '/auth/user/index', '/user', 'uil:user', 5, '', '用户增删改查', 'r');
INSERT INTO `auth_perm`
VALUES (6, 4, 'RoleIndex', '角色管理', 'role', '/auth/role/index', '/role', 'oui:app-users-roles', 6, '',
        '角色增删改查', 'r');
INSERT INTO `auth_perm`
VALUES (7, 4, 'Menu', '菜单管理', 'menu', '/auth/menu/index', '/menu', 'dashicons:welcome-widgets-menus', 7, '',
        '菜单增删改查', 'r');
INSERT INTO `auth_perm`
VALUES (8, 0, 'system', '系统', 'sys_group', 'BasicLayout', '/system', 'tdesign:system-setting', 8, '', '系统一级菜单',
        'r');
INSERT INTO `auth_perm`
VALUES (9, 8, 'SetupIndex', '系统设置', 'setup', '/system/setup/index', '/setup', 'hugeicons:setup-01', 9, '',
        '系统设置', 'r');
INSERT INTO `auth_perm`
VALUES (10, 0, 'LowCode', '低代码', 'low_code', 'BasicLayout', '/lowcode', 'tabler:api-app', 11, '', '低代码设计器',
        'r');
INSERT INTO `auth_perm`
VALUES (11, 0, 'Audit', '审计', 'audit', 'BasicLayout', '/audit', 'mdi:account-search', 10, '', '审计一级菜单', 'r');
INSERT INTO `auth_perm`
VALUES (12, 11, 'SyslogIndex', '系统日志', 'syslog', '/system/syslog/index', '/syslog', 'radix-icons:activity-log', 12,
        '', '审计日志', 'r');
INSERT INTO `auth_perm`
VALUES (14, 0, 'Partner', '合伙人', 'partner', 'BasicLayout', '/partner', 'mdi:currency-usd', 3, '', '', 'rw');
INSERT INTO `auth_perm`
VALUES (15, 14, 'EquityDict', '权益指标', 'equity_dict', '/partner/equity_dict/index', '/equity_dict', 'mdi:view-list',
        25, '', '', 'rw');
INSERT INTO `auth_perm`
VALUES (16, 14, 'PartnerEquity', '合伙人权益', 'partner_equity', '/lowcode/publish/index', '/partner-equity',
        'mdi:cart', 19, 'Fj5L28IK5wPbgySQV3DB2', '', 'rw');
INSERT INTO `auth_perm`
VALUES (17, 10, 'Designer', '设计器', 'designer', '/lowcode/designer/index', '/designer', 'mdi:tools', 17, '',
        '低代码设计器，用来对业务模型进行定义并持久化', 'rw');
INSERT INTO `auth_perm`
VALUES (18, 10, 'Render', '预研区', 'render', '/lowcode/render/debug-index', '/render', 'mdi:clipboard-text', 18, '',
        '低代码设计器渲染预览', 'rw');
INSERT INTO `auth_perm`
VALUES (19, 14, 'Parnter-List', '合伙人名单', 'parnter-list', '/lowcode/publish/index', '/parnter-list',
        'mdi:account-star', 15, 'jVJgNFvmKwDkyyeqQpckP', '', 'rw');
INSERT INTO `auth_perm`
VALUES (20, 0, 'Demo', '示例', 'demo', 'BasicLayout', '/demo', 'mdi:message-video', 20, '', '', 'rw');
INSERT INTO `auth_perm`
VALUES (21, 20, 'Spring-Demo', 'Spring MVC示例', 'spring-demo', '/demo/index', '/spring-demo', 'mdi:toolbox', 21, '',
        '', 'rw');
INSERT INTO `auth_perm`
VALUES (22, 14, 'Parnter-Cost', '合伙人成本', 'parnter-cost', '/lowcode/publish/index', 'parnter-cost', 'mdi:fire', 16,
        '6Obj2R_JWRhVl5R0CVxn0', '', 'rw');
INSERT INTO `auth_perm`
VALUES (23, 10, 'Publish', '发布区', 'publish', '/lowcode/publish/index', '/publish', 'mdi:database', 23, '', '', 'rw');
INSERT INTO `auth_perm`
VALUES (24, 14, 'income_dict', '收益指标', 'income_dict', '/lowcode/publish/index', '/income_dict',
        'mdi:format-list-numbered', 24, '-t3kmn7JAYxcqvjTwloKG', '', 'rw');
INSERT INTO `auth_perm`
VALUES (25, 14, 'income_detail', '合伙人收益', 'income_detail', '/lowcode/publish/index', '/income_detail', 'mdi:crown',
        22, 'zmER_QnvUciFWr5OGRuk3', '', 'rw');

-- ----------------------------
-- Table structure for auth_role_perm
-- ----------------------------
DROP TABLE IF EXISTS `auth_role_perm`;
CREATE TABLE `auth_role_perm`
(
    `id`      bigint(20) NOT NULL AUTO_INCREMENT,
    `role_id` bigint(20) NOT NULL,
    `perm_id` bigint(20) NOT NULL,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 35 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of auth_role_perm
-- ----------------------------
INSERT INTO `auth_role_perm`
VALUES (1, 2, 1);
INSERT INTO `auth_role_perm`
VALUES (2, 2, 2);
INSERT INTO `auth_role_perm`
VALUES (3, 2, 3);
INSERT INTO `auth_role_perm`
VALUES (4, 2, 8);
INSERT INTO `auth_role_perm`
VALUES (5, 2, 9);
INSERT INTO `auth_role_perm`
VALUES (6, 2, 10);
INSERT INTO `auth_role_perm`
VALUES (7, 3, 4);
INSERT INTO `auth_role_perm`
VALUES (8, 3, 5);
INSERT INTO `auth_role_perm`
VALUES (9, 3, 6);
INSERT INTO `auth_role_perm`
VALUES (10, 3, 7);
INSERT INTO `auth_role_perm`
VALUES (11, 4, 11);
INSERT INTO `auth_role_perm`
VALUES (12, 4, 12);
INSERT INTO `auth_role_perm`
VALUES (18, 2, 14);
INSERT INTO `auth_role_perm`
VALUES (21, 2, 19);
INSERT INTO `auth_role_perm`
VALUES (22, 2, 22);
INSERT INTO `auth_role_perm`
VALUES (30, 2, 16);
INSERT INTO `auth_role_perm`
VALUES (32, 2, 24);
INSERT INTO `auth_role_perm`
VALUES (33, 2, 25);
INSERT INTO `auth_role_perm`
VALUES (34, 2, 15);

-- ----------------------------
-- Table structure for auth_roles
-- ----------------------------
DROP TABLE IF EXISTS `auth_roles`;
CREATE TABLE `auth_roles`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT,
    `name`        varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL COMMENT '角色名',
    `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色描述',
    `attr`        varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NOT NULL COMMENT '属性: r-只读 rw-读写',
    `create_time` bigint(10) NULL DEFAULT NULL,
    `update_time` bigint(10) NULL DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `nameIndex`(`name`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of auth_roles
-- ----------------------------
INSERT INTO `auth_roles`
VALUES (1, '超级管理员', '其他全部角色的总和', 'r', 1728791719148, 1728791719148);
INSERT INTO `auth_roles`
VALUES (2, '系统管理员', '具有业务功能操控的角色', 'r', 1728791719148, 1728791719148);
INSERT INTO `auth_roles`
VALUES (3, '用户管理员', '创建用户,分配功能权限的角色', 'r', 1728791719148, 1728791719148);
INSERT INTO `auth_roles`
VALUES (4, '审计管理员', '查看操控日志,进行安全审计的角色', 'r', 1728791719148, 1728791719148);

-- ----------------------------
-- Table structure for auth_user
-- ----------------------------
DROP TABLE IF EXISTS `auth_user`;
CREATE TABLE `auth_user`
(
    `id`              bigint(20) NOT NULL AUTO_INCREMENT,
    `username`        varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL COMMENT '账号',
    `password`        varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '密码',
    `real_name`       varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL COMMENT '真是姓名',
    `email`           varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '邮箱',
    `phone`           varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '电话',
    `dept_id`         bigint(20) NOT NULL COMMENT '所属部门',
    `attr`            varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NOT NULL COMMENT '属性:r-只读 rw-读写',
    `status`          varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL COMMENT '用户状态 normal\n:正常 pw:密码锁定 timeout:超时锁定 ip:IP锁定 disabled:失效 forbidden:禁用:',
    `language`        varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '语言设置',
    `last_login_time` bigint(10) NULL DEFAULT 0 COMMENT '最后登录时间',
    `create_time`     bigint(10) NULL DEFAULT NULL COMMENT '创建时间',
    `update_time`     bigint(10) NULL DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of auth_user
-- ----------------------------
INSERT INTO `auth_user`
VALUES (1, 'super', '$2a$10$6DoMl7Dl.b6H3Srmy6xR1ePo1IluwzpNZ7h7etioNYp64WGYMADni', 'admin.real.name.super', ' ', ' ',
        2, 'r', 'NORMAL', 'zh_CN', 1740319218910, 1728791719148, 1736603273409);
INSERT INTO `auth_user`
VALUES (2, 'sysadmin', '$2a$10$0DVe8p0uY/tiP6HRJKEPQOjw8EswopJjS12v5gX7YMipFLjmaFKjO', 'admin.real.name.sys', ' ', ' ',
        2, 'r', 'NORMAL', 'zh_CN', 1737267509815, 1728791719148, 1736603272785);
INSERT INTO `auth_user`
VALUES (3, 'useradmin', '$2a$10$3t6rW.vJdvGKqUyqgpRz.eeKnfcBwr2ROldUA7Sbxb0DljeU6qTYi', 'admin.real.name.user', ' ',
        ' ', 2, 'r', 'NORMAL', 'zh_CN', 1737267478646, 1728791719148, 1736603272097);
INSERT INTO `auth_user`
VALUES (4, 'auditadmin', '$2a$10$MhzXyJmDgeq/ZFeTe01u..P/U1zeaMcMpdnMmD8pe798I2I14aGzm', 'admin.real.name.audit', ' ',
        ' ', 2, 'r', 'NORMAL', 'zh_CN', 1737267432997, 1728791719148, 1736603270641);
INSERT INTO `auth_user`
VALUES (5, 'vben', '$2a$10$a0/hN6sQltB5JurRUJG0D.rXLvYGWPStyKblqrU3UumkrJQun4Pue', 'vben', '13911899175@139.com', '123',
        1, 'rw', 'NORMAL', 'zh_CN', 0, 1740193236463, 1740193236463);

-- ----------------------------
-- Table structure for auth_user_role
-- ----------------------------
DROP TABLE IF EXISTS `auth_user_role`;
CREATE TABLE `auth_user_role`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT,
    `user_id`     bigint(20) NOT NULL,
    `role_id`     bigint(20) NOT NULL,
    `create_time` bigint(10) NULL DEFAULT NULL,
    `update_time` bigint(10) NULL DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of auth_user_role
-- ----------------------------
INSERT INTO `auth_user_role`
VALUES (1, 1, 1, 0, 0);
INSERT INTO `auth_user_role`
VALUES (2, 2, 2, 0, 0);
INSERT INTO `auth_user_role`
VALUES (3, 3, 3, 0, 0);
INSERT INTO `auth_user_role`
VALUES (4, 4, 4, 0, 0);
INSERT INTO `auth_user_role`
VALUES (5, 5, 2, 1740193236540, 1740193236540);

-- ----------------------------
-- Table structure for lowcode_designer
-- ----------------------------
DROP TABLE IF EXISTS `lowcode_designer`;
CREATE TABLE `lowcode_designer`
(
    `id`          bigint(64) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `redirect`    varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '定向码',
    `name`        varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '名称',
    `table_name`  varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '表名称',
    `layout`      varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '布局类型',
    `context`     longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '页面定义上下文',
    `attr`        varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL COMMENT '操作属性：r - 只读；rw-读写',
    `create_by`   bigint(255) NULL DEFAULT NULL COMMENT '创建人',
    `update_by`   bigint(20) NOT NULL COMMENT '最近一次修改人',
    `description` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '描述',
    `create_time` bigint(20) NULL DEFAULT NULL COMMENT '创建时间',
    `update_time` bigint(20) NOT NULL COMMENT '最近一次修改时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of lowcode_designer
-- ----------------------------
INSERT INTO `lowcode_designer`
VALUES (2, '6Obj2R_JWRhVl5R0CVxn0', '合伙人成本', 'partner_cost', 'single',
        '{\"fields\":[{\"inTable\":true,\"storage\":true,\"visible\":false,\"width\":\"auto\",\"field\":\"user_id\",\"title\":\"姓名\",\"component\":\"Select\",\"default\":\"1\",\"rule\":[\"required\"],\"ruleLength\":{},\"source\":\"partnerListService\",\"type\":\"enum\",\"sort\":7},{\"inTable\":true,\"storage\":true,\"visible\":false,\"width\":\"auto\",\"field\":\"code\",\"title\":\"成本代码\",\"component\":\"Radio\",\"default\":\"Hard\",\"rule\":[\"required\"],\"ruleLength\":{},\"source\":\"com.matrix.app.mvc.partner.enums.Cost\",\"type\":\"enum\",\"sort\":3},{\"inTable\":true,\"visible\":false,\"width\":\"auto\",\"type\":\"enum\",\"field\":\"cost\",\"title\":\"单价\",\"component\":\"Input\",\"rule\":[\"required\"],\"ruleLength\":{},\"sort\":4},{\"inTable\":true,\"visible\":false,\"width\":\"auto\",\"type\":\"enum\",\"field\":\"amount\",\"title\":\"数量\",\"component\":\"Input\",\"default\":\"1\",\"rule\":[\"required\"],\"ruleLength\":{},\"sort\":5},{\"inTable\":true,\"visible\":false,\"width\":\"auto\",\"type\":\"enum\",\"field\":\"unit\",\"title\":\"单位\",\"component\":\"Input\",\"ruleLength\":{},\"sort\":6},{\"inTable\":true,\"storage\":false,\"visible\":false,\"width\":\"auto\",\"type\":\"enum\",\"field\":\"sum\",\"title\":\"合计\",\"component\":\"Input\",\"ruleLength\":{},\"sort\":8},{\"inTable\":true,\"storage\":true,\"visible\":false,\"width\":\"auto\",\"field\":\"description\",\"title\":\"备注\",\"component\":\"Textarea\",\"ruleLength\":{}},{\"inTable\":false,\"storage\":false,\"visible\":true,\"width\":\"auto\",\"field\":\"create_time\",\"title\":\"登记时间\",\"component\":\"Input\",\"ruleLength\":{},\"type\":\"enum\",\"sort\":1}],\"tableName\":\"partner_cost\",\"layout\":\"single\",\"version\":\"1.0\",\"updateTime\":\"2025-02-22T03:50:41.364Z\"}',
        'rw', NULL, 1, NULL, 1739071807740, 1740196241423);
INSERT INTO `lowcode_designer`
VALUES (3, 'rCVs_mHoFUsceTQSGRCui', '基础单页示例', 'partner-demo', 'single',
        '{\"fields\":[{\"inTable\":true,\"visible\":false,\"width\":\"custom\",\"type\":\"enum\",\"field\":\"name\",\"title\":\"姓名\",\"component\":\"Input\",\"customWidth\":120,\"rule\":[\"required\"],\"ruleLength\":{}},{\"inTable\":true,\"visible\":false,\"width\":\"auto\",\"type\":\"enum\",\"field\":\"age\",\"title\":\"年龄\",\"component\":\"Input\",\"customWidth\":100,\"rule\":[\"required\"],\"ruleLength\":{}},{\"inTable\":true,\"visible\":false,\"width\":\"auto\",\"type\":\"enum\",\"field\":\"sex\",\"title\":\"性别\",\"component\":\"Input\",\"ruleLength\":{}},{\"inTable\":true,\"visible\":false,\"width\":\"auto\",\"type\":\"enum\",\"field\":\"level\",\"title\":\"学历\",\"component\":\"Input\",\"rule\":[\"required\"],\"ruleLength\":{}},{\"inTable\":true,\"storage\":true,\"visible\":false,\"width\":\"auto\",\"type\":\"enum\",\"field\":\"status\",\"title\":\"状态\",\"component\":\"Select\",\"ruleLength\":{},\"source\":\"com.matrix.app.mvc.partner.enums.Equity\",\"cascades\":\"equity\"},{\"inTable\":true,\"visible\":false,\"width\":\"auto\",\"type\":\"service\",\"field\":\"equity\",\"title\":\"权益\",\"component\":\"Select\",\"helper\":\"权益可选项\",\"ruleLength\":{},\"source\":\"equityDictService\",\"dependOn\":\"status\"}],\"tableName\":\"partner-demo\",\"layout\":\"single\",\"version\":\"1.0\",\"updateTime\":\"2025-02-23T14:10:15.967Z\"}',
        'rw', NULL, 1, NULL, 1738808348476, 1740319816008);
INSERT INTO `lowcode_designer`
VALUES (4, 'jVJgNFvmKwDkyyeqQpckP', '合伙人名单', 'partner_list', 'single',
        '{\"fields\":[{\"inTable\":true,\"storage\":true,\"visible\":false,\"width\":\"auto\",\"field\":\"name\",\"title\":\"姓名\",\"component\":\"Input\",\"rule\":[\"required\"],\"ruleLength\":{\"min\":5,\"max\":10},\"type\":\"enum\"},{\"inTable\":true,\"storage\":true,\"visible\":false,\"width\":\"auto\",\"field\":\"sex\",\"title\":\"性别\",\"component\":\"Select\",\"default\":\"Man\",\"rule\":[\"required\"],\"ruleLength\":{},\"source\":\"com.matrix.app.mvc.partner.enums.Sex\",\"type\":\"enum\"},{\"inTable\":true,\"visible\":false,\"width\":\"auto\",\"type\":\"enum\",\"field\":\"phone\",\"title\":\"手机号\",\"component\":\"Input\",\"rule\":[\"required\"],\"ruleLength\":{}},{\"inTable\":true,\"storage\":true,\"visible\":false,\"width\":\"auto\",\"type\":\"enum\",\"field\":\"role\",\"title\":\"角色\",\"component\":\"Select\",\"rule\":[\"required\"],\"ruleLength\":{},\"source\":\"com.matrix.app.mvc.partner.enums.Role\"}],\"tableName\":\"partner_list\",\"layout\":\"single\",\"version\":\"1.0\",\"updateTime\":\"2025-02-20T15:48:28.607Z\"}',
        'rw', NULL, 1, NULL, 1739071796424, 1740066508646);
INSERT INTO `lowcode_designer`
VALUES (5, 'ieVvxe9KArAWff-8tF7_3', '我的家庭', 'demo', 'single',
        '{\"fields\":[{\"inTable\":true,\"visible\":false,\"width\":\"auto\",\"type\":\"enum\",\"field\":\"name\",\"title\":\"姓名\",\"component\":\"Input\",\"ruleLength\":{}},{\"inTable\":true,\"visible\":false,\"width\":\"auto\",\"type\":\"enum\",\"field\":\"sex\",\"title\":\"性别\",\"component\":\"Select\",\"ruleLength\":{},\"source\":\"mvc.partner.enums.Sex\"},{\"inTable\":true,\"visible\":false,\"width\":\"auto\",\"type\":\"enum\",\"field\":\"age\",\"title\":\"年龄\",\"component\":\"Input\",\"ruleLength\":{}}],\"tableName\":\"demo\",\"layout\":\"single\",\"version\":\"1.0\",\"updateTime\":\"2025-02-20T12:36:00.839Z\"}',
        'rw', NULL, 1, NULL, 1739072594686, 1740054960872);
INSERT INTO `lowcode_designer`
VALUES (6, 'Fj5L28IK5wPbgySQV3DB2', '合伙人权益', 'partner_equity', 'single',
        '{\"fields\":[{\"inTable\":true,\"storage\":true,\"visible\":false,\"width\":\"auto\",\"field\":\"user_id\",\"title\":\"姓名\",\"component\":\"Select\",\"rule\":[\"required\"],\"ruleLength\":{},\"source\":\"partnerListService\",\"type\":\"enum\",\"sort\":7},{\"inTable\":true,\"storage\":true,\"visible\":false,\"width\":\"auto\",\"field\":\"equity_id\",\"title\":\"权益项\",\"component\":\"Select\",\"rule\":[\"required\"],\"ruleLength\":{},\"source\":\"equityDictService\"},{\"inTable\":true,\"storage\":false,\"visible\":false,\"width\":\"auto\",\"field\":\"ratio\",\"title\":\"权益占比(%)\",\"component\":\"Input\",\"ruleLength\":{}},{\"inTable\":true,\"storage\":true,\"visible\":false,\"width\":\"auto\",\"field\":\"status\",\"title\":\"状态\",\"component\":\"Select\",\"rule\":[\"required\"],\"ruleLength\":{},\"source\":\"com.matrix.app.mvc.partner.enums.Equity\"}],\"tableName\":\"partner_equity\",\"layout\":\"single\",\"version\":\"1.0\",\"updateTime\":\"2025-02-20T14:56:14.144Z\"}',
        'rw', NULL, 1, NULL, 1740054789490, 1740063374168);
INSERT INTO `lowcode_designer`
VALUES (7, '-t3kmn7JAYxcqvjTwloKG', '合伙人收益指标', 'partner_income_dict', 'single',
        '{\"fields\":[{\"inTable\":true,\"storage\":true,\"visible\":false,\"width\":\"auto\",\"field\":\"name\",\"title\":\"指标名称\",\"component\":\"Input\",\"rule\":[\"required\"],\"ruleLength\":{}},{\"inTable\":true,\"storage\":true,\"visible\":false,\"width\":\"auto\",\"field\":\"royalty_rate\",\"title\":\"提成比例\",\"component\":\"Input\",\"default\":\"0\",\"rule\":[\"required\"],\"ruleLength\":{}},{\"inTable\":true,\"storage\":true,\"visible\":false,\"width\":\"auto\",\"field\":\"description\",\"title\":\"指标说明\",\"component\":\"Textarea\",\"ruleLength\":{}}],\"tableName\":\"partner_income_dict\",\"layout\":\"single\",\"version\":\"1.0\",\"updateTime\":\"2025-02-23T08:40:48.198Z\"}',
        'rw', NULL, 1, '合伙人收益指标字典的定义', 1740299738954, 1740300048233);
INSERT INTO `lowcode_designer`
VALUES (8, 'zmER_QnvUciFWr5OGRuk3', '合伙人收益', 'partner_income_detail', 'single',
        '{\"fields\":[{\"inTable\":true,\"storage\":true,\"visible\":false,\"width\":\"auto\",\"field\":\"dict_id\",\"title\":\"收益名称\",\"component\":\"Select\",\"rule\":[\"required\"],\"ruleLength\":{},\"source\":\"incomeDictService\"},{\"inTable\":true,\"storage\":true,\"visible\":false,\"width\":\"auto\",\"field\":\"income\",\"title\":\"收益金额\",\"component\":\"Input\",\"rule\":[\"required\"],\"ruleLength\":{}},{\"inTable\":true,\"storage\":true,\"visible\":false,\"width\":\"auto\",\"field\":\"partner_ids\",\"title\":\"合伙人\",\"component\":\"SelectMultiple\",\"rule\":[\"required\"],\"ruleLength\":{},\"source\":\"partnerListService\"},{\"inTable\":true,\"storage\":true,\"visible\":false,\"width\":\"auto\",\"field\":\"status\",\"title\":\"状态\",\"component\":\"Radio\",\"default\":\"Arrival\",\"ruleLength\":{},\"source\":\"com.matrix.app.mvc.partner.enums.IncomeStatus\"},{\"inTable\":true,\"storage\":true,\"visible\":false,\"width\":\"custom\",\"field\":\"description\",\"title\":\"描述\",\"component\":\"Textarea\",\"customWidth\":300,\"rule\":[],\"ruleLength\":{},\"source\":\"partnerListService\"}],\"tableName\":\"partner_income_detail\",\"layout\":\"single\",\"version\":\"1.0\",\"updateTime\":\"2025-02-23T12:58:59.197Z\"}',
        'rw', NULL, 1, NULL, 1740304870599, 1740315539246);

-- ----------------------------
-- Table structure for partner-demo
-- ----------------------------
DROP TABLE IF EXISTS `partner-demo`;
CREATE TABLE `partner-demo`
(
    `id`     int(11) NOT NULL AUTO_INCREMENT,
    `name`   varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
    `sex`    varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
    `age`    int(11) NULL DEFAULT NULL,
    `level`  varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
    `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
    `equity` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of partner-demo
-- ----------------------------

-- ----------------------------
-- Table structure for partner_cost
-- ----------------------------
DROP TABLE IF EXISTS `partner_cost`;
CREATE TABLE `partner_cost`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT,
    `user_id`     varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '合伙人id',
    `code`        varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '费用代码',
    `cost`        decimal(10, 2) NULL DEFAULT NULL COMMENT '单价',
    `amount`      int(255) NULL DEFAULT NULL COMMENT '数量',
    `unit`        varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '单位',
    `description` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '描述',
    `create_time` bigint(20) NULL DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 18 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of partner_cost
-- ----------------------------
INSERT INTO `partner_cost`
VALUES (1, '1', 'Hard', 9866.00, 1, '台', '家用台式机-DELL', 1739557043897);
INSERT INTO `partner_cost`
VALUES (2, '1', 'Hard', 7989.00, 1, '台', 'ThinkPad笔记本电脑', 1739975036211);
INSERT INTO `partner_cost`
VALUES (3, '1', 'Hard', 2500.00, 1, '个', 'AOC34吋4K显示器', 1739975195311);
INSERT INTO `partner_cost`
VALUES (4, '1', 'Soft', 117.00, 13, '月', 'Cursor服务费', 1739975151760);
INSERT INTO `partner_cost`
VALUES (5, '1', 'ManHour', 300.00, 656, '小时', '双休+节假日+工作日2小时', 1739974398327);
INSERT INTO `partner_cost`
VALUES (17, '1', 'Hard', 913.00, 1, '台', '15.6吋Eimio便携式2.5K显示器', 1739975362199);

-- ----------------------------
-- Table structure for partner_equity
-- ----------------------------
DROP TABLE IF EXISTS `partner_equity`;
CREATE TABLE `partner_equity`
(
    `id`        bigint(20) NOT NULL AUTO_INCREMENT,
    `user_id`   varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户id',
    `equity_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '权益指标id',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of partner_equity
-- ----------------------------
INSERT INTO `partner_equity`
VALUES (1, '1', '2');
INSERT INTO `partner_equity`
VALUES (2, '1', '4');
INSERT INTO `partner_equity`
VALUES (3, '1', '21');
INSERT INTO `partner_equity`
VALUES (4, '1', '22');
INSERT INTO `partner_equity`
VALUES (5, '2', '6');
INSERT INTO `partner_equity`
VALUES (6, '2', '16');
INSERT INTO `partner_equity`
VALUES (7, '2', '18');
INSERT INTO `partner_equity`
VALUES (8, '3', '5');
INSERT INTO `partner_equity`
VALUES (9, '3', '17');
INSERT INTO `partner_equity`
VALUES (10, '3', '19');
INSERT INTO `partner_equity`
VALUES (11, '6', '7');

-- ----------------------------
-- Table structure for partner_equity_dic
-- ----------------------------
DROP TABLE IF EXISTS `partner_equity_dic`;
CREATE TABLE `partner_equity_dic`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT,
    `pid`         bigint(20) NOT NULL COMMENT '父id',
    `name`        varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '指标名称',
    `ratio`       int(100) NOT NULL COMMENT '占比值',
    `status`      varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '状态',
    `sort`        int(255) NULL DEFAULT NULL COMMENT '排序字段',
    `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '描述',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 27 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of partner_equity_dic
-- ----------------------------
INSERT INTO `partner_equity_dic`
VALUES (1, 0, '开局', 10, 'Done', 1, '万事开头难，启动神农项目打好发展基础，权益总占比10%');
INSERT INTO `partner_equity_dic`
VALUES (2, 1, '构思与基座', 10, 'Done', 6, '构思神农项目发展路线图，完成基座功能的开发，为后续团队工作打好基础');
INSERT INTO `partner_equity_dic`
VALUES (3, 0, '加盟', 40, 'Ready', 3,
        '为团队成员组建提供基础权益，只要完成基本权益门槛要求的工作即可获得所约定的权益，迁移总占比25%');
INSERT INTO `partner_equity_dic`
VALUES (4, 3, '架构师', 10, 'Done', 0,
        '系统与应用架构师，负责神农产品的系统架构与应用架构相关工作。权益门槛：搭建神农rise版本系统架构');
INSERT INTO `partner_equity_dic`
VALUES (5, 3, '前端专家', 10, 'Ready', 2, 'VUE前端专家，能解决前端疑难问题。权益门槛：安管产品某个模块迁移完成');
INSERT INTO `partner_equity_dic`
VALUES (6, 3, '后端专家', 10, 'Ready', 4, 'WebFlux后端专家，能解决后端疑难问题。权益门槛：安管产品某个模块迁移完成');
INSERT INTO `partner_equity_dic`
VALUES (7, 3, '市场专员', 10, 'Ready', 5, '将神农项目变现，权益门槛：可以拉到第一笔投资或寻找到稳定的合作伙伴');
INSERT INTO `partner_equity_dic`
VALUES (11, 0, '退盟', 0, 'Done', 9,
        '完成加盟后才有资格退盟。退盟后已分红的不追回，但后续不再有分红。还未分红的只保留加盟权益，其他权益自动放弃，首次分红只按加盟比例进行一次性分红。');
INSERT INTO `partner_equity_dic`
VALUES (12, 0, '研发', 40, 'Ready', 10, '完成神农项目三阶段目标的权益定义，权益总占比40%');
INSERT INTO `partner_equity_dic`
VALUES (13, 12, '系统架构', 20, 'Ready', 12, '在集成式单体架构基础上继续进行架构演化，权益总占比20%');
INSERT INTO `partner_equity_dic`
VALUES (15, 12, '安管产品', 10, 'Ready', 18, '将网关安管移植到神农架构，完成获得10%权益');
INSERT INTO `partner_equity_dic`
VALUES (16, 15, '资产管理', 5, 'Ready', 14, '完成资产模块代码迁移，获得2%权益');
INSERT INTO `partner_equity_dic`
VALUES (17, 15, '策略管理', 5, 'Ready', 15, '完成策略模块代码迁移，获得2%权益');
INSERT INTO `partner_equity_dic`
VALUES (18, 15, '日志管理', 5, 'Ready', 16, '完成日志模块代码迁移，获得4%权益.专家成长阶段');
INSERT INTO `partner_equity_dic`
VALUES (19, 15, '其他模块', 5, 'Ready', 17, '其他辅助模块代码迁移完成，获得3%权益');
INSERT INTO `partner_equity_dic`
VALUES (21, 13, '可组装单体架构', 5, 'Ready', 11, '神农第二阶段架构目标，完成后获得10%权益');
INSERT INTO `partner_equity_dic`
VALUES (22, 13, '微服务架构', 5, 'Ready', 13, '神农第三阶段架构目标，完成后获得10%权益');
INSERT INTO `partner_equity_dic`
VALUES (23, 0, '市场', 20, 'Ready', 24, '神农系统变现，融资权益总占比20%');

-- ----------------------------
-- Table structure for partner_income_detail
-- ----------------------------
DROP TABLE IF EXISTS `partner_income_detail`;
CREATE TABLE `partner_income_detail`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT,
    `dict_id`     varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '权益指标id',
    `income`      bigint(255) NULL DEFAULT NULL COMMENT '收益(单位-元)',
    `partner_ids` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '合伙人id,id=0表示为捐赠的',
    `status`      varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '状态(purpose-意向,way-在途,arrival-到账)',
    `create_time` bigint(20) NULL DEFAULT NULL COMMENT '登记时间',
    `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '描述说明',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of partner_income_detail
-- ----------------------------
INSERT INTO `partner_income_detail`
VALUES (1, '1', 500, '6', 'Arrival', 1739557043897, NULL);
INSERT INTO `partner_income_detail`
VALUES (2, '2', 100000, '1', 'Way', 1739557043897, 'Leo联系到外包项目XXX.已签合同');
INSERT INTO `partner_income_detail`
VALUES (3, '3', 70000, '2,3', 'Purpose', 1739557043897, 'Easton与Selena合作与xxx公司正在商讨安管产品出售事宜');

-- ----------------------------
-- Table structure for partner_income_dict
-- ----------------------------
DROP TABLE IF EXISTS `partner_income_dict`;
CREATE TABLE `partner_income_dict`
(
    `id`           bigint(20) NOT NULL AUTO_INCREMENT,
    `name`         varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '收益指标名称',
    `royalty_rate` int(255) NULL DEFAULT NULL COMMENT '提成比例',
    `description`  text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '指标说明',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of partner_income_dict
-- ----------------------------
INSERT INTO `partner_income_dict`
VALUES (1, '捐赠', 0, '神农1.0开源版本获得的捐赠款项');
INSERT INTO `partner_income_dict`
VALUES (2, '外包', 20, '市场专员为团队联系到的外包项目收益');
INSERT INTO `partner_income_dict`
VALUES (3, '产品出售', 20, '市场专员完成的基于神农架构基础上实现的产品出售,比如进销存通用产品的销售');
INSERT INTO `partner_income_dict`
VALUES (4, '架构出售', 20, '市场专员促成的神农架构2.0,3.0版本的出售.');
INSERT INTO `partner_income_dict`
VALUES (5, '投资与融资', 0, '获得天使投资或商业投资');
INSERT INTO `partner_income_dict`
VALUES (6, '全面收购', 20, '有实力的公司收购整体团队与产品,实现团队入驻企业');

-- ----------------------------
-- Table structure for partner_list
-- ----------------------------
DROP TABLE IF EXISTS `partner_list`;
CREATE TABLE `partner_list`
(
    `id`    bigint(20) NOT NULL AUTO_INCREMENT,
    `name`  varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
    `sex`   varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
    `phone` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
    `role`  varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of partner_list
-- ----------------------------
INSERT INTO `partner_list`
VALUES (1, 'Leo', 'Man', '13911988173', 'architect');
INSERT INTO `partner_list`
VALUES (2, 'Easton', 'Man', '139xxxxxxxx', 'Back');
INSERT INTO `partner_list`
VALUES (3, 'Selena', 'Woman', '136xxxxxxxx', 'Front');
INSERT INTO `partner_list`
VALUES (6, 'Unknow', 'Man', '139xxxxxxxx', 'Market');

SET
FOREIGN_KEY_CHECKS = 1;

