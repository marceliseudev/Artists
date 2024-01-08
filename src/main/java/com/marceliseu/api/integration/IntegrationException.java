package com.marceliseu.api.integration;

public class IntegrationException extends Exception {

    private int code;

    public IntegrationException(String message) {
        super(message);
    }

    public IntegrationException(String message, int code) {
        super(message);
        this.code = code;
    }

    public IntegrationException(String message, Throwable cause, int code) {
        super(message, cause);
        this.code = code;
    }
    
    public IntegrationException(String message, Throwable cause) {
        super(message, cause);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
