package com.youcode.covoiturage.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends BusinessException {

    private static final String ERROR_CODE = "RESOURCE_NOT_FOUND";
    private static final HttpStatus HTTP_STATUS = HttpStatus.NOT_FOUND;

    public ResourceNotFoundException(String message) {
        this(ERROR_CODE, message);
    }

    public ResourceNotFoundException(String errorCode, String message) {
        super(errorCode, message, HTTP_STATUS);
    }

    public ResourceNotFoundException(String errorCode, String message, Throwable cause) {
        super(errorCode, message, cause, HTTP_STATUS);
    }
}
