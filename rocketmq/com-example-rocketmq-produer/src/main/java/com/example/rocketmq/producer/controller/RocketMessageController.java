package com.example.rocketmq.producer.controller;

import com.example.rocketmq.producer.service.RocketSendMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
		String message = "";
		rocketSendMessageService.sendNormalMessage(message);
		return ResponseEntity.ok().body("ok");
	}


}
