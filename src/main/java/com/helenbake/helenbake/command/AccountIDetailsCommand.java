package com.helenbake.helenbake.command;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class AccountIDetailsCommand {
    @NotNull
    private Long id;
    @NotNull
    private String name;
    @NotNull
    private Long categoryItemId;
    @NotNull
    private BigDecimal pricePerUnit = new BigDecimal("0.00");

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPricePerUnit() {
        return pricePerUnit;
    }

    public void setPricePerUnit(BigDecimal pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }

    public Long getCategoryItemId() {
        return categoryItemId;
    }

    public void setCategoryItemId(Long categoryItemId) {
        this.categoryItemId = categoryItemId;
    }
}
