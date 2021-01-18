package com.helenbake.helenbake.domain;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Entity
@Table(name = "tbaccountDetails")
public class AccountDetails extends BaseEntity {

    @ManyToOne
    @JoinColumn(nullable = false, name = "categoryItem_id")
    private CategoryItem categoryItem;
    @NotNull
    private BigDecimal pricePerUnit= new BigDecimal("0.00");

    public CategoryItem getCategoryItem() {
        return categoryItem;
    }

    public void setCategoryItem(CategoryItem categoryItem) {
        this.categoryItem = categoryItem;
    }

    public BigDecimal getPricePerUnit() {
        return pricePerUnit;
    }

    public void setPricePerUnit(BigDecimal pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }

}
