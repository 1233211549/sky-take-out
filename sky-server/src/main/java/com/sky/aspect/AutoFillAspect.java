package com.sky.aspect;

import com.sky.annotation.Autofill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 定义切面
 */
@Aspect
@Component
@Slf4j
public class AutoFillAspect {

    /**
     * 定义切入点
     */
    @Pointcut("execution(* com.sky.mapper.*.*(..))&& @annotation(com.sky.annotation.Autofill)")
    public void autofillpointcut() {}
    /**
     * 前置通知,在通知中进行字段赋值
     */
    @Before("autofillpointcut()")
    public void before(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();//获取方法签名对象
        Autofill annotation = signature.getMethod().getAnnotation(Autofill.class);//获取方法上的注解对象
        val operationType = annotation.value();//获取数据库操作类型
        val args = joinPoint.getArgs();
        //如果传递为空则返回
        if (args == null || args.length == 0) {
            return;
        }
        Object entity = args[0];
        //准备赋值的数据
        LocalDateTime now = LocalDateTime.now();
        val currentId = BaseContext.getCurrentId();
        //通过反射将对应的属性赋值
        try {
            //赋值insert
            if (operationType == OperationType.INSERT) {
                Method SET_CREATE_TIME = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                Method SET_CREATE_USER = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
                Method SET_UPDATE_TIME = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method SET_UPDATE_USER = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
                SET_CREATE_TIME.invoke(entity, now);
                SET_CREATE_USER.invoke(entity, currentId);
                SET_UPDATE_TIME.invoke(entity, now);
                SET_UPDATE_USER.invoke(entity, currentId);
                //赋值update
            }else if (operationType == OperationType.UPDATE) {
                Method SET_UPDATE_TIME = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method SET_UPDATE_USER = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
                SET_UPDATE_TIME.invoke(entity, now);
                SET_UPDATE_USER.invoke(entity, currentId);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
