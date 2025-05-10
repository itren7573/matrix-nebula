package com.matrix.framework.core.annotation.excepton;

import com.matrix.framework.core.common.result.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ValidationException.class)
    public Mono<Result<?>> handleValidationException(ValidationException e) {
        return Mono.just(Result.fail(e.getMessage()));
    }
} 