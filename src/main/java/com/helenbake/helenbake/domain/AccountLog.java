package com.helenbake.helenbake.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
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
    @JsonIgnore
    @Transient
    private String refCode;

    @JsonIgnore
    @Transient
    private String payMethod;
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

    public String getRefCode() {
        return refCode;
    }

    public void setRefCode(String refCode) {
        this.refCode = refCode;
    }

    public String getPayMethod() {
        return payMethod;
    }

    public void setPayMethod(String payMethod) {
        this.payMethod = payMethod;
    }
}
