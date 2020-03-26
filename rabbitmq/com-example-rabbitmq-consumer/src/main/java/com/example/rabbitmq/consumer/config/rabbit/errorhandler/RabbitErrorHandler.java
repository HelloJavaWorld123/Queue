package com.example.rabbitmq.consumer.config.rabbit.errorhandler;

import com.example.rabbit.common.utils.LoggerUtils;
import org.springframework.amqp.rabbit.listener.ConditionalRejectingErrorHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.ErrorHandler;

/**
 * @author : RXK
 * Date : 2020/3/24 13:32
 * Desc: 直接实现该错误处理类需要自己实现对 AmqpRejectAndDontRequeueException 异常和 非致命异常 是否入队的处理逻辑
 * 如果 继承 {ConditionalRejectingErrorHandler.DefaultExceptionStrategy} 则在 源码的基础上再定义其他的异常是否是致命的
 *
 * @see ConditionalRejectingErrorHandler.DefaultExceptionStrategy
 */
@Deprecated
public class RabbitErrorHandler implements ErrorHandler{

	@Override
	public void handleError(Throwable t){
		LoggerUtils.error("创建SimpleRabbitListenerContainerFactory:",t);
	}
}
