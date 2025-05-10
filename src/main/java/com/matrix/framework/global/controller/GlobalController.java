package com.matrix.framework.global.controller;

import com.matrix.framework.core.common.global.IOptionService;
import com.matrix.framework.core.common.global.SpringUtil;
import com.matrix.framework.core.common.global.TypeDefDto;
import com.matrix.framework.core.common.global.Options;
import com.matrix.framework.core.common.result.Result;
import com.matrix.framework.core.common.utils.Common;
import com.matrix.framework.lowcode.eventintf.data.ModelOption;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * 全局控制器类，提供统一的option接口
 * Copyright © 海平面工作室
 *
 * @author: Leo
 * @create: 2025/1/29
 * @since 1.0
 */
@RestController
@RequestMapping("/global")
public class GlobalController {

    public static final List<ModelOption> ALL_OPTIONS = new ArrayList<>();

    @GetMapping("/getAllOptionListInTable")
    public Result<List<Options>> getAllOptionListInTable(@RequestParam String tableName) {
        List<Options> optionsList = ALL_OPTIONS.stream()
                .filter(modelOption -> "Global".equals(modelOption.getTableName()) 
                        || tableName.contains(modelOption.getTableName()))
                .map(ModelOption::getOptions)
                .collect(Collectors.toList());
        return Result.ok(optionsList);
    }

    @PostMapping("/getOptions")
    public Result<List<Options>> getOptions(@RequestBody TypeDefDto dto) throws ClassNotFoundException {
        if (dto.getPathOrBeanName().contains(".")) {
            Class clazz = Class.forName(dto.getPathOrBeanName());
            List<Options> optionsList = Common.getOptionsList(clazz);
            return Result.ok(optionsList);
        } else {
            IOptionService service = (IOptionService) SpringUtil.getBean(dto.getPathOrBeanName());
            return Result.ok(service.getOptions(dto.getFilterConditions()));
        }
    }
}
