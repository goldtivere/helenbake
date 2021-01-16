package com.helenbake.helenbake.dto;

import javax.validation.constraints.NotNull;


public class AccountDto {
    private Long id;
    @NotNull
    private String to;
    @NotNull
    private String from;
    @NotNull
    private String description;

    private Boolean accountstatus;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
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
