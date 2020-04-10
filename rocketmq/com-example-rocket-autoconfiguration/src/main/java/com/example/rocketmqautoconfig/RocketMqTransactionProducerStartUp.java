package com.example.rocketmqautoconfig;

import com.example.rocketmq.common.util.LogUtils;
import com.example.rocketmqautoconfig.properties.RocketMqConfigProperties;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

/**
 * @author : RXK
 * Date : 2020/4/10 18:04
 * Desc: 启动事务生产者
 */
@Component
@EnableConfigurationProperties(value = {RocketMqConfigProperties.class})
public class RocketMqTransactionProducerStartUp implements SmartLifecycle{

	@Autowired(required = false)
	private TransactionMQProducer transactionMQProducer;

	@Autowired
	private RocketMqConfigProperties rocketMqConfigProperties;


	private volatile boolean start = false;

	@Override
	public void start(){
		if(!start){
			if(rocketMqConfigProperties.getProducer().isTransactionEnable()){
				try{
					LogUtils.info("启动 transactionProducer ");
					transactionMQProducer.start();
					start = true;
					LogUtils.info("启动 transactionProducer 成功");
				} catch(MQClientException e){
					LogUtils.error("启动transactionProducer异常:",e);
					start = false;
				}
			}
		}
	}

	@Override
	public void stop(){
		if(!start){
			LogUtils.info("开始停止 transactionProducer");
			transactionMQProducer.shutdown();
			start = false;
			LogUtils.info("停止 transactionProducer 成功");
		}
	}

	@Override
	public boolean isRunning(){
		return start;
	}

	@Override
	public boolean isAutoStartup(){
		return start;
	}

	@Override
	public int getPhase(){
		return Integer.MAX_VALUE;
	}
}
