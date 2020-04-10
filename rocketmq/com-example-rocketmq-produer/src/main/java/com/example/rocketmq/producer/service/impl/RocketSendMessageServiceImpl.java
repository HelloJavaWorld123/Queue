package com.example.rocketmq.producer.service.impl;

import com.example.rocketmq.common.enums.RocketMQEnum;
import com.example.rocketmq.producer.service.RocketSendMessageService;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author : RXK
 * Date : 2020/4/7 15:01
 * Desc:
 */
@Service
public class RocketSendMessageServiceImpl implements RocketSendMessageService{

	@Autowired
	private DefaultMQProducer defaultMQProducer;


	@Override
	public void sendNormalMessage(String messageBody) throws InterruptedException, RemotingException, MQClientException, MQBrokerException{
		Message message = new Message(
				RocketMQEnum.Topic.TEST_CUSTOMER_TOPIC_TAG_ONE.name(),
				RocketMQEnum.Tag.TEST_CUSTOMER_TAG_ONE.name(),
				messageBody.getBytes()
		);
		message.setInstanceId("10000000L");
		message.setFlag(1);
		defaultMQProducer.send(message);
	}
}
