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
		TEST_DIRECT_EXCHANGE,
		TEST_FANOUT_EXCHANGE,
		TEST_DEAD_LETTER_EXCHANGE,
		TEST_TRANSACTION_DIRECT_EXCHANGE
		;
	}

	public enum QueueEnum{
		/**
		 * 测试队列
		 */
		TEST_QUEUE,
		TEST_ONE_EXCHANGE_QUEUES_ONE,
		TEST_ONE_EXCHANGE_QUEUES_TWO,
		TEST_DURABLE_QUEUE,
		TEST_X_DEAD_LETTER_EXCHANGE_ARG,
		TEST_LOG_QUEUE,
		TEST_TRANSACTION_QUEUE
		;
	}

	public enum RoutingKey{
		/**
		 * 测试路由key
		 */
		TEST_ROUTING_KEY,
		TEST_ONE_EXCHANGE_BINDING_TWO_QUEUES,
		TEST_DEAD_LETTER_EXCHANGE_KEY,
		TEST_TRANSACTION_KEY,
		RABBIT_PRODUCER_LOG
		;
	}
}
