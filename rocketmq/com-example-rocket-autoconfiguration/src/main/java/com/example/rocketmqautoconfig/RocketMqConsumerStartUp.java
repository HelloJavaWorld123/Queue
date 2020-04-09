package com.example.rocketmqautoconfig;

import com.example.rocketmq.common.util.LogUtils;
import com.example.rocketmqautoconfig.properties.RocketMqConfigProperties;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @author : RXK
 * Date : 2020/4/9 12:00
 * Desc: 消费自动启动
 *
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
						start = true;
						LogUtils.info("启动Default Consumer 成功");
					} catch(MQClientException e){
						LogUtils.error("启动MQ Consumer 时 出现异常：",e);
						start = false;
					}
				}else{
					start = false;
					throw new IllegalArgumentException("Consumer Enable But MqConsumer Don't Configuration");
				}
			}
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
