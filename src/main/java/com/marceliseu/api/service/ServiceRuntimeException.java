package com.marceliseu.api.service;

public class ServiceRuntimeException extends RuntimeException {

    private int code;

    public ServiceRuntimeException(String message) {
        super(message);
    }

    public ServiceRuntimeException(String message, int code) {
        super(message);
        this.code = code;
    }

    public ServiceRuntimeException(String message, Throwable cause, int code) {
        super(message, cause);
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
