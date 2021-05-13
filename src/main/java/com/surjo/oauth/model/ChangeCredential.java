package com.surjo.oauth.model;


import com.surjo.oauth.annotation.ValidatePassword;

public class ChangeCredential extends Credential {

    //@ValidatePassword
    private String newValue;
    private String confirmNewValue;

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public String getConfirmNewValue() {
        return confirmNewValue;
    }

    public void setConfirmNewValue(String confirmNewValue) {
        this.confirmNewValue = confirmNewValue;
    }

    @Override
    public String toString() {
        return "ChangeCredential{" +
                "newValue='" + newValue + '\'' +
                ", confirmNewValue='" + confirmNewValue + '\'' +
                '}';
    }

}
