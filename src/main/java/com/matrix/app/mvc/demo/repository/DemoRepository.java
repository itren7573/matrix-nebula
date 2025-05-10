package com.matrix.app.mvc.demo.repository;

import com.matrix.app.mvc.demo.data.Demo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 测试仓库接口
 *
 * Copyright © 海平面工作室 版权所有
 *
 * @Author: Leo
 * @Create: 2024/10/3 11:15
 */
@Repository
public interface DemoRepository extends JpaRepository<Demo, Integer> {
    
    @Query("SELECT d FROM Demo d WHERE (:name IS NULL OR d.name LIKE %:name%) " +
           "AND (:sex IS NULL OR d.sex = :sex) " +
           "AND (:level IS NULL OR d.level = :level)")
    List<Demo> findByConditions(@Param("name") String name, 
                               @Param("sex") String sex, 
                               @Param("level") String level);
} 