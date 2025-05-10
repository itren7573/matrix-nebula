package com.matrix.framework.auth.service;

import com.matrix.framework.auth.data.DeptPo;
import com.matrix.framework.auth.data.DeptVo;
import com.matrix.framework.auth.repositories.DeptRepository;
import com.matrix.framework.auth.repositories.UserRepository;
import com.matrix.framework.core.i18n.I18n;
import com.matrix.framework.core.i18n.MessageConstants;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DeptService {
    
    private final DeptRepository deptRepository;
    private final UserRepository userRepository;
    
    public DeptService(DeptRepository deptRepository, UserRepository userRepository) {
        this.deptRepository = deptRepository;
        this.userRepository = userRepository;
    }
    
    public Mono<DeptPo> save(DeptPo dept) {
        dept.setUpdateTime(System.currentTimeMillis());
        return deptRepository.save(dept);
    }
    
    public Mono<Void> delete(Long id) {
        // 先删除所有子部门的用户
        return getAllSubDeptIds(id)
                .collectList()
                .flatMap(subDeptIds -> {
                    subDeptIds.add(id); // 添加当前部门ID
                    // 先删除用户
                    return userRepository.deleteByDeptIdIn(subDeptIds)
                            // 然后删除部门
                            .then(Flux.fromIterable(subDeptIds)
                                    .flatMap(deptRepository::deleteById)
                                    .then());
                });
    }
    
    public Mono<List<DeptVo>> getDeptTree() {
        return deptRepository.findAll()
                .collectList()
                .map(this::buildDeptTree);
    }
    
    public Flux<DeptVo> searchDepts(String name) {
        return deptRepository.findByNameContaining(name)
                .map(this::convertToVo);
    }
    
    public Mono<DeptPo> getDeptById(Long id) {
        return deptRepository.findById(id);
    }
    
    public Flux<Long> getAllSubDeptIds(Long deptId) {
        return deptRepository.findByPid(deptId)
                .flatMap(dept -> Flux.concat(
                    Flux.just(dept.getId()),
                    getAllSubDeptIds(dept.getId())
                ));
    }
    
    private List<DeptVo> buildDeptTree(List<DeptPo> depts) {
        Map<Long, List<DeptPo>> pidMap = depts.stream()
                .collect(Collectors.groupingBy(DeptPo::getPid));
                
        return buildDeptTreeRecursive(0L, pidMap);
    }
    
    private List<DeptVo> buildDeptTreeRecursive(Long pid, Map<Long, List<DeptPo>> pidMap) {
        List<DeptVo> tree = new ArrayList<>();
        List<DeptPo> children = pidMap.get(pid);
        
        if (children != null) {
            for (DeptPo child : children) {
                DeptVo node = convertToVo(child);
                node.setChildren(buildDeptTreeRecursive(child.getId(), pidMap));
                tree.add(node);
            }
        }
        
        return tree;
    }
    
    private DeptVo convertToVo(DeptPo po) {
        DeptVo vo = new DeptVo();
        vo.setId(po.getId());
        vo.setPid(po.getPid());
        vo.setTitle(po.getName());
        vo.setKey(po.getId().toString());
        return vo;
    }
    
    public Mono<DeptPo> getById(Long id) {
        return deptRepository.findById(id)
            .switchIfEmpty(Mono.error(new RuntimeException(I18n.getMessage(MessageConstants.DEPT_NOT_EXISTS))));
    }
} 