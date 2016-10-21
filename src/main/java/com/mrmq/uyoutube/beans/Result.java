package com.mrmq.uyoutube.beans;


public class Result<T> {
    private ErrorCode errorCode;
    private T value;

    public Result(){}

    public Result(T value, ErrorCode errorCode) {
        this.errorCode = errorCode;
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}
