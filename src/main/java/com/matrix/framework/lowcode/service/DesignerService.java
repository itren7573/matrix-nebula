package com.matrix.framework.lowcode.service;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.matrix.framework.auth.service.UserService;
import com.matrix.framework.core.i18n.I18n;
import com.matrix.framework.lowcode.data.DesignerPo;
import com.matrix.framework.lowcode.data.DesignerVo;
import com.matrix.framework.lowcode.repositories.DesignerRepository;
import com.matrix.framework.core.common.utils.DateTime;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.*;


/**
 * 设计器服务类
 *
 * Copyright © 海平面工作室 版权所有
 *
 * @Author: Leo
 * @Create: 2024/1/3 11:15
 * @Since 1.0
 */
@Service
public class DesignerService {

    private final DesignerRepository designerRepository;

    private final UserService userService;

    public DesignerService(DesignerRepository designerRepository, UserService userService) {
        this.designerRepository = designerRepository;
        this.userService = userService;
    }

    public Mono<DesignerPo> save(DesignerPo designer, ServerWebExchange request) {
        long userId = userService.getUserIdByToken(getToken(request));
        if (designer.getId() == null) {
            designer.setRedirect(NanoIdUtils.randomNanoId());
            designer.setCreateBy(userId);
            designer.setCreateTime(System.currentTimeMillis());
        }
        designer.setUpdateBy(userId);
        designer.setUpdateTime(System.currentTimeMillis());
        return designerRepository.save(designer);
    }

    private String getToken(ServerWebExchange request) {
        String token = request.getRequest().getHeaders().getFirst("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return token;
    }

    public Mono<Map<String, Object>> findAll(PageRequest pageable, String search) {
        return designerRepository.findByNameLike(search)
                .flatMap(this::convertToVo)
                .collectList()
                .map(list -> {
                    int start = (int) pageable.getOffset();
                    int end = Math.min((start + pageable.getPageSize()), list.size());
                    
                    Map<String, Object> result = new HashMap<>();
                    result.put("items", list.subList(start, end));
                    result.put("total", list.size());
                    return result;
                });
    }

    /**
     * 将 DesignerPo 转换为 DesignerVo
     */
    private Mono<DesignerVo> convertToVo(DesignerPo po) {
        DesignerVo vo = new DesignerVo();
        // 复制基础属性
        vo.setId(po.getId());
        vo.setRedirect(po.getRedirect());
        vo.setName(po.getName());
        vo.setTableName(po.getTableName());
        vo.setLayout(po.getLayout());
        vo.setContext(po.getContext());
        vo.setAttr(po.getAttr());
        vo.setDescription(po.getDescription());
        vo.setCreateTime(po.getCreateTime());
        vo.setUpdateTime(po.getUpdateTime());
        
        // 转换时间格式
        vo.setCreateTimeFormat(DateTime.format(po.getCreateTime()));
        vo.setUpdateTimeFormat(DateTime.format(po.getUpdateTime()));

        // 获取创建者和更新者信息
        Mono<String> createByName = po.getCreateBy() != null ? 
            userService.getById(po.getCreateBy())
                .map(user -> I18n.getMessage(user.getRealName()))
                .defaultIfEmpty("未知") :
            Mono.just("未知");

        Mono<String> updateByName = po.getUpdateBy() != null ?
            userService.getById(po.getUpdateBy())
                .map(user -> I18n.getMessage(user.getRealName()))
                .defaultIfEmpty("未知") :
            Mono.just("未知");

        return Mono.zip(createByName, updateByName)
                .map(tuple -> {
                    vo.setCreateByName(tuple.getT1());
                    vo.setUpdateByName(tuple.getT2());
                    return vo;
                });
    }

    public Mono<DesignerPo> getById(Long id) {
        return designerRepository.findById(id);
    }

    public Mono<Void> delete(Long id) {
        return designerRepository.deleteById(id);
    }

    /**
     * 根据redirect查询设计器配置
     *
     * @param redirect 重定向标识
     * @return 设计器配置
     */
    public Mono<DesignerPo> findByRedirect(String redirect) {
        return designerRepository.findByRedirect(redirect);
    }

    /**
     * 获取设计器Schema配置
     * 
     * @param redirect 重定向标识
     * @return Schema配置
     */
    public Mono<Map<String, Object>> getSchema(String redirect) {
        return findByRedirect(redirect)
                .map(designer -> {
                    ObjectMapper mapper = new ObjectMapper();
                    Map<String, Object> result = new HashMap<>();
                    try {
                        JsonNode contextNode = mapper.readTree(designer.getContext());
                        JsonNode fieldsNode = contextNode.get("fields");
                        
                        // 构建基础属性
                        result.put("isPreview", false);
                        result.put("name", designer.getName());
                        result.put("tableName", designer.getTableName());
                        result.put("layout", designer.getLayout());
                        
                        // 构建字段配置
                        List<Map<String, Object>> fields = new ArrayList<>();
                        for (JsonNode field : fieldsNode) {
                            Map<String, Object> fieldConfig = new HashMap<>();
                            // 基础属性
                            fieldConfig.put("field", field.get("field").asText());
                            fieldConfig.put("title", field.get("title").asText());
                            fieldConfig.put("component", field.get("component").asText());
                            fieldConfig.put("required", true); // 默认必填

                            // 可选属性
                            if (field.has("width") && field.get("width").isInt()) {
                                fieldConfig.put("width", field.get("customWidth").asInt());
                            }
                            if (field.has("helper")) {
                                fieldConfig.put("helper", field.get("helper").asText());
                            }
                            if (field.has("default")) {
                                fieldConfig.put("default", field.get("default").asText());
                            }
                            if (field.has("sort") && field.get("sort").isInt()) {
                                fieldConfig.put("sort", field.get("sort").asInt());
                            }

                            // Select组件的特殊属性
//                            if ("Select".equals(field.get("component").asText())) {
//                                Map<String, Object> componentProps = new HashMap<>();
//                                Map<String, Object> optionConfig = new HashMap<>();
//                                optionConfig.put("type", field.get("type").asText());
//                                optionConfig.put("source", field.get("source").asText());
//
//                                if (field.has("dependOn")) {
//                                    optionConfig.put("dependOn", field.get("dependOn").asText());
//                                }
//
//                                componentProps.put("optionConfig", optionConfig);
//                                if (field.has("placeholder")) {
//                                    componentProps.put("placeholder", field.get("placeholder").asText());
//                                }
//                                if (field.has("style")) {
//                                    componentProps.put("style", field.get("style").asText());
//                                }
//                                fieldConfig.put("componentProps", componentProps);
//
//                                if (field.has("cascades")) {
//                                    fieldConfig.put("cascades", Arrays.asList(field.get("cascades").asText()));
//                                }
//                            }
                            if ("Select, Radio, Checkbox, SelectMultiple".contains(field.get("component").asText())) {
                                fieldConfig.put("source", field.get("source").asText());

                                if (field.has("dependOn")) {
                                    fieldConfig.put("dependOn", field.get("dependOn").asText());
                                }

                                if (field.has("placeholder")) {
                                    fieldConfig.put("placeholder", field.get("placeholder").asText());
                                }
                                if (field.has("style")) {
                                    fieldConfig.put("style", field.get("style").asText());
                                }

                                if (field.has("cascades")) {
                                    fieldConfig.put("cascades", Arrays.asList(field.get("cascades").asText()));
                                }
                            }

                            // 表格和表单控制属性
                            fieldConfig.put("inTable", field.has("inTable") ? field.get("inTable").asBoolean() : false);
                            fieldConfig.put("enable", field.has("enable") ? field.get("enable").asBoolean() : true);
                            fieldConfig.put("visible", field.has("visible") ? field.get("visible").asBoolean() : true);
                            fieldConfig.put("storage", field.has("storage") ? field.get("storage").asBoolean() : true);

                            // 校验规则
                            if (field.has("rule")) {
                                List<String> rules = new ArrayList<>();
                                for (JsonNode rule : field.get("rule")) {
                                    rules.add(rule.asText());
                                }
                                fieldConfig.put("rule", rules);
                            }

                            if (field.has("ruleLength")) {
                                Map<String, Integer> ruleLength = new HashMap<>();
                                JsonNode ruleLengthNode = field.get("ruleLength");
                                if (ruleLengthNode.has("min")) {
                                    ruleLength.put("min", ruleLengthNode.get("min").asInt());
                                }
                                if (ruleLengthNode.has("max")) {
                                    ruleLength.put("max", ruleLengthNode.get("max").asInt());
                                }
                                fieldConfig.put("ruleLength", ruleLength);
                            }

                            fields.add(fieldConfig);
                        }

                        result.put("fields", fields);
                        return result;
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException("解析context失败: " + e.getMessage());
                    }
                })
                .switchIfEmpty(Mono.error(new RuntimeException("未找到对应的设计器配置")));
    }
} 