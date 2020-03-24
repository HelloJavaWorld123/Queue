package com.example.rabbitmq.consumer.config.annon;

import java.lang.annotation.*;

/**
 * @Repeatable 同一个注解可以在同一个方法上重复使用
 * @author admin
 */
@Target(value = {ElementType.METHOD,ElementType.TYPE})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface CustomerRabbitListeners{
	CustomerRabbitListener[] value();
}