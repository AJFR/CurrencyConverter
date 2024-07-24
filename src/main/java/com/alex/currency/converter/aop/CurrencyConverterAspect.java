package com.alex.currency.converter.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Slf4j
@Component
public class CurrencyConverterAspect {

    @Around(value = "execution(* com.alex.currency.converter.controller.CurrencyConverterRestController.*(..))")
    public Object aroundControllerCalls(ProceedingJoinPoint pjp) throws Throwable {
        log.info("Called: [{}], with args: [{}]", pjp.getSignature().getName(), pjp.getArgs());
        Object result = pjp.proceed();
        log.info("Returning: {}", result);

        return result;
    }

    @Around(value = "execution(* com.alex.currency.converter.exception.handler.CurrencyConverterExceptionHandler.*(..))")
    public Object aroundExceptions(ProceedingJoinPoint pjp) throws Throwable {
        log.info("Exception: [{}]", pjp.getArgs());
        Object result = pjp.proceed();
        log.info("Returning: {}", result);

        return result;
    }

}
