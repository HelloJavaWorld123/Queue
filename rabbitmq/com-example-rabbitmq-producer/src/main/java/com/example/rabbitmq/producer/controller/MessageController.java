package com.example.rabbitmq.producer.controller;

import com.example.rabbit.common.enums.RabbitMqEnum;
import com.example.rabbit.common.utils.LoggerUtils;
import com.example.rabbitmq.producer.service.RabbitMqSendMessageService;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.FailureCallback;
import org.springframework.util.concurrent.SuccessCallback;
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


	/**
	 * 简单的发送带有自定义唯一标识的信息数据
	 */
	@RequestMapping("/send")
	public ResponseEntity<String> send(){
		CorrelationData correlationData = new CorrelationData();
		correlationData.setId("normal-message-1");
		sendMessageService.sendMessage(RabbitMqEnum.ExchangeEnum.TEST_DIRECT_EXCHANGE.name(),RabbitMqEnum.RoutingKey.TEST_ROUTING_KEY.name(),"111111111",correlationData);
		return ResponseEntity.ok().body("发送成功");
	}


	/**
	 * 发送消息并带有CallBack回调机制
	 */
	@RequestMapping("/send/callback")
	public ResponseEntity<String> sendAndCallBack(){
		CorrelationData correlationData = new CorrelationData();
		correlationData.setId("call-back-message-2");
		sendMessageService.sendMessage(RabbitMqEnum.ExchangeEnum.TEST_DIRECT_EXCHANGE.name(),RabbitMqEnum.RoutingKey.TEST_ROUTING_KEY.name(),"22222",correlationData);
		//回调确认机制
		correlationData.getFuture().addCallback(new SuccessCallback<CorrelationData.Confirm>(){
			@Override
			public void onSuccess(CorrelationData.Confirm result){
				LoggerUtils.info("接收到消费方的确认回应:{"+result.toString()+"}");
			}
		}, new FailureCallback(){
			@Override
			public void onFailure(Throwable ex){
				LoggerUtils.error("接收到异常的回调：",ex);
			}
		});
		return ResponseEntity.ok().body("ok");
	}

	@RequestMapping("/send/transactional")
	public ResponseEntity<String> sendAndUseSameChannel(){
		CorrelationData correlationData = new CorrelationData();
		correlationData.setId("10000");
		sendMessageService.sendTransactionMessage(correlationData,"This Is A Fall Transaction's Message");
		return ResponseEntity.ok().body("ok");
	}


	@RequestMapping("/send/waitForConfirm")
	public ResponseEntity<String> sendWaitForConfirms(){
		CorrelationData correlationData = new CorrelationData();
		correlationData.setId("200000");
		sendMessageService.sendWaitConfirmCallBack("111111",correlationData);
		return ResponseEntity.ok().body("OK");
	}

	@RequestMapping("/send/delayMessage")
	public ResponseEntity<String> sendDelayMessage(){
		CorrelationData data = new CorrelationData();
		data.setId("300000");
		sendMessageService.sendDelayMessage("2222222222222",data);
		return ResponseEntity.ok().body("ok");
	}


}
