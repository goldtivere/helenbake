package com.helenbake.helenbake.dto;

import com.helenbake.helenbake.domain.CategoryItem;

import java.math.BigDecimal;

public class ReportValue {

    private String name;
    private Long received;
    private Long sold;
    private BigDecimal price= new BigDecimal("0.00");

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getReceived() {
        return received;
    }

    public void setReceived(Long received) {
        this.received = received;
    }

    public Long getSold() {
        return sold;
    }

    public void setSold(Long sold) {
        this.sold = sold;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
