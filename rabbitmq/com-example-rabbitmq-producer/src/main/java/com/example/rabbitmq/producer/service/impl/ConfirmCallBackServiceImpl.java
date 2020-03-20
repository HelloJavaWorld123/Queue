package com.example.rabbitmq.producer.service.impl;

import com.example.rabbit.common.utils.LoggerUtils;
import com.example.rabbitmq.producer.service.DefaultConfirmCallBackService;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author : RXK
 * Date : 2020/3/20 18:21
 * Desc:
 */
@Service
public class ConfirmCallBackServiceImpl extends DefaultConfirmCallBackService{

	@Override
	protected void processHandle(CorrelationData correlationData, boolean ack, String cause){
		if(Objects.nonNull(correlationData)){
			LoggerUtils.info("{子类继续处理}");
		} else{
			LoggerUtils.info("{子类继续处理}是否已经确认:{"+ack+"},cause:{"+cause+"}");
		}
	}
}
