package com.mastercard.fraud.exception;

public enum CustomExceptionType {
    USER_INPUT_ERROR(400),
    SYSTEM_ERROR(500),
    OTHER_ERROR(999);

    private int code;
    CustomExceptionType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
