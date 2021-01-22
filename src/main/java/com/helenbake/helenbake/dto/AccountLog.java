package com.helenbake.helenbake.dto;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class AccountLog {
    @NotNull
    private Long categoryItemId;
    @NotNull
    private Long unit;
    private String name;
    private BigDecimal total;
    private BigDecimal pricePerUnit;

    public Long getCategoryItemId() {
        return categoryItemId;
    }

    public void setCategoryItemId(Long categoryItemId) {
        this.categoryItemId = categoryItemId;
    }

    public Long getUnit() {
        return unit;
    }

    public void setUnit(Long unit) {
        this.unit = unit;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public BigDecimal getPricePerUnit() {
        return pricePerUnit;
    }

    public void setPricePerUnit(BigDecimal pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }
}
