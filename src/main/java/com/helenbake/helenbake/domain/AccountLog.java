package com.helenbake.helenbake.domain;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "tbaccountlog")
public class AccountLog extends BaseEntity {

    @ManyToOne
    @JoinColumn(nullable = false, name = "categoryItem_id")
    private CategoryItem categoryItem;

    @ManyToOne
    @JoinColumn(nullable = false, name = "collection_id")
    private Collections collections;

    private Long quantity;

    private BigDecimal amountPerItem= new BigDecimal("0.00");

    private BigDecimal totalAmount= new BigDecimal("0.00");

    public CategoryItem getCategoryItem() {
        return categoryItem;
    }

    public void setCategoryItem(CategoryItem categoryItem) {
        this.categoryItem = categoryItem;
    }

    public Collections getCollections() {
        return collections;
    }

    public void setCollections(Collections collections) {
        this.collections = collections;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getAmountPerItem() {
        return amountPerItem;
    }

    public void setAmountPerItem(BigDecimal amountPerItem) {
        this.amountPerItem = amountPerItem;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
}
