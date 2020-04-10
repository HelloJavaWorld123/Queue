package com.example.rocketmqautoconfig.properties;

import lombok.Data;
import org.apache.rocketmq.client.AccessChannel;
import org.apache.rocketmq.client.consumer.AllocateMessageQueueStrategy;
import org.apache.rocketmq.client.consumer.MessageSelector;
import org.apache.rocketmq.client.consumer.listener.MessageListener;
import org.apache.rocketmq.client.consumer.store.OffsetStore;
import org.apache.rocketmq.client.trace.TraceDispatcher;
import org.apache.rocketmq.common.UtilAll;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.apache.rocketmq.remoting.netty.TlsSystemConfig;
import org.apache.rocketmq.remoting.protocol.LanguageCode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import sun.rmi.runtime.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

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

	private boolean vipChannelEnabled = false;

	private boolean useTLS = TlsSystemConfig.tlsEnable;

	private LanguageCode languageCode = LanguageCode.JAVA;

	private AccessChannel accessChannel = AccessChannel.LOCAL;

	private RocketMqConfigProperties.Producer producer = new RocketMqConfigProperties.Producer();

	private RocketMqConfigProperties.Consumer consumer = new RocketMqConfigProperties.Consumer();

	private RocketMqConfigProperties.ConsumerThreadPool consumerThreadPool = new RocketMqConfigProperties.ConsumerThreadPool();

	private List<ConsumerSubscribeInfo> consumerSubscribeInfoList = new ArrayList<>();


	/**
	 * 生产者配置
	 */
	@Data
	public static class Producer{

		/**
		 * 是否启动 Producer
		 */
		private boolean enable;

		/**
		 * 事务消息时 很重要（将Producer进行归类）
		 * 如果不是事务消息 只要保证唯一
		 * 不能使用默认的 org.apache.rocketmq.client.exception.MQClientException： producerGroup can not equal DEFAULT_PRODUCER, please specify another one
		 * 区分 生产者
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

		/**
		 * TransactionMQProducer 检查小城池的最小数量
		 * 默认值为 1
		 */
		@Deprecated
		private int checkThreadPoolMinSize = 1;

		/**
		 * TransactionMQProducer 检查小城池的最大数量
		 * 默认值为 10
		 */
		@Deprecated
		private int checkThreadPoolMaxSize = 10;

		/**
		 * TransactionMQProducer
		 * 默认值 2000
		 */
		@Deprecated
		private int checkRequestHoldMax = 2000;

		/**
		 * TransactionMQProducer
		 * 自定义线程池的名字
		 * 默认为 : producerExecutorService
		 */
		private String executorServiceBeanName;

		/**
		 * TransactionMQProducer
		 */
		private String transactionListenerBeanName;

	}

	/**
	 * 消费者配置
	 */
	@Data
	public static class Consumer{

		/**
		 * 是否启用Consumer
		 * 会启动 Consumer
		 */
		private boolean enable;

		/**
		 * 消费者所属群组
		 */
		private String consumerGroup;

		/**
		 * 最少消费者线程数量
		 * 默认值 20
		 */
		private int consumerThreadMin = 20;

		/**
		 * 消费者最大的线程数量
		 * 默认值 20
		 */
		private int consumerThreadMax = 20;

		/**
		 * 消息被重复消费失败多少次后  进入正在等待删除的队列
		 * 默认值为 3
		 */
		private int maxReconsumeTimes = 3;

		/**
		 * 默认值 100000
		 */
		private long adjustThreadPoolNumsThreshold = 100000;

		/**
		 * 默认值 200
		 */
		private int consumeConcurrentlyMaxSpan = 200;


		/**
		 * 每一个消息队列中能够缓存消息条数的最多数量的阈值
		 * 只计算了消息体总大小 不精确的消息体积
		 * 默认值 1000
		 */
		private int pullThresholdForQueue = 1000;

		/**
		 * 每一个消息队列中能够缓存的消息体积的最大的阈值
		 * 默认值 100M
		 */
		private int pullThresholdSizeForQueue = 100;

		/**
		 * Topic级别
		 * {@code pullThresholdForQueue}将会失效,根据{@code pullThresholdForTopic}重新计算
		 * <p>
		 * 默认值 -1 无限制
		 */
		private int pullThresholdForTopic = -1;

		/**
		 * Topic 级别
		 * {@code pullThresholdSizeForQueue}会被覆盖
		 * eg:pullThresholdSizeForTopic 为 1000M 有10个messageQueue
		 * 那么 每个pullThresholdSizeForQueue 是100m
		 * 默认值 -1 无限制
		 */
		private int pullThresholdSizeForTopic = -1;

		/**
		 * pull message 的时间间隔
		 * 默认值为 0
		 */
		private long pullInterval = 0;

		/**
		 * 消费消息的批数量
		 * 默认值 1
		 */
		private int consumeMessageBatchMaxSize = 1;


		/**
		 * 拉取消息的批数量
		 */
		private int pullBatchSize = 32;

		/**
		 * 默认 false
		 */
		private boolean postSubscriptionWhenPull = false;

		/**
		 * clustering Or Broadcasting
		 * 一定数量的消息，是所有消费者一共消费的总量，还是每一个消费者都消费所有的消息
		 * 默认值为 Clustering
		 */
		private MessageModel messageModel = MessageModel.CLUSTERING;

		/**
		 * 指定消费者消费消息指针的移动位置
		 * 默认值为:{@link ConsumeFromWhere#CONSUME_FROM_LAST_OFFSET}
		 */
		private ConsumeFromWhere consumeFromWhere = ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET;

		/**
		 *
		 */
		private String consumeTimestamp = UtilAll.timeMillisToHumanString3(System.currentTimeMillis() - (1000 * 60 * 30));


		/**
		 * 分配消息的策略
		 */
		private AllocateMessageQueueStrategy allocateMessageQueueStrategy;

		private MessageListener messageListener;

		/**
		 * 对于缓慢拉动的情况  需要暂停拉动的时间
		 * 默认值 1000
		 */
		private long suspendCurrentQueueTimeMillis = 1000;

		/**
		 * 一条消息 消费超时的时间
		 * 默认值 15min
		 */
		private long consumeTimeout = 15;

	}

	/**
	 * 消费者线程池配置
	 */
	@Data
	public static class ConsumerThreadPool{

		/**
		 * 核心线程数量
		 * 默认值 20
		 */
		private int corePoolSize = 20;

		/**
		 * 默认值 30
		 */
		private int maxPoolSize = 30;

		/**
		 * 默认值 50
		 */
		private int queueCapacity = 50;
		/**
		 * 默认值 common_consumer_
		 */
		private String prefixThreadName = "common_consumer_";

		/**
		 * 针对重要的任务需要有感知
		 * {@link RejectedExecutionHandler}
		 */
		private RejectedExecutionHandler rejectedExecutionHandler = new ThreadPoolExecutor.AbortPolicy();

		/**
		 * 线程存活时间
		 * 默认值 60s
		 */
		private int keepLiveSeconds = 60;

		/**
		 * 默认值 true
		 */
		private boolean waitForTasksToCompleteWhenShutDown = true;

		private boolean allowCoreThreadTimeOut = true;

		/**
		 * 默认值 60s
		 */
		private int awaitTerminationForSeconds = 60;

	}

	@Data
	public static class ConsumerSubscribeInfo{
		private String topic;
		private String tagType;
		private String expression;
	}
}
