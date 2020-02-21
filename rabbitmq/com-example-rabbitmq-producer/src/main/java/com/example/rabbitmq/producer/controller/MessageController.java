package com.example.rabbitmq.producer.controller;

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


	@RequestMapping("/send")
	public String send(){
		return "发送成功";
	}


}
