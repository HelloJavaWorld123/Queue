package com.example.rabbit.common.enums;

/**
 * @author : RXK
 * Date : 2020/2/21 17:37
 * Desc: rabbit 先关 枚举类
 */
public enum RabbitMqEnum{
	;
	public enum ExchangeEnum{
		/**
		 * 测试 单播 交换机
		 */
		TEST_DIRECT_EXCHANGE
		;
	}

	public enum QueueEnum{
		/**
		 * 测试队列
		 */
		TEST_QUEUE
		;
	}

	public enum RoutingKey{
		/**
		 * 测试路由key
		 */
		TEST_ROUTING_KEY
		;
	}
}
