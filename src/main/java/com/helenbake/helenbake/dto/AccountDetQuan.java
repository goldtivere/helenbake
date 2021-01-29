package com.helenbake.helenbake.dto;

import javax.validation.constraints.NotNull;

public class AccountDetQuan {
    @NotNull
    private Long categoryItemId;

    @NotNull
    private Long quantity;

    public Long getCategoryItemId() {
        return categoryItemId;
    }

    public void setCategoryItemId(Long categoryItemId) {
        this.categoryItemId = categoryItemId;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }
}
