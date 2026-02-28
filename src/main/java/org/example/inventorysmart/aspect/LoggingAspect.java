package org.example.inventorysmart.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    // Pointcut to intercept all methods in the service package and its sub-packages
    @Pointcut("execution(* org.example.inventorysmart.service..*(..))")
    public void serviceMethods() {
    }

    @Around("serviceMethods()")
    public Object logMethodExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();
        Object[] args = joinPoint.getArgs();

        log.info(">> Start execute method: {} with arguments: {}", methodName, Arrays.toString(args));

        long startTime = System.currentTimeMillis();

        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Throwable ex) {
            log.error("<< Exception in method: {} with cause: {}", methodName, ex.getMessage());
            throw ex;
        }

        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;

        log.info("<< Finished execute method: {} in {} ms with result: {}", methodName, executionTime, result);

        return result;
    }
}
