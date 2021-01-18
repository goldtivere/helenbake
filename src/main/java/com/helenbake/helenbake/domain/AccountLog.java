package com.helenbake.helenbake.domain;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "tbaccountlog")
public class AccountLog extends BaseEntity {
    @ManyToOne
    @JoinColumn(nullable = false, name = "accountDetails_id")
    private AccountDetails accountDetails;
    private Long quantity;

    public AccountDetails getAccountDetails() {
        return accountDetails;
    }

    public void setAccountDetails(AccountDetails accountDetails) {
        this.accountDetails = accountDetails;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

}
