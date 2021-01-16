package com.helenbake.helenbake.command;

import javax.validation.constraints.NotNull;
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
    private Boolean accountstatus;

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
}
