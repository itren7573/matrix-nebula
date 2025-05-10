package com.matrix.framework.core.common.utils;

import com.matrix.framework.core.common.enums.IEnumDescribable;
import com.matrix.framework.core.common.global.Options;
import java.util.ArrayList;
import java.util.List;

/**
 * 通用工具类
 *
 * Copyright © 海平面工作室 版权所有
 *
 * @Author: Leo
 * @Create: 2024/10/3 11:15
 * @Since 1.0
 */

public class Common {

    public static <E extends Enum<E>> List<Options> getOptionsList(Class<E> enumClass) {
        List<Options> optionsList = new ArrayList<>();
        E[] enumConstants = enumClass.getEnumConstants();

        for (E enumConstant : enumConstants) {
            String description = ((IEnumDescribable) enumConstant).getDescription();
            optionsList.add(new Options(description, enumConstant.name()));
        }

        return optionsList;
    }
}
