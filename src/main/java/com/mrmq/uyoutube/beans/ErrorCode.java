package com.mrmq.uyoutube.beans;


public enum ErrorCode {
    SUCCESS(0, "SUCCESS"),
    FAIL(1, "FAIL"),
    CHANNEL_ID_INVALID(20, "CHANNEL_ID_INVALID"),
    CHANNEL_NAME_INVALID(21, "CHANNEL_NAME_INVALID");

    private int code;
    private String message;

    ErrorCode(int code, String message) {

    }

    public String getMessage() {
        return message;
    }

    public int getCode() {
        return code;
    }
}
