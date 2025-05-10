package com.matrix.framework.core.annotation.excepton;

public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
} 