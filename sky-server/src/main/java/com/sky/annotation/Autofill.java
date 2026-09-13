package com.sky.annotation;

import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.*;

/**
 * 自定义注解：自动填充注解
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Autofill {
    OperationType value();
}
