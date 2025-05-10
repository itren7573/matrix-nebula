package com.matrix.framework.core.annotation.aop;

import com.matrix.framework.core.annotation.excepton.ValidationException;
import com.matrix.framework.core.annotation.validation.Valid;
import com.matrix.framework.core.common.result.Result;
import com.matrix.framework.core.i18n.I18n;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Aspect
@Component
public class ValidationAspect {

    @Around("within(com.matrix.framework..*.controller..*)")
    public Object validateParameters(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Object[] args = joinPoint.getArgs();

            for (int i = 0; i < signature.getMethod().getParameters().length; i++) {
                if (signature.getMethod().getParameters()[i].isAnnotationPresent(Valid.class)) {
                    Object arg = args[i];
                    if (arg != null) {
                        Validator.validate(arg);
                    }
                }
            }

            Object result = joinPoint.proceed();
            return result;
            
        } catch (ValidationException e) {
            // 对于响应式端点，返回 Mono<Result>
            if (((MethodSignature) joinPoint.getSignature()).getReturnType().equals(Mono.class)) {
                return Mono.just(Result.checkFail(I18n.getMessage(e.getMessage())));
            }
            // 对于普通端点，直接返回 Result
            return Result.fail(e.getMessage());
        }
    }
}