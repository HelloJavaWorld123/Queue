package com.example.rabbitmq.consumer.config.rabbit.errorhandler;

import com.example.rabbit.common.utils.LoggerUtils;
import org.springframework.amqp.rabbit.listener.ConditionalRejectingErrorHandler;
import org.springframework.stereotype.Component;

/**
 * @author : RXK
 * Date : 2020/3/25 13:18
 * Desc:
 * 根据异常类型 配置 消息是否重新入队的策略
 * 默认重新入队的异常包括：消息转换异常，类转换异常,方法参数无效异常,方法参数类型不配置异常，找不到方法的异常
 * 默认不出重新入队的异常:{@link org.springframework.amqp.AmqpRejectAndDontRequeueException}
 *
 * 如果想要重新扩展是否重新入队的逻辑，实现ErrorHandler，覆盖handlerError 方法；
 * 或者继承 {@link ConditionalRejectingErrorHandler.DefaultExceptionStrategy},在原来的基础上自定义致命的异常；
 *
 * 如果是 致命的异常:消息的头部 会增加一个 x-death 标识 以及异常的内容，并将 消息推送到 DLQ
 *
 */
@Component
public class CustomerExceptionStrategy extends ConditionalRejectingErrorHandler.DefaultExceptionStrategy{

	/**
	 * 根据当前的异常类型 是否是致命的原因
	 * 这将决定 消息是否进入 Dead Letter Queue
	 *
	 */
	@Override
	protected boolean isUserCauseFatal(Throwable cause){
		Throwable cause1 = cause.getCause();
		LoggerUtils.error("isUserCauseFatal 的异常是：",cause);
		return Boolean.TRUE;
	}
}
