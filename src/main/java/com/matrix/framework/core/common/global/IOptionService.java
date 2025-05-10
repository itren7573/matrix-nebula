package com.matrix.framework.core.common.global;

import java.util.List;
import java.util.Map;

/**
 * 通用的选择项获取接口
 *
 * Copyright © 海平面工作室
 *
 * @author: Leo
 * @create: 2025/1/29
 * @since 1.0
 */
public interface IOptionService {

    List<Options> getOptions(Map<String, String> filter);
}
