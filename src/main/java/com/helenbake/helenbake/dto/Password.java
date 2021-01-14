package com.helenbake.helenbake.dto;

import javax.validation.constraints.NotNull;

public class Password {
        @NotNull
        private String passcode;
        @NotNull
        private String password;
        @NotNull
        private String phone;

        public String getPasscode() {
            return passcode;
        }

        public void setPasscode(String passcode) {
            this.passcode = passcode;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

}
