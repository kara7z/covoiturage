package com.youcode.covoiturage.exception;

import org.springframework.http.HttpStatus;

public class AlreadyExistsException extends BusinessException {

    private static final String ERROR_CODE = "ALREADY_EXISTS";
    private static final HttpStatus HTTP_STATUS = HttpStatus.CONFLICT;

    public AlreadyExistsException(String message) {
        super(ERROR_CODE, message, HTTP_STATUS);
    }

    public AlreadyExistsException(String errorCode, String message) {
        super(errorCode, message, HTTP_STATUS);
    }

    public AlreadyExistsException(String errorCode, String message, Throwable cause) {
        super(errorCode, message, cause, HTTP_STATUS);
    }
}
