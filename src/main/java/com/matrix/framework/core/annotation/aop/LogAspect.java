package com.matrix.framework.core.annotation.aop;

import com.matrix.framework.audit.data.AuditLogPo;
import com.matrix.framework.audit.service.AuditLogService;
import com.matrix.framework.auth.data.UserVo;
import com.matrix.framework.core.annotation.validation.LogCollector;
import com.matrix.framework.core.common.result.Result;
import com.matrix.framework.core.component.Jwt;
import com.matrix.framework.core.i18n.I18n;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 日志切面
 *
 * Copyright © 海平面工作室 版权所有
 *
 * @Author: Leo
 * @Create: 2024/10/3 11:15
 * @Since 1.0
 */
@Aspect
@Component
public class LogAspect {

    private final AuditLogService auditLogService;

    private final Jwt jwt;

    public LogAspect(AuditLogService auditLogService, Jwt jwt) {
        this.auditLogService = auditLogService;
        this.jwt = jwt;
    }

    @Pointcut("@annotation(com.matrix.framework.core.annotation.validation.LogCollector)")
    public void logCollectorPointcut() {}

    @AfterReturning(pointcut = "logCollectorPointcut()", returning = "returnValue")
    public void afterReturning(JoinPoint joinPoint, Object returnValue) throws NoSuchMethodException {
        // 获取类信息
        String moduleName = joinPoint.getTarget().getClass().getSimpleName().replaceAll("(?i)controller", "");
        // 获取方法信息和参数
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        if (methodName.equalsIgnoreCase("login")) {
            saveLoginLog(moduleName, args);
            return;
        }

        String username = null;
        ServerWebExchange exchange = null;
        for (Object arg : args) {
            if (arg instanceof ServerWebExchange) {
                exchange = (ServerWebExchange) arg;
                String token = exchange.getRequest().getHeaders().getFirst("Authorization");
                if (token != null && token.startsWith("Bearer ")) {
                    token = token.substring(7);
                    username = jwt.extractUsername(token);
                }
                break;
            }
        }
        
        // 创建日志对象
        AuditLogPo auditLog = new AuditLogPo();
        auditLog.setUsername(username != null ? username : "unknown");
        auditLog.setModule(moduleName);
        auditLog.setTimestamp(System.currentTimeMillis());

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        LogCollector logCollector = signature.getMethod().getAnnotation(LogCollector.class);
        auditLog.setAction(logCollector.detail().isEmpty() ? 
            I18n.getMessage((moduleName + "." + methodName).toLowerCase()) :
                I18n.getMessage(logCollector.detail()));

        if (methodName.equalsIgnoreCase("save")) {
            auditLog.setDetails(args[0].toString());
        } else {
            doDeleteEvent(returnValue, auditLog);
        }
        auditLogService.save(auditLog).subscribe();
    }

//    private void doSaveEvent(Object returnValue, AuditLogPo auditLog, Object[] args) {
//        if (returnValue instanceof Mono<?>) {
//            ((Mono<?>) returnValue).subscribe(result -> {
//                if (result instanceof Result) {
//                    Object info = ((Result<?>) result).getData();
//                    if (info instanceof String) {
//                        auditLog.setDetails(args[0].toString() + ";" + I18n.getMessage("save.status") + info);
//                    } else {
//                        auditLog.setDetails(args[0].toString() + ";" + I18n.getMessage("save.status") + I18n.getMessage("response.success"));
//                    }
//                    auditLogService.save(auditLog).subscribe();
//                }
//            });
//        }
//    }

    private void doDeleteEvent(Object returnValue, AuditLogPo auditLog) {
        if (returnValue instanceof Mono<?>) {
            ((Mono<?>) returnValue).subscribe(result -> {
                if (result instanceof Result) {
                    Object data = ((Result<?>) result).getData();
                    if (data != null) {
                        auditLog.setDetails(data.toString());
                        auditLogService.save(auditLog).subscribe();
                    }
                }
            });
        } else {
            Result<?> result = (Result<?>) returnValue;
            auditLog.setDetails(result.getData().toString());
            auditLogService.save(auditLog).subscribe();
        }
    }

    private void saveLoginLog(String moduleName, Object[] args) {
        UserVo userVo = args[0] instanceof UserVo ? (UserVo) args[0] : null;
        AuditLogPo auditLog = new AuditLogPo();
        auditLog.setUsername(userVo != null? userVo.getUsername() : "unknown");
        auditLog.setModule(moduleName);
        auditLog.setAction(I18n.getMessage("auth.login"));
        auditLogService.save(auditLog).subscribe();
    }

} 