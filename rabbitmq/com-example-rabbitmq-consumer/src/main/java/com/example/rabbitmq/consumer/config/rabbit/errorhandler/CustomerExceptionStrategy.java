package com.example.rabbitmq.consumer.config.rabbit.errorhandler;

import com.example.rabbit.common.utils.LoggerUtils;
import org.springframework.amqp.rabbit.listener.ConditionalRejectingErrorHandler;
import org.springframework.stereotype.Component;

/**
 * @author : RXK
 * Date : 2020/3/25 13:18
 * Desc:
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
