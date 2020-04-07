package com.example.rocketmqautoconfig.properties;

import lombok.Data;
import org.apache.rocketmq.client.AccessChannel;
import org.apache.rocketmq.client.trace.TraceDispatcher;
import org.apache.rocketmq.remoting.netty.TlsSystemConfig;
import org.apache.rocketmq.remoting.protocol.LanguageCode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * @author : RXK
 * Date : 2020/4/7 15:10
 * Desc:
 */
@Data
@ConfigurationProperties(prefix = "spring.rocketmq")
public class RocketMqConfigProperties{

	/**
	 * namvesrv的ip地址
	 * 多个地址使用 分号隔开 ip:port;ip:port
	 */
	private String nameServerAddress;

	/**
	 * Rocket Client Name
	 */
	private String instanceName;

	/**
	 * MQClientId = ip@instanceName@unitName
	 */
	private String unitName;

	/**
	 * 如果为空则使用默认的核数
	 */
	private int clientCallBackExecutorThreads = Runtime.getRuntime().availableProcessors();


	private String nameSpace;

	/**
	 * client 与 Message Broker 的链接心跳间隔 单位 毫秒ms
	 */
	private int heartbeatBrokerInterval = 30 * 1000;

	/**
	 * 从 Name Server 拉取 topic Information 的时间间隔 单位 毫秒ms
	 */
	private int pollNameServerInterval = 30 * 1000;

	private int pullTimeDelayMillsWhenException = 1000;

	private boolean unitMode = false;

	private boolean vipChannelEnabled = false ;

	private boolean useTLS = TlsSystemConfig.tlsEnable;

	private LanguageCode languageCode = LanguageCode.JAVA;

	private AccessChannel accessChannel = AccessChannel.LOCAL;

	private RocketMqConfigProperties.Producer producer = new RocketMqConfigProperties.Producer();

	private RocketMqConfigProperties.Consumer consumer = new RocketMqConfigProperties.Consumer();

	/**
	 * 生产者配置
	 */
	@Data
	public static class Producer{

		/**
		 *事务消息时 很重要（将Producer进行归类）
		 * 如果不是事务消息 只要保证唯一
		 */
		private String producerGroup;

		private String createTopicKey;

		/**
		 * Number of queues to create per default topic.
		 */
		private int defaultTopicQueueNums = 5;


		private int sendMsgTimout = 3000;

		/**
		 * 压缩消息体的阈值 默认值 4k
		 */
		private int compressMsgBodyOverHowMach = 1024 * 4;

		/**
		 * 可能会导致 消息重复
		 */
		private int retryTimesWhenSendFailed = 3;

		private int retryTimesWhenSendAsyncFailed = 3;


		private boolean retryAnOtherBrokerWhenNotStoreOk = false;

		private int maxMessageSize = 1024 * 1024 * 4;

		private TraceDispatcher traceDispatcher = null;

	}

	/**
	 * 消费者配置
	 */
	public static class Consumer{

	}


}