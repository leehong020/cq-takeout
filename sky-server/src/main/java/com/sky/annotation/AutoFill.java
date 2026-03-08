package com.sky.annotation;

import com.sky.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)//指定注解只能加在方法上面
@Retention(RetentionPolicy.RUNTIME)//指定注解在运行时生效
public @interface AutoFill {

    OperationType value();//注解的属性，表示数据库操作类型

}
