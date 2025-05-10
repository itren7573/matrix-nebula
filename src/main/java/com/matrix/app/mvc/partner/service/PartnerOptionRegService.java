package com.matrix.app.mvc.partner.service;

import com.matrix.framework.lowcode.eventintf.annotation.OptionsFilter;
import org.springframework.stereotype.Service;

/**
 * 为低代码提供合伙人选项注册服务
 *
 * Copyright © 雪球工作室 版权所有
 *
 * @Author: Leo
 * @Create: 2025/2/15 21:10
 * @Since 1.0
 */
@Service
public class PartnerOptionRegService {

    @OptionsFilter(name = "合伙人权益选项", tableName = "partner", location = "equityDictService")
    public void equityOption() {
    }

    @OptionsFilter(name = "合伙人权益状态", tableName = "partner", location = "com.matrix.app.mvc.partner.enums.Equity")
    public void equityStatus() {
    }

    @OptionsFilter(name = "合伙人成本选项", tableName = "partner", location = "com.matrix.app.mvc.partner.enums.Cost")
    public void costOption() {
    }

    @OptionsFilter(name = "合伙人角色选项", tableName = "partner", location = "com.matrix.app.mvc.partner.enums.Role")
    public void roleOption() {
    }

    @OptionsFilter(name = "合伙人性别选项", tableName = "partner", location = "com.matrix.app.mvc.partner.enums.Sex")
    public void sexOption() {
    }

    @OptionsFilter(name = "合伙人名单选项", tableName = "partner", location = "partnerListService")
    public void partnerListServiceOption() {
    }

    @OptionsFilter(name = "合伙人收益状态选项", tableName = "partner", location = "com.matrix.app.mvc.partner.enums.IncomeStatus")
    public void partnerIncomeStatusOption() {
    }

    @OptionsFilter(name = "合伙人收益指标选项", tableName = "partner", location = "incomeDictService")
    public void partnerIncomeDictOptionServiceOption() {
    }
}
