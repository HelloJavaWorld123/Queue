package com.example.rabbitmq.consumer.config.rabbit.errorhandler;

import org.springframework.amqp.rabbit.listener.ConditionalRejectingErrorHandler;
import org.springframework.stereotype.Component;

/**
 * @author : RXK
 * Date : 2020/3/25 13:18
 * Desc:
 */
@Component
public class CustomerExceptionStrategy extends ConditionalRejectingErrorHandler.DefaultExceptionStrategy{
	@Override
	protected boolean isUserCauseFatal(Throwable cause){
		return Boolean.TRUE;
	}
}
