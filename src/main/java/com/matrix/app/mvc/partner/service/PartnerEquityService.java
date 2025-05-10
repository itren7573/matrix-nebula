package com.matrix.app.mvc.partner.service;


import com.matrix.app.mvc.partner.data.EquityDictPo;
import com.matrix.app.mvc.partner.enums.Equity;
import com.matrix.app.mvc.partner.repository.EquityDictRepository;
import com.matrix.framework.lowcode.enums.EventLocation;
import com.matrix.framework.lowcode.eventintf.annotation.ExtendEvent;
import com.matrix.framework.lowcode.eventintf.event.AfterQueryEvent;
import com.matrix.framework.lowcode.eventintf.event.BeforeEditEvent;
import com.matrix.framework.lowcode.eventintf.event.BeforeSaveEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.matrix.app.mvc.partner.service.PartnerListService.PARTNER_LIST_DICT;

/**
 *
 * Copyright © 海平面工作室
 *
 * @author: Leo
 * @create: 2025/2/20
 * @since 1.0
 */
@Service
public class PartnerEquityService implements BeforeSaveEvent, AfterQueryEvent, BeforeEditEvent {

    private final EquityDictRepository equityDictRepository;

    public PartnerEquityService(EquityDictRepository equityDictRepository) {
        this.equityDictRepository = equityDictRepository;
    }

    @Override
    @ExtendEvent(tableName = "partner_equity", eventLocation = EventLocation.BEFORE_SAVE, description = "合伙人权益保存前事件，保存前更新权益表状态")
    public Mono<Map<String, Object>> doBeforeSave(Map<String, Object> data) {
        return Mono.just(data)
                .publishOn(Schedulers.boundedElastic())
                .map(d -> {
                    Object equityId = data.get("equity_id");
                    if (equityId != null) {
                        Optional<EquityDictPo> po = equityDictRepository.findById(Long.parseLong(equityId.toString()));
                        if (po.isPresent()) {
                            EquityDictPo equityDictPo = po.get();
                            equityDictPo.setStatus(data.remove("status").toString());
                            equityDictRepository.save(equityDictPo);
                        }
                    }
                    return d;

                });

    }

    @Override
    @ExtendEvent(tableName = "partner_equity", eventLocation = EventLocation.AFTER_QUERY, description = "合伙人权益查询后事件，给表格显示字典值与状态值信息")
    public Mono<Map<String, Object>> doAfterQuery(Map<String, Object> result) {
        return Mono.just(result)
                .publishOn(Schedulers.boundedElastic())
                .map(d -> {
                    if (d.containsKey("items")) {
                        List<Map<String, Object>> items = (List<Map<String, Object>>) d.get("items");
                        for (Map<String, Object> item : items) {
                            Long userId = Long.valueOf(item.get("user_id").toString());
                            String userName = PARTNER_LIST_DICT.containsKey(userId) ?
                                    PARTNER_LIST_DICT.get(userId) : "";
                            item.put("userName", userName);
                            Long equityDictId = Long.valueOf(item.get("equity_id").toString());
                            Optional<EquityDictPo> po = equityDictRepository.findById(equityDictId);
                            if (po.isPresent()) {
                                EquityDictPo equityDictPo = po.get();
                                item.put("equityName", equityDictPo.getName());
                                item.put("ratio", equityDictPo.getRatio());
                                item.put("status", Equity.valueOf(equityDictPo.getStatus()).name());
                            }
                        }
                    }
                    return d;
                });
    }

    @ExtendEvent(tableName = "partner_equity", eventLocation = EventLocation.BEFORE_EDIT, description = "合伙人权益编辑前事件，增补权益状态")
    public Mono<Map<String, Object>> doBeforeEdit(Map<String, Object> data) {
        return Mono.just(data)
                .publishOn(Schedulers.boundedElastic())
                .map(d -> {
                    Object equityId = data.get("equity_id");
                    if (equityId != null) {
                        Optional<EquityDictPo> po = equityDictRepository.findById(Long.parseLong(equityId.toString()));
                        if (po.isPresent()) {
                            EquityDictPo equityDictPo = po.get();
                            data.put("status", equityDictPo.getStatus());
                        }
                    }
                    return d;
                });
    }
}
