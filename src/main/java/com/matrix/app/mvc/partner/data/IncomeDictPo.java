package com.matrix.app.mvc.partner.data;

import jakarta.persistence.*;
/**
 * Copyright © 雪球工作室 版权所有
 *
 * @Author: 李鹏
 * @Create: 2025/2/23 18:13
 * @Since 1.0
 */
@Entity
@Table(name = "partner_income_dict")
public class IncomeDictPo {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    private String name;

    private int royaltyRate;

    private String description;

    public int getRoyaltyRate() {
        return royaltyRate;
    }

    public void setRoyaltyRate(int royaltyRate) {
        this.royaltyRate = royaltyRate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
