package com.youcode.covoiturage.exception;

import org.springframework.http.HttpStatus;

public class BusinessRuleException extends BusinessException {

    private static final String ERROR_CODE = "BUSINESS_RULE_VIOLATION";
    private static final HttpStatus HTTP_STATUS = HttpStatus.CONFLICT;

    public BusinessRuleException(String message) {
        super(ERROR_CODE, message, HTTP_STATUS);
    }

    public BusinessRuleException(String errorCode, String message) {
        super(errorCode, message, HTTP_STATUS);
    }

    public BusinessRuleException(String errorCode, String message, Throwable cause) {
        super(errorCode, message, cause, HTTP_STATUS);
    }
}
