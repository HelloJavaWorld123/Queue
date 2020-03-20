package com.example.rabbitmq.producer.config;

import com.example.rabbit.common.utils.LoggerUtils;
import com.example.rabbitmq.producer.service.DefaultReturnCallBackService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * @author : RXK
 * Date : 2020/3/19 18:52
 * Desc:
 *
 *  *           ConfirmCallBack
 *  * Producer  ---------------> Rabbit Cluster Broker -----> Exchange ----> Queue ----> Consumer
 *  *           <--------------                        <-----          <----
 *  *            ReturnCallBack
 *
 *  Exchange 在将消息发送到无效的Queue时 会回调该ReturnCallBack,如果成功投递到Queue中会不调用,不管Queue是否有消费者
 *
 * @see DefaultReturnCallBackService
 */
@Deprecated
public class GlobalReturnCallBack implements RabbitTemplate.ReturnCallback{

	/**
	 * 返回消息回调
	 * @param message ： 消息体
	 * @param replyCode ： 回复码
	 * @param replyText ： 回复内容
	 * @param exchange ： 交换机
	 * @param routingKey ： 路由键
	 */
	@Override
	public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey){
		LoggerUtils.info(String.format("----消息体是:{%s},回复编码是:{%s},回复的内容是:{%s},交换机是:{%s},路由key是:{%s}-------", message.toString(), replyCode, replyText, exchange, routingKey));
	}
}
