package com.surjo.oauth.model;

public class ResetCredential extends Credential{

    private String confirmNewValue;
    private boolean changeOnLogin;
    private String remarks;

    public String getConfirmNewValue() {
        return confirmNewValue;
    }

    public void setConfirmNewValue(String confirmNewValue) {
        this.confirmNewValue = confirmNewValue;
    }

    public boolean isChangeOnLogin() {
        return changeOnLogin;
    }

    public void setChangeOnLogin(boolean changeOnLogin) {
        this.changeOnLogin = changeOnLogin;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
