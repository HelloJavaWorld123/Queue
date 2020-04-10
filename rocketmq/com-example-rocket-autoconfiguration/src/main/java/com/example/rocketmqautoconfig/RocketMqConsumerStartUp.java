package com.example.rocketmqautoconfig;

import com.example.rocketmq.common.util.LogUtils;
import com.example.rocketmqautoconfig.properties.RocketMqConfigProperties;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.MessageSelector;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.filter.ExpressionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * @author : RXK
 * Date : 2020/4/9 12:00
 * Desc: 消费自动启动
 * <p>
 * 必须有启动消费者的设置
 * spring.rocketmq.consumer.enable = true
 */
@Component
@EnableConfigurationProperties(value = {RocketMqConfigProperties.class})
public class RocketMqConsumerStartUp implements SmartLifecycle{


	@Autowired(required = false)
	private DefaultMQPushConsumer defaultMQPushConsumer;

	@Autowired
	private RocketMqConfigProperties rocketMqConfigProperties;


	private volatile boolean start = false;


	@Override
	public void start(){
		if(!start){
			if(rocketMqConfigProperties.getConsumer().isEnable()){
				if(Objects.nonNull(defaultMQPushConsumer)){
					try{
						LogUtils.info("开始启动Default Consumer");
						defaultMQPushConsumer.start();
						subscribeTopic();
						start = true;
						LogUtils.info("启动Default Consumer 成功");
					} catch(MQClientException e){
						LogUtils.error("启动MQ Consumer 时 出现异常：", e);
						start = false;
					}
				} else{
					start = false;
					throw new IllegalArgumentException("Consumer Enable But MqConsumer Don't Configuration");
				}
			}
		}
	}

	private void subscribeTopic(){
		if(CollectionUtils.isNotEmpty(rocketMqConfigProperties.getConsumerSubscribeInfoList())){
			List<RocketMqConfigProperties.ConsumerSubscribeInfo> infoList = rocketMqConfigProperties.getConsumerSubscribeInfoList();
			infoList.forEach(item -> {
				if(StringUtils.isNotEmpty(item.getTagType())){
					try{
						LogUtils.info("rocketMq Consumer 订阅了 {"+item.getTopic()+"}的消息");
						defaultMQPushConsumer.subscribe(item.getTopic(), getMessageSelector(item));
					} catch(MQClientException e){
						LogUtils.error("提交 订阅消息时 出现异常：",e);
					}
				}
			});
		}
	}

	private MessageSelector getMessageSelector(RocketMqConfigProperties.ConsumerSubscribeInfo item){
		if(StringUtils.equals(item.getTagType().trim().toUpperCase(), ExpressionType.TAG)){
			return MessageSelector.byTag(item.getExpression());
		} else if(StringUtils.equals(item.getTagType().trim().toUpperCase(), ExpressionType.SQL92)){
			return MessageSelector.bySql(item.getExpression());
		}else{
			throw new IllegalArgumentException("TAG Type Is wrong Exception");
		}
	}

	@Override
	public void stop(){
		if(!start){
			defaultMQPushConsumer.shutdown();
			start = false;
		}
	}

	@Override
	public boolean isRunning(){
		return start;
	}

	@Override
	public boolean isAutoStartup(){
		return Boolean.TRUE;
	}

	@Override
	public int getPhase(){
		return Integer.MAX_VALUE;
	}
}
