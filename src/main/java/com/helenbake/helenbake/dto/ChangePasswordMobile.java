package com.helenbake.helenbake.dto;

import javax.validation.constraints.NotNull;

public class ChangePasswordMobile {
    @NotNull
    private String oldpassword;
    @NotNull
    private String password;
    @NotNull
    private String answer;

    public String getOldpassword() {
        return oldpassword;
    }

    public void setOldpassword(String oldpassword) {
        this.oldpassword = oldpassword;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
