package com.matrix.app.mvc.partner.repository;

import com.matrix.app.mvc.partner.data.PartnerListPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 合伙人列表仓库接口
 *
 * Copyright © 海平面工作室 版权所有
 *
 * @Author: Leo
 * @Create: 2024/3/19
 * @Since 1.0
 */
@Repository
public interface PartnerListRepository extends JpaRepository<PartnerListPo, Long> {

    @Query("SELECT p.name FROM PartnerListPo p WHERE p.id IN :ids")
    List<String> getNamesByIds(@Param("ids") String[] partnerIdArr);
}