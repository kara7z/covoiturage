package com.youcode.covoiturage.exception;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends BusinessException {

    private static final String ERROR_CODE = "FORBIDDEN";
    private static final HttpStatus HTTP_STATUS = HttpStatus.FORBIDDEN;

    public ForbiddenException(String message) {
        super(ERROR_CODE, message, HTTP_STATUS);
    }

    public ForbiddenException(String errorCode, String message) {
        super(errorCode, message, HTTP_STATUS);
    }

    public ForbiddenException(String errorCode, String message, Throwable cause) {
        super(errorCode, message, cause, HTTP_STATUS);
    }
}
