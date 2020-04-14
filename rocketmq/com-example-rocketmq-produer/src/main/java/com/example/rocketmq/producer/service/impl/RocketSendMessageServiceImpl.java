package com.example.rocketmq.producer.service.impl;

import com.example.rocketmq.common.enums.RocketMQEnum;
import com.example.rocketmq.common.util.LogUtils;
import com.example.rocketmq.producer.service.RocketSendMessageService;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
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

	@Override
	public void sendMessage(String tag, String body) throws InterruptedException, RemotingException, MQClientException, MQBrokerException {
		Message message = new Message(RocketMQEnum.Topic.TEST_CUSTOMER_TOPIC_TAG_ONE.name(), tag, body.getBytes());
		defaultMQProducer.send(message, new SendCallback() {
			@Override
			public void onSuccess(SendResult sendResult) {
				LogUtils.info("发送成功的结果是：{"+sendResult.toString()+"}");
			}

			@Override
			public void onException(Throwable e) {
				LogUtils.info("发送失败的结果是：{"+e+"}");
			}
		});
	}
}
