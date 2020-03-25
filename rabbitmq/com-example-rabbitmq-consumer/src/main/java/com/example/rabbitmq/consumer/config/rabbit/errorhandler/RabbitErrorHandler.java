package com.example.rabbitmq.consumer.config.rabbit.errorhandler;

import com.example.rabbit.common.utils.LoggerUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ErrorHandler;

/**
 * @author : RXK
 * Date : 2020/3/24 13:32
 * Desc:
 */
@Component
public class RabbitErrorHandler implements ErrorHandler{

	@Override
	public void handleError(Throwable t){
		LoggerUtils.error("创建SimpleRabbitListenerContainerFactory:",t);
	}
}
