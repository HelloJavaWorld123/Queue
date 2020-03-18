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
    二.Exchange:
    三.Queue:
    四.Bingding:
# Spring
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
- RabbitAdmin

      1.1
# QA

    1. 启动时不会检查配置的有效性？
        1.1 增加*spring-boot-starter-actuator* 会使用RabbitTemplate进行链接测试