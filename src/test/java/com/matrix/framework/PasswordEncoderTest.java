package com.matrix.framework;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 为系统用户获取初始密码的加密测试类
 *
 * Copyright © 雪球工作室 版权所有
 *
 * @Author: 李鹏
 * @Create: 2024/10/3 15:18
 * @Since 1.0
 */
public class PasswordEncoderTest {

    @Test
    public void testPasswordEncryption() {
        // 定义要加密的密码
        String rawPassword = "Admin@123";

        // 创建 BCryptPasswordEncoder 实例
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        // 对密码进行加密
        String encodedPassword = passwordEncoder.encode(rawPassword);

        // 打印加密后的密文
        System.out.println("加密后的密码：" + encodedPassword);

    }

}
