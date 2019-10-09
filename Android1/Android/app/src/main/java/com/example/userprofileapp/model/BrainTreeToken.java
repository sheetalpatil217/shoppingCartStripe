package com.example.userprofileapp.model;

public class BrainTreeToken {
    private String clientToken;
    private boolean success;

    public BrainTreeToken() {
    }

    public String getClientToken() {
        return clientToken;
    }

    public void setClientToken(String clientToken) {
        this.clientToken = clientToken;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
