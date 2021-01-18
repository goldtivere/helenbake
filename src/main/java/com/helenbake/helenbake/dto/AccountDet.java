package com.helenbake.helenbake.dto;

import com.helenbake.helenbake.domain.Account;
import com.helenbake.helenbake.domain.CategoryItem;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class AccountDet {

    @NotNull
    private Long categoryItemId;

    @NotNull
    private BigDecimal pricePerUnit= new BigDecimal("0.00");

    public Long getCategoryItemId() {
        return categoryItemId;
    }

    public void setCategoryItemId(Long categoryItemId) {
        this.categoryItemId = categoryItemId;
    }

    public BigDecimal getPricePerUnit() {
        return pricePerUnit;
    }

    public void setPricePerUnit(BigDecimal pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }
}
