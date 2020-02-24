package com.example.rabbitmq.producer.controller;

import com.example.rabbitmq.producer.enums.RabbitMqEnum;
import com.example.rabbitmq.producer.service.RabbitMqSendMessageService;
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
		sendMessageService.sendMessage(RabbitMqEnum.RoutingKey.TEST_ROUTING_KEY.name(),"111111111");
		return "发送成功";
	}


}
