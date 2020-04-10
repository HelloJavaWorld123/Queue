package com.example.rocketmq.common.enums;

/**
 * @author : RXK
 * Date : 2020/4/8 11:53
 * Desc:
 */
public enum RocketMQEnum{
	;

	public enum Topic{
		/**
		 * 测试 topic
		 */
		TEST_CUSTOMER_TOPIC_TAG_ONE,
		TEST_CUSTOMER_TOPIC_TAG_TWO,
		TEST_CUSTOMER_TOPIC_SQL_ONE,
		TEST_CUSTOMER_TOPIC_SQL_TWO
		;
	}

	public enum Tag{
		/**
		 * 测试 tag
		 */
		TEST_CUSTOMER_TAG_ONE,
		TEST_CUSTOMER_TAG_TWO
		;
	}
}
