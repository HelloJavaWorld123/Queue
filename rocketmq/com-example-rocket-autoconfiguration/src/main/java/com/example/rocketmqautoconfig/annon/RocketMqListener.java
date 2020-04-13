package com.example.rocketmqautoconfig.annon;

import org.apache.rocketmq.common.filter.ExpressionType;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author : RXK
 * Date : 2020/4/7 15:01
 * Desc: 注册Listener 的注解
 * 在consumer启动的时候 获取到需要注册的监听器 进行注册
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RocketMqListener {
    /**
     * 当前消费者所属组
     */
    String consumerGroup() default "";

    String topic() default "";

    String selectExpressionType() default ExpressionType.TAG;

    String expression() default "*";
}
