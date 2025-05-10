package com.matrix.app.mvc.demo.service.impl;

import com.matrix.app.mvc.demo.data.Demo;
import com.matrix.app.mvc.demo.repository.DemoRepository;
import com.matrix.app.mvc.demo.service.DemoService;
import com.matrix.framework.core.i18n.I18n;
import com.matrix.framework.core.i18n.MessageConstants;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 测试服务实现类
 *
 * Copyright © 海平面工作室 版权所有
 *
 * @Author: Leo
 * @Create: 2024/10/3 11:15
 */
@Service
public class DemoServiceImpl implements DemoService {

    private final DemoRepository demoRepository;

    public DemoServiceImpl(DemoRepository demoRepository) {
        this.demoRepository = demoRepository;
    }

    @Override
    @Transactional
    public Demo save(Demo demo) {
        return demoRepository.save(demo);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        demoRepository.deleteById(id);
    }

    @Override
    public Demo getById(Integer id) {
        return demoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(I18n.getMessage(MessageConstants.RESPONSE_DATA_ERROR)));
    }

    @Override
    public Map<String, Object> findDemos(Pageable pageable, String name, String sex, String level) {
        List<Demo> demos = demoRepository.findByConditions(name, sex, level);
        
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), demos.size());
        
        Page<Demo> page = new PageImpl<>(
                demos.subList(start, end),
                pageable,
                demos.size()
        );

        Map<String, Object> result = new HashMap<>();
        result.put("items", page.getContent());
        result.put("total", page.getTotalElements());
        
        return result;
    }
} 