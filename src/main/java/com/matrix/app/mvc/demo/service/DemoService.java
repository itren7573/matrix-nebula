package com.matrix.app.mvc.demo.service;

import com.matrix.app.mvc.demo.data.Demo;
import org.springframework.data.domain.Pageable;

import java.util.Map;

/**
 * 测试服务接口
 *
 * Copyright © 海平面工作室 版权所有
 *
 * @Author: Leo
 * @Create: 2024/10/3 11:15
 * @Since 1.0
 */
public interface DemoService {
    Demo save(Demo demo);
    void delete(Integer id);
    Demo getById(Integer id);
    Map<String, Object> findDemos(Pageable pageable, String name, String sex, String level);
} 