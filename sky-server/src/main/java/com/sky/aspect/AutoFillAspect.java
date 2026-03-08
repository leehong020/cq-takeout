package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.apache.xmlbeans.impl.xb.xsdschema.Public;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

//自定义切面类，处理公共字段的自动填充
@Aspect
@Component
@Slf4j
public class AutoFillAspect {
    //切入点
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointCut() {
        //定义前置通知

    }
    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        log.info("自动填充公共字段...");
        //获取当前被拦截的数据库操作类型
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();//方法签名注解
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);//方法上的注解对象
        OperationType operationType = autoFill.value();//获取数据库操作类型
        //获取当前被拦截方法的实体参数———实体对象
            Object[] args = joinPoint.getArgs();
            if (args == null || args.length == 0) {
                return;//没有参数，直接返回
            }
            Object entity = args[0];//获取第一个参数，假设第一个参数就是实体对象
            log.info("被拦截的方法：{}，操作类型：{}，实体对象：{}", signature.getMethod(), operationType, entity);
        //准备赋值的数据
        Long currentId = BaseContext.getCurrentId();//假设当前登录用户的id为1
        LocalDateTime now = LocalDateTime.now();//当前时间
        //根据操作类型，给不同的字段赋值
        if (operationType == OperationType.INSERT) {
            //给创建时间、修改时间、创建人、修改人赋值
            Method setCreateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
            Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
            Method setCreateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
            Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
            setCreateTime.invoke(entity, now);
            setUpdateTime.invoke(entity, now);
            setCreateUser.invoke(entity, currentId);
            setUpdateUser.invoke(entity, currentId);
            //反射给字段赋值
        } else if (operationType == OperationType.UPDATE) {
            //给修改时间、修改人赋值
            //反射给字段赋值
//            Method setCreateTime = signature.getMethod().getDeclaringClass().getMethod("setCreateTime", LocalDateTime.class);
            Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
//            Method setCreateUser = signature.getMethod().getDeclaringClass().getMethod("setCreateUser", Long.class);
            Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
//            setCreateTime.invoke(entity, now);
            setUpdateTime.invoke(entity, now);
//            setCreateUser.invoke(entity, currentId);
            setUpdateUser.invoke(entity, currentId);
        }
        //根据当前不同的操作类型，给不同的字段通过反射赋值
    }
}
