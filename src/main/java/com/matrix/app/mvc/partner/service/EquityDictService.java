package com.matrix.app.mvc.partner.service;

import com.matrix.app.mvc.partner.data.EquityDictPo;
import com.matrix.app.mvc.partner.data.EquityDictVo;
import com.matrix.app.mvc.partner.repository.EquityDictRepository;
import com.matrix.framework.core.common.global.IOptionService;
import com.matrix.framework.core.common.global.Options;
import com.matrix.framework.core.i18n.I18n;
import com.matrix.framework.core.i18n.MessageConstants;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 权益字典服务类
 *
 * Copyright © 海平面工作室 版权所有
 *
 * @Author: Leo
 * @Create: 2024/10/3 11:15
 */
@Service
public class EquityDictService implements IOptionService {

    private final EquityDictRepository repository;

    public EquityDictService(EquityDictRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Options> getOptions(Map<String, String> filter) {
        // 处理 filter 为 null 的情况
        String status = filter != null ? filter.get("status") : null;

        // 获取所有数据
        List<EquityDictPo> allItems = repository.findAll();

        // 收集所有的pid到Set中
        Set<Long> pidSet = allItems.stream()
                .map(EquityDictPo::getPid)
                .collect(Collectors.toSet());

        // 过滤数据
        return allItems.stream()
                .filter(item -> {
                    // 状态匹配
                    boolean statusMatch = status == null || status.equals(item.getStatus());
                    // id不为0且不在pid集合中（即不是父节点）
                    boolean isLeafNode = !item.getId().equals(0L) && !pidSet.contains(item.getId());
                    return statusMatch && isLeafNode;
                })
                .map(item -> new Options(
                        item.getName(),
                        item.getId().toString()

                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public EquityDictPo save(EquityDictPo equityDic) {
        if (equityDic.getId() == null) {
            Integer maxSort = repository.findAll().stream()
                    .map(EquityDictPo::getSort)
                    .filter(Objects::nonNull)
                    .max(Integer::compareTo)
                    .orElse(0);
            equityDic.setSort(maxSort + 1);
        }
        return repository.save(equityDic);
    }

    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }

    public EquityDictPo getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(I18n.getMessage(MessageConstants.RESPONSE_DATA_ERROR)));
    }

    public List<EquityDictVo> getTree() {
        List<EquityDictPo> allItems = repository.findAll();
        return buildTree(allItems, 0L);
    }

    private List<EquityDictVo> buildTree(List<EquityDictPo> allItems, Long pid) {
        return allItems.stream()
                .filter(item -> item.getPid().equals(pid))
                .map(item -> {
                    EquityDictVo vo = convertToVo(item);
                    vo.setChildren(buildTree(allItems, item.getId()));
                    return vo;
                })
                .sorted(Comparator.comparing(item -> item.getSort() != null ? item.getSort() : Integer.MAX_VALUE))
                .collect(Collectors.toList());
    }

    private EquityDictVo convertToVo(EquityDictPo item) {
        EquityDictVo vo = new EquityDictVo();
        vo.setId(item.getId());
        vo.setPid(item.getPid());
        vo.setName(item.getName());
        vo.setRatio(item.getRatio());
        vo.setStatus(item.getStatus());
        vo.setSort(item.getSort());
        vo.setDescription(item.getDescription());
        vo.setTitle(item.getName());
        vo.setKey(item.getId().toString());
        return vo;
    }

    public Map<String, Object> findAll(Pageable pageable, Long pid) {
        List<EquityDictPo> items = pid != null ?
                repository.findByPidOrderBySortAsc(pid) : 
                repository.findAll();

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), items.size());
        
        Page<EquityDictPo> page = new PageImpl<>(
                items.subList(start, end),
                pageable,
                items.size()
        );

        Map<String, Object> result = new HashMap<>();
        result.put("items", page.getContent());
        result.put("total", page.getTotalElements());
        
        return result;
    }

    public List<EquityDictVo> searchByName(String name) {
        List<EquityDictPo> items = repository.searchByName(name);
        return items.stream()
                .map(this::convertToVo)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updatePosition(Long dragId, Long dropId, Long dropPosition, String dropType) {
        EquityDictPo dragItem = getById(dragId);
        EquityDictPo dropItem = getById(dropId);

        Integer dragSort = dragItem.getSort();
        Integer dropSort = dropItem.getSort();

        dragItem.setSort(dropSort);
        dropItem.setSort(dragSort);

        if ("inner".equals(dropType)) {
            dragItem.setPid(dropId);
        } else {
            dragItem.setPid(dropItem.getPid());
        }
        
        repository.save(dragItem);
        repository.save(dropItem);
    }

    @Transactional
    public void updateOrder(List<Map<String, Long>> data) {
        data.forEach(item -> {
            Long id = item.get("id");
            Long sort = item.get("sort");
            EquityDictPo entity = getById(id);
            entity.setSort(sort.intValue());
            repository.save(entity);
        });
    }


}