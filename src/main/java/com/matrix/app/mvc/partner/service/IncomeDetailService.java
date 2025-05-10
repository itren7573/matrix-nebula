package com.matrix.app.mvc.partner.service;

import com.matrix.app.mvc.partner.data.IncomeDictPo;
import com.matrix.app.mvc.partner.repository.IncomeDictRepository;
import com.matrix.app.mvc.partner.repository.PartnerListRepository;
import com.matrix.framework.core.common.utils.DateTime;
import com.matrix.framework.lowcode.enums.EventLocation;
import com.matrix.framework.lowcode.eventintf.annotation.ExtendEvent;
import com.matrix.framework.lowcode.eventintf.event.AfterQueryEvent;
import com.matrix.framework.lowcode.eventintf.event.BeforeSaveEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Copyright © 海平面工作室
 *
 * @Author: Leo
 * @Create: 2025/2/23 19:19
 * @Since 1.0
 */
@Service
public class IncomeDetailService implements BeforeSaveEvent, AfterQueryEvent {

    private final PartnerListRepository partnerListRepository;

    private final IncomeDictRepository incomeDictRepository;

    public IncomeDetailService(PartnerListRepository partnerListRepository, IncomeDictRepository incomeDictRepository) {
        this.partnerListRepository = partnerListRepository;
        this.incomeDictRepository = incomeDictRepository;
    }

    @Override
    @ExtendEvent(tableName = "partner_income_detail", eventLocation = EventLocation.BEFORE_SAVE, description = "合伙人收益明细保存前事件，将合伙人数组转换为字符串")
    public Mono<Map<String, Object>> doBeforeSave(Map<String, Object> data) {
        return Mono.just(data)
                .map(d -> {
                    // 保存前处理逻辑,将合伙人数组转换为字符串,这可以理解为将一个Vo对象转换为一个Po对象,将其中的属性进行转换
                    if (d.get("partner_ids") != null) {
                        List<String> partners = (List<String>) d.remove("partner_ids");
                        d.put("partner_ids", String.join(",", partners));
                    }
                    d.put("create_time", System.currentTimeMillis());
                    return d;
                });

    }

    @Override
    @ExtendEvent(tableName = "partner_income_detail", eventLocation = EventLocation.AFTER_QUERY, description = "合伙人收益明细查询后事件，转换全部id为名称后返回给前端")
    public Mono<Map<String, Object>> doAfterQuery(Map<String, Object> result) {
        return Mono.just(result)
                .publishOn(Schedulers.boundedElastic())
                .map(d -> {
                    if (d.containsKey("items")) {
                        List<Map<String, Object>> items = (List<Map<String, Object>>) d.get("items");
                        for (Map<String, Object> item : items) {
                            if (item.containsKey("partner_ids")) {
                                String partnerIds = item.remove("partner_ids").toString();
                                String[] partnerIdArr = partnerIds.split(",");
                                List<String> partnerNames = partnerListRepository.getNamesByIds(partnerIdArr);
                                item.put("partner_ids", partnerNames);
                            }
                            if (item.containsKey("create_time")) {
                                Long createTime = (Long) item.remove("create_time");
                                item.put("create_time", DateTime.format(createTime));
                            }
                            if (item.containsKey("dict_id")) {
                                Long dictId = Long.valueOf(item.remove("dict_id").toString());
                                Optional<IncomeDictPo> incomeDictPo = incomeDictRepository.findById(dictId);
                                item.put("dict_id", incomeDictPo.get().getName());
                            }
                        }
                    }
                    return d;
                });
    }

}
