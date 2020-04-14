package com.example.rocketmq.producer.controller;

import com.example.rocketmq.common.util.LogUtils;
import com.example.rocketmq.producer.service.RocketSendMessageService;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

/**
 * @author : RXK
 * Date : 2020/4/7 15:01
 * Desc:
 */
@RestController
@RequestMapping("/send/message")
public class RocketMessageController{


	@Autowired
	private RocketSendMessageService rocketSendMessageService;

	@RequestMapping("/info")
	public ResponseEntity<String> sendNormalMessage(){
		String message = "1111111111111111111111111";
		for(int i = 0; i < 100; i++){
			message += i;
		}
		try{
			rocketSendMessageService.sendNormalMessage(message);
		} catch(InterruptedException | RemotingException | MQClientException | MQBrokerException e){
			LogUtils.error("发送消息失败：",e);
			return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(e.getMessage());
		}
		return ResponseEntity.ok().body("ok");
	}


	@RequestMapping("/topic")
	public ResponseEntity<String> sendMoreTopicMessage(){
		String body = "测试多tag";
		String tag = "TAG";
		int nextInt = new Random().nextInt(100);
		try {
			rocketSendMessageService.sendMessage(tag+nextInt,body+nextInt);
		} catch (InterruptedException | RemotingException | MQClientException | MQBrokerException e) {
			return ResponseEntity.ok().body("error");
		}
		return ResponseEntity.ok().body("ok");
	}

}
