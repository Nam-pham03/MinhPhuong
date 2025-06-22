package com.lgcns.aidd.exception;

import lombok.Getter;

@Getter
public class ConstraintViolationException extends RuntimeException {
    private final  ErrorMessage errorMessage;

    public ConstraintViolationException(ErrorMessage errorMessage) {
        this.errorMessage = errorMessage;
    }
}
