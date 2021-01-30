package com.helenbake.helenbake.command;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public class AccountCommand {
    private Long id;
    @NotNull
    private LocalDate to;
    @NotNull
    private LocalDate from;
    @NotNull
    private String description;
    @NotNull
    private Boolean accountstatus=Boolean.FALSE;
    @NotNull
    private BigDecimal amount= new BigDecimal("0.00");

    private BigDecimal soldSoFar = new BigDecimal("0.00");

    private String accountValue;
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getTo() {
        return to;
    }

    public void setTo(LocalDate to) {
        this.to = to;
    }

    public LocalDate getFrom() {
        return from;
    }

    public void setFrom(LocalDate from) {
        this.from = from;
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

    public String getAccountValue() {
        return accountValue;
    }

    public void setAccountValue(String accountValue) {
        this.accountValue = accountValue;
    }
}
