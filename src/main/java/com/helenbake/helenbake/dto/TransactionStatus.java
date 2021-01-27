package com.helenbake.helenbake.dto;

import java.math.BigDecimal;

public class TransactionStatus {
    private Boolean status;
    private String add;
    private String message;
    private String methid;

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAdd() {
        return add;
    }

    public void setAdd(String add) {
        this.add = add;
    }

    public String getMethid() {
        return methid;
    }

    public void setMethid(String methid) {
        this.methid = methid;
    }
}
