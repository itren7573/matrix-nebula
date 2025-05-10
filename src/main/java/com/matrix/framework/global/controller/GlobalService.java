package com.matrix.framework.global.controller;

import com.matrix.framework.lowcode.eventintf.annotation.OptionsFilter;
import org.springframework.stereotype.Service;

/**
 * Copyright © 雪球工作室 版权所有
 *
 * @Author: Leo
 * @Create: 2025/2/15 20:54
 * @Since 1.0
 */
@Service
public class GlobalService {

    @OptionsFilter(name = "是与否选项", location = "com.matrix.framework.lowcode.enums.YesOrNo")
    public void getYesOrNoInfo() {
    }
}
