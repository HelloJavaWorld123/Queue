package com.example.rabbitmq.producer.service;

import com.example.rabbit.common.utils.LoggerUtils;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author : RXK
 * Date : 2020/3/20 18:20
 * Desc:
 */
@Service
public class DefaultConfirmCallBackService implements RabbitTemplate.ConfirmCallback{


	@Override
	public void confirm(CorrelationData correlationData, boolean ack, String cause){
		if(Objects.nonNull(correlationData)){
			LoggerUtils.info(String.format("Confirm Call Back 消息体id是:{{%s}},确认消息内容是:{{%s}},是否已经确认:{{%s}},cause:{{%s}}", correlationData.toString(), correlationData.getReturnedMessage(), ack, cause));
		} else{
			LoggerUtils.info("是否已经确认:{"+ack+"},cause:{"+cause+"}");
		}

		processHandle(correlationData,ack,cause);
	}

	protected void processHandle(CorrelationData correlationData, boolean ack, String cause){};
}
