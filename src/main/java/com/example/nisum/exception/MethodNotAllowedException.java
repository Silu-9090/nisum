package com.example.nisum.exception;

public class MethodNotAllowedException {
	private final int errorCode;
	private final String type;
    private final String errorMessage;

    public MethodNotAllowedException(int errorCode,String type, String errorMessage) {
        this.errorCode = errorCode;
        this.type = type;
        this.errorMessage = errorMessage;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getType() {
    	return type;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
}
