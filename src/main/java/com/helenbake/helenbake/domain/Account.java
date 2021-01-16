package com.helenbake.helenbake.domain;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "tbaccount")
public class Account extends BaseEntity{

    private LocalDate to;

    private LocalDate from;

    private String description;

    private Boolean accountstatus=Boolean.FALSE;

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
}
