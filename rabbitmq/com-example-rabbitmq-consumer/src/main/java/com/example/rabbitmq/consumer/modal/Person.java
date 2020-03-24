package com.example.rabbitmq.consumer.modal;

import lombok.Data;

import java.io.Serializable;

/**
 * @author : RXK
 * Date : 2020/3/24 16:14
 * Desc: Rabbit 消息接收到的消息 自动装换的类
 */
@Data
public class Person implements Serializable{
	private String id;
	private String userName;
}
