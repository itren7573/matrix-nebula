package com.matrix.app.mvc.partner.repository;

import com.matrix.app.mvc.partner.data.IncomeDictPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


/**
 * 收益字典仓库接口
 *
 * Copyright © 海平面工作室 版权所有
 *
 * @Author: Leo
 * @Create: 2024/10/3 11:15
 */
@Repository
public interface IncomeDictRepository extends JpaRepository<IncomeDictPo, Long> {

} 