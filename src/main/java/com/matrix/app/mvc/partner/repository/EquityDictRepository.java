package com.matrix.app.mvc.partner.repository;

import com.matrix.app.mvc.partner.data.EquityDictPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
    
/**
 * 权益字典仓库接口
 *
 * Copyright © 海平面工作室 版权所有
 *
 * @Author: Leo
 * @Create: 2024/10/3 11:15
 */
@Repository
public interface EquityDictRepository extends JpaRepository<EquityDictPo, Long> {

    @Query("SELECT p FROM EquityDictPo p WHERE p.name LIKE %:name%")
    List<EquityDictPo> searchByName(@Param("name") String name);

    @Query("SELECT p FROM EquityDictPo p WHERE p.id = :pid or p.pid = :pid")
    List<EquityDictPo> findByPidOrderBySortAsc(Long pid);
} 