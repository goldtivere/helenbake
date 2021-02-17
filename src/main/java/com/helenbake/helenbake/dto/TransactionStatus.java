package com.helenbake.helenbake.dto;

import java.math.BigDecimal;

public class TransactionStatus {
    private Boolean status;
    private String add;
    private String message;
    private String methid;
    private String cusName;

    public String getCusName() {
        return cusName;
    }

    public void setCusName(String cusName) {
        this.cusName = cusName;
    }

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
