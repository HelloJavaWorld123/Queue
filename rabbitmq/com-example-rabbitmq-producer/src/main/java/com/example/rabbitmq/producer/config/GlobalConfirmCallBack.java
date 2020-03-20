package com.example.rabbitmq.producer.config;

import com.example.rabbit.common.utils.LoggerUtils;
import com.example.rabbitmq.producer.service.DefaultConfirmCallBackService;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @author : RXK
 * Date : 2020/3/19 18:49
 * Desc:
 * ConfirmCallBack
 * Producer  ---------------> Rabbit Cluster Broker ---------> Exchange ---------> Queue ----> Consumer
 *          <--------------                        <--------          <----------
 *          ReturnCallBack                         message投递Queue   message 投递Queue 失败
 *
 * @see DefaultConfirmCallBackService
 */
@Deprecated
public class GlobalConfirmCallBack implements RabbitTemplate.ConfirmCallback{

	/**
	 * 确认消息的回调
	 *
	 * @param correlationData ： 消息id  以及确认信息
	 * @param ack ： 是否确认
	 * @param cause ：
	 */
	@Override
	public void confirm(CorrelationData correlationData, boolean ack, String cause){
		if(Objects.nonNull(correlationData)){
			LoggerUtils.info(String.format("返回的确认的消息体id是:{{%s}},确认消息内容是:{{%s}},是否已经确认:{{%s}},cause:{{%s}}", correlationData.toString(), correlationData.getReturnedMessage(), ack, cause));
		} else{
			LoggerUtils.info("是否已经确认:{"+ack+"},cause:{"+cause+"}");
		}
	}
}
