package com.matrix.app.mvc.partner.service;

import com.matrix.app.mvc.partner.data.PartnerListPo;
import com.matrix.app.mvc.partner.repository.PartnerListRepository;
import com.matrix.framework.core.common.global.IOptionService;
import com.matrix.framework.core.common.global.Options;
import com.matrix.framework.core.loader.ILoader;
import com.matrix.framework.lowcode.enums.EventLocation;
import com.matrix.framework.lowcode.eventintf.annotation.ExtendEvent;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import com.matrix.framework.lowcode.eventintf.event.AfterSaveEvent;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 合伙人名单服务类, 为相关合伙人低代码提供options选项数据
 *
 * Copyright © 雪球工作室 版权所有
 *
 * @Author: Leo
 * @Create: 2025/2/9 10:38
 * @Since 1.0
 */
@Service
@Order(99)
public class PartnerListService implements IOptionService, AfterSaveEvent, ILoader {

    public static Map<Long, String> PARTNER_LIST_DICT = new HashMap<>();

    private final PartnerListRepository repository;

    public PartnerListService(PartnerListRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Options> getOptions(Map<String, String> filter) {
        return repository.findAll()
                .stream()
                .map(partner -> new Options(
                        partner.getName(),
                        partner.getId().toString()
                ))
                .collect(Collectors.toList());
    }

    @Override
    @ExtendEvent(tableName = "partner_list", eventLocation = EventLocation.AFTER_SAVE, description = "合伙人名单保存后事件，更新合伙人名单字典")
    public Mono<Map<String, Object>> doAfterSave(Map<String, Object> data) {
        return Mono.just(data)
                .map(d -> {
                    String id = data.get("id").toString();
                    if (id != null || !id.isEmpty()) {
                        long partnerId = Long.parseLong(id);
                        String name = PARTNER_LIST_DICT.get(partnerId);
                        if (name == null) {
                            PARTNER_LIST_DICT.put(partnerId, data.get("name").toString());
                        } else if (!name.equals(data.get("name").toString())) {
                            PARTNER_LIST_DICT.remove(partnerId);
                            PARTNER_LIST_DICT.put(partnerId, data.get("name").toString());
                        }
                    }
                    return d;
                });
    }

    @Override
    public Mono<Void> load() {
        return Mono.fromRunnable(() -> {
            List<PartnerListPo> allPartner = repository.findAll();
            PARTNER_LIST_DICT = allPartner.stream()
                    .collect(Collectors.toMap(
                            PartnerListPo::getId,
                            PartnerListPo::getName,
                            (oldValue, newValue) -> newValue,
                            HashMap::new
                    ));
        });
    }

    @Override
    public String getServiceName() {
        return "合伙人名单字典服务";
    }
}
