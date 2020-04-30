package com.example.rocketmq.producer.service;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.remoting.exception.RemotingException;

/**
 * @author : RXK
 * Date : 2020/4/7 15:00
 * Desc:
 */
public interface RocketSendMessageService{
	void sendNormalMessage(String message) throws InterruptedException, RemotingException, MQClientException, MQBrokerException;

    void sendMessage(String tag, String body) throws InterruptedException, RemotingException, MQClientException, MQBrokerException;

    void sendTransactionMessage(String msg) throws MQClientException;
}
