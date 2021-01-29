package com.helenbake.helenbake.domain;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "tbaccountDetailsQuantity")
public class AccountItemQuantity extends BaseEntity{

    @ManyToOne
    @JoinColumn(nullable = false, name = "account_id")
    private Account account;

    @ManyToOne
    @JoinColumn(nullable = false, name = "categoryItem_id")
    private CategoryItem categoryItem;

    private Long quantity;

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public CategoryItem getCategoryItem() {
        return categoryItem;
    }

    public void setCategoryItem(CategoryItem categoryItem) {
        this.categoryItem = categoryItem;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }
}
