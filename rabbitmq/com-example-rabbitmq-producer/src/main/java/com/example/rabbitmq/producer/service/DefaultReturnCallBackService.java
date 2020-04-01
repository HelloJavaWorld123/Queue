package com.example.rabbitmq.producer.service;

import com.example.rabbit.common.utils.LoggerUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

/**
 * @author : RXK
 * Date : 2020/3/20 18:33
 * Desc: 默认处理投递失败时 只打印日志或者发送到日志处理中心
 */
@Service
public class DefaultReturnCallBackService implements RabbitTemplate.ReturnCallback{


	@Override
	public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey){
		LoggerUtils.info(String.format("ReturnCallBack消息体是:{%s},回复编码是:{%s},回复的内容是:{%s},交换机是:{%s},路由key是:{%s}", message.toString(), replyCode, replyText, exchange, routingKey));
		processHandleReturnCallBack(message,replyCode,replyText,exchange,routingKey);
	}
	protected void processHandleReturnCallBack(Message message, int replyCode, String replyText, String exchange, String routingKey){ }
}
