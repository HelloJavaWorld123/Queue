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
        2. CacheMode:
            2.1 Channel
                2.1.1 size 缓存中Channel的数量
                2.1.2 checkTimeOut：如果为零，则Channel不会被复用,每次获取会创建新的Channel;大于零时会在CheckTimeOut时间内复用缓存中的Channel
            2.2 Connection
                2.2.1 model: 设置Cache的方式;默认方式为Channel;
                2.2.2 size: 缓存中链接的数量;当且仅当CacheModel为Connection时，该值的大小起作用；如果CacheModel为Channel，则该值为1;
                    备注：如果CacheModel为Channel,并且配置了Connection的Size,启动报错:[java.lang.IllegalArgumentException: When the cache mode is 'CHANNEL', the connection cache size cannot be configured.]
                
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
- AbstractRabbitListenerContainerFactory(e.g:org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory)

      
      2. concurrentConsumers: 默认值为1
      3. maxConcurrentConsumers: 除了{concurrentConsumers},最多多少个消费者(在当前消费者无法消费时，会自动的启动多余的消费者)
      4. startConsumerMinInterval: 启动其他消费者的最大时间间隔
      5. stopConsumerMinInterval: 停止其他的消费者最小的时间间隔
      6. consecutiveActiveTrigger:
      7. consecutiveIdleTrigger:
      8. batch Size: 批量处理的数量。会以当前数量循环获取消息数量，进行批处理
      9. consumerBatchEnabled: 批量消费消息
      10.DebatchingEnable:是否支持批量处理的消息分批处理(与Batch Size 和 ConsumerBatchEnable 一起使用)
      11.batchingStrategy:消息分批处理的策略 
      10.defaultRequeueRejected:当消费者无法消费时(或者Listener抛出异常或者ContentType无法解析)，
                                是否将消息重新入队；默认值为：true;并抛出{AmqpRejectAndDontRequeueException}
      11.ErrorHandler: 处理在异步执行Task任务时，抛出的异常。以及在启动时是否能够连接到服务器的异常.默认使用：ConditionalRejectingErrorHandler
        11.1 方案一:实现ErrorHandler接口，自定义处理相关异常的逻辑
        11.2 方案二：默认使用的ConditionRejectionErrorHandler,通过Exception的类型处理不同的逻辑,默认的策略是:[org.springframework.amqp.rabbit.listener.ConditionalRejectingErrorHandler.DefaultExceptionStrategy]
        其中 isFatal()方法决定当前的异常是否是致命的。可以继承(DefaultExceptionStrategy)决定什么异常属于属于致命的异常，然后再入队。
        11.3 出现异常后，根据异常的种类判断消息是否入死信队列
            11.3.1 异常是:AmqpRejectAndDontRequeueException,并且不是致命的，则将消息加入死信队列
      12.AutoStartUp: 当前监听器容器是否自动启动;默认值为True.如果设置成False,则手动调用[org.springframework.amqp.rabbit.listener.AbstractMessageListenerContainer.start]方法启动监听器容器
      13.AcknowledgeMode:设置消费者的确认模式;如果要支持事务则必须为(AUTO 或者 MANUAL)模式
      14.MessageConverter:自定义实现从Message到Java Object或者Java Object 到Message 的转换.包括如下几种形式:
        14.1 SimpleMessageConverter:默认的消息转换器
        14.2 Jackson2JsonMessageConverter:使用的比较多，支持灵活的从Message到Java Object的转化;
        14.3 Jackson2XmlMessageConverter:
        14.4 SmartMessageConverter
        14.5 ContentTypeDelegatingMessageConverter:支持根据具体的ContentType,对应具体的MessageConverter
            14.5.1 e.g: application/json --> Jackson2JsonMessageConverter;application/xml-->Jackson2XmlMessageConverter;
        14.6 MarshallingMessageConverter
        14.7 MessagingMessageConverter
        14.8 SerializerMessageConverter
        14.9 WhiteListDeserializingMessageConverter
      15.FailedDeclarationRetryInterval:
      16.RetryTemplate: 设置重试Template的句柄
      17.ReceiveTimeOut:
      18.IdleEventInterval:
      19.TaskExcutor: 设置执行任务的线程池;
                      默认为:SimpleAsyncTaskExecutor,缺点是:线程不复用,每个Task重新创建一个线程;
                      在创建自己的Task线程池时要注意线程池中线程的数量要满足任务的最大数量
      20.ChannelTransacted: 标志是否支持事务。默认值为:false 
      21.TransactionManager: 当ChannelTransacted设置为true时,transactionManager扩展自定义的事务管理器
      22.RecoveryInterval:自动恢复链接的时间间隔(FixedBackOff),与RecoveryBackOff属性互斥.
      23.RecoveryBackOff:自动恢复链接使用的策略,与RecoveryInterval互斥,使用指定的策略
        23.1.FixedBackOff：固定的时间间隔检查恢复链接
        23.2.ExponentialBackOff：按照倍数递增的方式恢复链接：
            从{DEFAULT_INITAL_INTERVAL}开始,每次递增{DEFAULT_INITAL_INTERVAL+DEFAULT_INITAL_INTERVAL*MULITIPLIER},最大为{DEFAULT_MAX_INTERVAL}
            DEFAULT_INITAL_INTERVAL:2000L
            MULITIPLIER = 0.5
            DEFAULT_MAX_INTERVAL:30000L
      
      
- AbstractMessageListenerContainer


    1.alwaysRequeueWithTxManagerRollback: 事务回滚时,消息是否重新入队
    2.transactionAttribute      
    3.shutDownTimeOut:容器关闭时,等待消息处理完毕的超时时间,默认为300000ms
    4.forceCloseChannel:在shutDownTimeOut时间内,如果有消息再处理也就是工作(Workers)状态,是否强制关闭Channel,V2.0以后默认为True.
    5.possibleAuthenticationFailureFatal:授权失败是否是致命的。如果是则在启动时,容器上下文不能被初始化.如果不是则会进入重试模式
    6.missingQueuesFatal: 当Queue不能使用时,是否停止当前的容器上下文.
    7.autoDeclare: 依赖RabbitAdmin对象.启动时或者队列自动删除后是否重新自动的创建.
    8.declarationRetries:创建失败时 重试的次数.依赖RabbitAdmin对象
    9.RetryDeclarationInterval:重试创建的时间间隔.依赖RabbitAdmin对象
    10.FailedDeclarationRetryInterval:创建失败时重试的时间间隔.依赖RabbitAdmin对象
- MessageBuilder
    - . 消息体的构建
- RabbitAdmin

      在启动时,声明所有的Queue、Exchange、Binding;或者如果存在auto_delete的Queue,Exchange,再使用时也会使用该RabbitAdmin重新创建缺失的Queue，Exchange
      如果autoDeclare设置为True,则必须提供该RabbitAdmin对象
      
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
    1.
- @RabbitListeners
    1. 支持 @RabbitListener 的 @Repeatable,
- @RabbitHandler
    1.根据方法的签名的不同，接收不同的消息
    2.isDefault() 表示当前的方法是否是默认的接收消息的方法
- @PayLoad
    1. 在接收消息的方法签名上使用,指定消息的Body信息;按照Body的类型处理消息
- @Header
    1. 在接收消息的方法签名上使用，指定能够处理的消息的头部相关的信息
- @SendTo
    1. 消息处理后的回复信息.(比如:标注在类上，统一回复)
    2. 支持SpEl表达式(eg: @SendTo("!{'some.reply.queue.with.' + result.queueName}"))
- Channel: [Prefetch Count](https://www.rabbitmq.com/blog/2012/05/11/some-queuing-theory-throughput-latency-and-bandwidth/): 
            客户端从Queue中获取消息的预期值。Spring Boot的默认值为250
            设置Channel或者Queue上堆积消息的数量.false:指定的Queue上未被处理的消息最大数量.
            true:Channel上未被处理的消息的最大数量.影响到客户端的吞吐量(Tell the broker how many messages to send to each consumer in a single request)
- BlockingQueueConsumer:
- AmqpAppender:日志发送相关配置
- AbstractRoutingConnectionFactory
   1. SimpleRoutingConnectionFactory


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
    2.死信队列(Dead Latter Queue)
    3.延迟队列
        3.1 方案一：在RabbitMq3.6.0中有延迟插件,在发送消息时：通过MessageProperties设置message的头部：Set the x-delay header
    4.Transactional
    5.序列化和反序列化