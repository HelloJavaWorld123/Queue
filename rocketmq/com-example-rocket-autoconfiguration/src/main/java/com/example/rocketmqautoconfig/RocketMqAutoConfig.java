package com.example.rocketmqautoconfig;

import com.example.rocketmq.common.util.IpUtils;
import com.example.rocketmqautoconfig.properties.RocketMqConfigProperties;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.MQConsumer;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.common.MixAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author : RXK
 * Date : 2020/4/7 15:09
 * Desc: 配置 RocketMq 的 DefaultRocketMqProducer
 */
@Configuration
@ConditionalOnProperty(prefix = "spring.rocketmq", name = "nameServerAddress", matchIfMissing = true,havingValue = "true")
@EnableConfigurationProperties(value = {RocketMqConfigProperties.class})
public class RocketMqAutoConfig{

	@Autowired
	private RocketMqConfigProperties rocketMqConfigProperties;


	@Bean
	@ConditionalOnMissingBean(value = DefaultMQProducer.class)
	@SuppressWarnings("uncheck")
	public DefaultMQProducer defaultMQProducer(){
		DefaultMQProducer producer = new DefaultMQProducer();
		producer.setNamesrvAddr(getNameSrvAddr());

		producer.setInstanceName(getInstanceName());
		producer.setUnitName(getUnitName());

		producer.setClientCallbackExecutorThreads(rocketMqConfigProperties.getClientCallBackExecutorThreads());

		producer.setNamespace(rocketMqConfigProperties.getNameSpace());

		producer.setPollNameServerInterval(rocketMqConfigProperties.getPollNameServerInterval());
		producer.setHeartbeatBrokerInterval(rocketMqConfigProperties.getHeartbeatBrokerInterval());



		return producer;
	}


	private String getUnitName(){
		String unitName = rocketMqConfigProperties.getUnitName();
		if(StringUtils.isNotEmpty(unitName)){
			return unitName;
		}else{
			return null;
		}
	}


	private String getInstanceName(){
		String instanceName = rocketMqConfigProperties.getInstanceName();
		if(StringUtils.isNotEmpty(instanceName)){
			return instanceName;
		} else if("DEFAULT".equals(instanceName)){
			return IpUtils.ipAndName();
		} else if(StringUtils.isEmpty(instanceName)){
			return String.valueOf(MixAll.getPID());
		}else{
			return null;
		}
	}

	private String getNameSrvAddr(){
		String nameServerAddress = rocketMqConfigProperties.getNameServerAddress();
		if(StringUtils.isEmpty(nameServerAddress)){
			throw new IllegalArgumentException("RocketMQ Producer NameServer Address Con't Be NULL");
		}
		return nameServerAddress;
	}


	@Bean
	@ConditionalOnMissingBean(TransactionMQProducer.class)
	public TransactionMQProducer transactionMQProducer(){
		TransactionMQProducer transactionMQProducer = new TransactionMQProducer();
		return transactionMQProducer;
	}


}
