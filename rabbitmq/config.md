# RabbitMQ
    一.ConnectionFactory
        1. userName -- 默认值: guest
        2. password -- 默认值: guest
        3. port -- 默认值: -1 ;
            3.1 如果ssl == true,则使用 DEFAULT_AMQP_OVER_SSL_PORT = 5671
            3.2 如果ssl == false,则使用 AMQP.PROTOCOL.PORT = 5672
        4. host -- 默认值: localhost
        5. virtualHost -- 默认值: /
        6. RequestHeartBeat -- 心跳检查间隔，默认间隔为60s
        6. connectionTimeOut -- Tcp Connection TimeOut,默认值：60000(ms)
        6. handshakeTimeOut -- Tcp 握手超时时间,默认值: 10000(ms)
        7. shutDownTimeOut -- Tcp 断开链接的时间,默认值: 10000(ms);0值表示一直等待
        8. SaslConfig
        9. SocketFactory
        10. sharedExecutor -- ExecutorService
        11. threadFactory -- Java的DefaultThreadFactory.创建线程使用
        12. shutDownExecutor -- ExecutorService
        13. heartbeatExecutor -- ScheduleExecutorService
        14. socketConf -- socketConfigurator
        15. exceptionHandler
        16. credentialsProvider -- userName And Password
        17. automaticRecovery -- 链接自动恢复 默认值： true
        18. topologyRecovery  -- ?? 默认值: true
        19. networkRecoveryInterval -- 网络恢复间隔;默认值是:5000(ms)
        20. recoveryDelayHandler -- 设置 automaticRecovery 每次失败后的延迟时间
            20.1. com.rabbitmq.client.RecoveryDelayHandler.ExponentialBackoffDelayHandler#List<Long>保存延迟的时长:2000, 3000, 5000, 8000, 13000, 21000, 34000
        21. metricsCollectors -- 
        22. nio -- 默认值: false;
        23. frameHandlerFactory -- 
        24. nioParams -- NIO 参数的详细配置
            24.1. readByteBufferSize
            24.2. writeByteBufferSize
            24.3. nbIoThreads -- max Number of IO threads,默认值 1
            24.4. writeEnqueuingTimeoutInMs -- 默认值 10000ms
            24.5. writeQueueCapacity -- 写出数据队列的大小;默认值 10000
            24.6. NioExecutor -- ExecutorService
            24.7. threadFactory -- ThreadFactory
            24.8. socketChannelConfigurator
            24.9. sslEngineConfigurator
            25.0. connectionShutDownExecutor -- ExecutorService
            25.1. byteBufferFactory -- ByteBufferFactory; create java.io.byteBuffer
            25.2. writeQueueFactory -- com.rabbitmq.client.impl.nio.BlockingQueueNioQueue.(使用:ArrayBlockingQueue)
        25. sslContextFactory -- 
        26. channelRpcTimeOut -- 默认值:10min
        27. 
    二.ExchangeInfo:
    三.QueueInfo:
    四.BingdingInfo:
    五.NodeInfo:
    六.UserInfo:
    七.VhostInfo:
    八.ChannelInfo:
    九.ConnectionInfo:
# Spring RabbitMQ
   - [中文文档](https://s0docs0spring0io.icopy.site/spring-amqp/docs/current/reference/html/) 
   
    CacheConnectionFactory
        1. org.springframework.amqp.rabbit.connection.CachingConnectionFactory.newRabbitConnectionFactory -- 创建了Rabbit ConnectionFactory,并将AutomicRecovery设置为False
- RabbitTemplate

      1.1
- RetryTemplate

      1. RetryPolicy -- 重试策略
      2. BackOffPolicy -- ???
        2.1. SleepingBackOffPolicy --
        2.2. StatelessBackOffPolicy --
        2.3. ExponentialBackOffPolicy -- 
            2.3.1. ExponentialRandomBackOffPolicy --
        2.4. FixedBackOffPolicy -- 
        2.5. UniformRandomBackOffPolicy --
        2.6. NoBackOffPolicy -- 默认的
- AsyncRabbitTemplate
        
      1.1
- BatchingRabbitTemplate
- AbstractRabbitListenerContainerFactory

      1. batch Size:
      n. missingQueuesFatal
      2. concurrentConsumers: 默认值为1
      3. maxConcurrentConsumers: 
      4. startConsumerMinInterval:
      5. stopConsumerMinInterval:
      6. consecutiveActiveTrigger:
      7. consecutiveIdleTrigger:
      8. consumerBatchEnabled:
      
      
      
- RabbitAdmin

      1.Exchange
        1.1. DirectExchange -- 
        1.2. FanoutExchange --
        1.3. TopicExchange --
        1.4. HeadersExchange -- 
        1.5. CustomerExchange -- 
      2.Binding:
      3.Queue:
        3.1. name -- Queue 的名称
        3.2. durable -- Queue是否持久化.(在Server关机或者意外情况下,Queue会被持久化)
        3.3. exclusive -- 队列的性质.是否只能被声明Queue的Connection使用;
        3.4. autoDelete -- 当队列很长时间不再使用时,Server是否自动删除. 时间是多长???
        3.5. actualName -- 如果name不为空,则使用name的值.否则使用:spring.gen-UUID_awaiting_declaration.(参考:org.springframework.amqp.core.Base64UrlNamingStrategy.generateName)
- @RabbitListener
- @RabbitListeners
- @RabbitHandler
- @PayLoad
- @Header
- Channel: Prefetch Count: 设置Channel或者Queue上堆积消息的数量.false:指定的Queue上未被处理的消息最大数量.
           true:Channel上未被处理的消息的最大数量.影响到客户端的吞吐量(Tell the broker how many messages to send to each consumer in a single request)
- BlockingQueueConsumer:


# 消息的确认机制
   1.  *           ConfirmCallBack
       * Producer  ---------------> Rabbit Cluster Broker -----> Exchange ----> Queue ----> Consumer
       *           <--------------                        <-----          <----       
       *            ReturnCallBack 
    
   2. ConfirmCallBack: 是Cluster Broker收到消息后给Producer的确认.
      2.0 一个RabbitTemplate只能有一个ConfigCallBack,或者在Config中全局配置或者在条用RabbitTemplate中单独配置
      2.1 CachingConnectionFactory中setConfirmCallBack()过时,由ConfirmType取缔
      2.2 ConfirmType:
          2.2.1: None : 默认的确认机制
          2.2.2: Correlated:
          2.2.3: Simple: 
   3. ReturnCallBack:
      3.0 消息由Cluster Broker 投递到 Exchange,然后由Exchange是否成功投递到Queue时,返回的相关信息
      3.1 Mandatory: 设置为True;
# QA

    1.启动时不会检查配置的有效性？
       1.1 实现[com.rabbitmq.client.ExceptionHandler]接口.其实每次启动都会检查,
            只是在日志没有被打印出来.实现上面的接口,打印日志就会发现异常信息.
       1.2 增加*spring-boot-starter-actuator* 会使用RabbitTemplate进行链接测试
    2.死信队列(延迟队列)
    3.Transactional