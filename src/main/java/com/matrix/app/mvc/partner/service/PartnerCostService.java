package com.matrix.app.mvc.partner.service;

import com.matrix.framework.core.common.utils.DateTime;
import com.matrix.framework.lowcode.enums.EventLocation;
import com.matrix.framework.lowcode.eventintf.annotation.ExtendEvent;
import com.matrix.framework.lowcode.eventintf.event.AfterQueryEvent;
import com.matrix.framework.lowcode.eventintf.event.BeforeSaveEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

import static com.matrix.app.mvc.partner.service.PartnerListService.PARTNER_LIST_DICT;

/**
 * Copyright © 海平面工作室
 *
 * @author: Leo
 * @create: 2025/2/14
 * @since 1.0
 */
@Service
public class PartnerCostService implements BeforeSaveEvent, AfterQueryEvent {

    @Override
    @ExtendEvent(tableName = "partner_cost", eventLocation = EventLocation.BEFORE_SAVE, description = "合伙人成本保存前事件，为新增成本记录添加创建时间")
    public Mono<Map<String, Object>> doBeforeSave(Map<String, Object> data) {
        return Mono.just(data)
                .map(d -> {
                    if (!data.containsKey("create_time") && data.get("create_time") == null) {
                        data.remove("create_time");
                        data.put("create_time", System.currentTimeMillis());
                    }
                    return d;
                });
    }

    @Override
    @ExtendEvent(tableName = "partner_cost", eventLocation = EventLocation.AFTER_QUERY, description = "合伙人成本查询后事件，转换用户id为用户名称后返回给前端")
    public Mono<Map<String, Object>> doAfterQuery(Map<String, Object> result) {
        return Mono.just(result)
                .map(d -> {
                    if (d.containsKey("items")) {
                        List<Map<String, Object>> items = (List<Map<String, Object>>) d.get("items");
                        for (Map<String, Object> item : items) {
                            if (item.containsKey("user_id")) {
                                Long userId = Long.valueOf(item.get("user_id").toString());
                                String userName = PARTNER_LIST_DICT.containsKey(userId) ? 
                                    PARTNER_LIST_DICT.get(userId) : "";
                                item.put("user_name", userName);
                                Long createTime = (Long) item.remove("create_time");
                                item.put("create_time", DateTime.format(createTime));
                                item.put("sum", Math.round(Double.parseDouble(item.get("cost").toString()) * Double.parseDouble(item.get("amount").toString())));
                            }
                        }
                    }
                    return d;
                });
    }
}
