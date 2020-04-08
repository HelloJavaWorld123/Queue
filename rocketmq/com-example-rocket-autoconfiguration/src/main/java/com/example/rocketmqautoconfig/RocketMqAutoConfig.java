package com.example.rocketmqautoconfig;

import com.example.rocketmq.common.util.IpUtils;
import com.example.rocketmq.common.util.LogUtils;
import com.example.rocketmqautoconfig.properties.RocketMqConfigProperties;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.MQConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
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
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;
import java.util.concurrent.ExecutorService;

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
	private ApplicationContext applicationContext;

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

		producer.setProducerGroup(rocketMqConfigProperties.getProducer().getProducerGroup());
		producer.setDefaultTopicQueueNums(rocketMqConfigProperties.getProducer().getDefaultTopicQueueNums());
		producer.setRetryTimesWhenSendAsyncFailed(rocketMqConfigProperties.getProducer().getRetryTimesWhenSendAsyncFailed());
		producer.setRetryAnotherBrokerWhenNotStoreOK(rocketMqConfigProperties.getProducer().isRetryAnOtherBrokerWhenNotStoreOk());

		return producer;
	}

	@Bean
	@ConditionalOnMissingBean(TransactionMQProducer.class)
	public TransactionMQProducer transactionMQProducer(){
		TransactionMQProducer transactionMQProducer = new TransactionMQProducer();
		transactionMQProducer.setNamesrvAddr(getNameSrvAddr());

		transactionMQProducer.setInstanceName(getInstanceName());
		transactionMQProducer.setUnitName(getUnitName());

		transactionMQProducer.setClientCallbackExecutorThreads(rocketMqConfigProperties.getClientCallBackExecutorThreads());

		transactionMQProducer.setNamespace(rocketMqConfigProperties.getNameSpace());

		transactionMQProducer.setPollNameServerInterval(rocketMqConfigProperties.getPollNameServerInterval());
		transactionMQProducer.setHeartbeatBrokerInterval(rocketMqConfigProperties.getHeartbeatBrokerInterval());

		transactionMQProducer.setProducerGroup(rocketMqConfigProperties.getProducer().getProducerGroup());
		transactionMQProducer.setDefaultTopicQueueNums(rocketMqConfigProperties.getProducer().getDefaultTopicQueueNums());
		transactionMQProducer.setRetryTimesWhenSendAsyncFailed(rocketMqConfigProperties.getProducer().getRetryTimesWhenSendAsyncFailed());
		transactionMQProducer.setRetryAnotherBrokerWhenNotStoreOK(rocketMqConfigProperties.getProducer().isRetryAnOtherBrokerWhenNotStoreOk());

		/**
		 * 事务句柄的参数设置
		 */

		transactionMQProducer.setExecutorService(getExecutorService());


		return transactionMQProducer;
	}

	private ExecutorService getExecutorService(){
		String executorServiceBeanName = rocketMqConfigProperties.getProducer().getExecutorServiceBeanName();
		if(StringUtils.isNotEmpty(executorServiceBeanName)){
			Object applicationContextBean = applicationContext.getBean(executorServiceBeanName);
		}else{

		}
		return null;
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
}
