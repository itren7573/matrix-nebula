package com.matrix.framework.auth.exception;

public class LoginException extends RuntimeException {
    private final String status;
    
    public LoginException(String status) {
        this.status = status;
    }
    
    public LoginException(String message, String status) {
        super(message);
        this.status = status;
    }
    
    public String getStatus() {
        return status;
    }
} 