package com.matrix.app.mvc.partner.service;

import com.matrix.app.mvc.partner.data.IncomeDictPo;
import com.matrix.app.mvc.partner.repository.IncomeDictRepository;
import com.matrix.framework.core.common.global.IOptionService;
import com.matrix.framework.core.common.global.Options;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Copyright © 海平面工作室 版权所有
 *
 * @Author: Leo
 * @Create: 2025/2/23 18:11
 * @Since 1.0
 */
@Service
public class IncomeDictService implements IOptionService {

    private final IncomeDictRepository repository;

    public IncomeDictService(IncomeDictRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Options> getOptions(Map<String, String> filter) {
        List<IncomeDictPo> pos = repository.findAll();
        return pos.stream().map(p -> new Options(p.getName(), p.getId() + "")).toList();
    }
}
