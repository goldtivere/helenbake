package com.helenbake.helenbake.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "tbaccount")
public class Account extends BaseEntity{

    private LocalDate toDate;

    private LocalDate fromDate;

    private String description;
    @Column(nullable = false)
    private Boolean accountstatus=Boolean.FALSE;
    @NotNull
    private BigDecimal amount= new BigDecimal("0.00");

    private BigDecimal soldSoFar = new BigDecimal("0.00");
    public LocalDate getToDate() {
        return toDate;
    }

    public void setToDate(LocalDate toDate) {
        this.toDate = toDate;
    }

    public LocalDate getFromDate() {
        return fromDate;
    }

    public void setFromDate(LocalDate fromDate) {
        this.fromDate = fromDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getAccountstatus() {
        return accountstatus;
    }

    public void setAccountstatus(Boolean accountstatus) {
        this.accountstatus = accountstatus;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getSoldSoFar() {
        return soldSoFar;
    }

    public void setSoldSoFar(BigDecimal soldSoFar) {
        this.soldSoFar = soldSoFar;
    }
}
