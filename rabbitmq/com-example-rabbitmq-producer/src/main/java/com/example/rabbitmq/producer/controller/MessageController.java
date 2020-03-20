package com.example.rabbitmq.producer.controller;

import com.example.rabbit.common.enums.RabbitMqEnum;
import com.example.rabbitmq.producer.service.RabbitMqSendMessageService;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : RXK
 * Date : 2020/2/21 17:22
 * Desc:
 */
@RestController
@RequestMapping("/api/message")
public class MessageController{


	@Autowired
	private RabbitMqSendMessageService sendMessageService;


	@RequestMapping("/send")
	public String send(){
		CorrelationData correlationData = new CorrelationData();
		correlationData.setId("1");
		sendMessageService.sendMessage(RabbitMqEnum.ExchangeEnum.TEST_DIRECT_EXCHANGE.name(),RabbitMqEnum.RoutingKey.TEST_ROUTING_KEY.name(),"111111111",correlationData);
		return "发送成功";
	}


}
