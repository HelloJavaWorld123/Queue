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
      2. BackOffPolicy + SleepingBackOffPolicy -- 重试时间间隔策略
        2.1. StatelessBackOffPolicy -- 基类
        2.2. ExponentialBackOffPolicy -- 固定倍数向上递增
            2.2.1. ExponentialRandomBackOffPolicy -- 倍数乘以一个随机小数的形式,进行递增
        2.3. FixedBackOffPolicy -- 固定的频率进行无限重试
        2.4. UniformRandomBackOffPolicy -- 以maxBackOffPeriod 和 minBackOffPeriod的差,作为种子计算一个随机数x,x+minBackOffPeriod 最为下一次的时间间隔
        2.5. NoBackOffPolicy -- 默认的
- AsyncRabbitTemplate
        
      1.1
- BatchingRabbitTemplate
- AbstractRabbitListenerContainerFactory(e.g:org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory)

      
      2. concurrentConsumers: 创建的最小消费者数量，默认值为1;
      3. maxConcurrentConsumers: 除了{concurrentConsumers},最多多少个消费者(在当前消费者无法消费时，会自动的启动多余的消费者)
      4. startConsumerMinInterval: 启动其他消费者的最大时间间隔
      5. stopConsumerMinInterval: 停止其他的消费者最小的时间间隔
      6. consecutiveActiveTrigger:
      7. consecutiveIdleTrigger:
      8. batchSize: 批量处理的消息数量。会以当前数量循环获取消息数量，进行批处理
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
      17.ReceiveTimeOut: 接收消息超时的时间
      18.IdleEventInterval: 容器达到指定的空闲时间间隔，发出ListerContainerIdleEvent事件
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
    
    SimpleMessageListenerContainer
    
    DirectMessageListenerContainer


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
    11.DefaultRequeueRejected: 在消息被监听器拒绝时是否重新入队,默认值:true;如果设置为False,而且在当前队列设置了x-dead-letter-exchange以及x-dead-letter-routing-key
       消息就会被投入到DLQ中
    12.prefetchCount:consumer在一个拉取消息的请求中，Broker发送多少条消息给Consumer;影响消费者的吞吐量
- MessageBuilder(MessageProperties、Message)
    - 消息体的构建,包括Content-type,是否持久化,x-delay延迟消息,消息的优先级,头部参数等属性
- RabbitAdmin

      在启动时,声明所有的Queue、Exchange、Binding;或者如果存在auto_delete的Queue,Exchange,再使用时也会使用该RabbitAdmin重新创建缺失的Queue，Exchange
      如果autoDeclare设置为True,则必须提供该RabbitAdmin对象
      
      1.Exchange
        1.1. DirectExchange -- 根据Rounting Key 全匹配的形式发送消息
        1.2. FanoutExchange -- 广播的形式发送消息,忽略Routing Key
        1.3. TopicExchange -- 根据Routing Key 模糊匹配
        1.4. HeadersExchange --  根据header的参数进行全匹配或者部分匹配
        1.5. CustomerExchange -- 自定义交换机
      2.Binding:
      3.Queue:
        3.1. name -- Queue 的名称
        3.2. durable -- Queue是否持久化.(在Server关机或者意外情况下,Queue会被持久化)
        3.3. exclusive -- 队列的性质.是否只能被声明Queue的Connection使用;
        3.4. autoDelete -- 当队列很长时间不再使用时,Server是否自动删除. 时间是多长???
        3.5. actualName -- 如果name不为空,则使用name的值.否则使用:spring.gen-UUID_awaiting_declaration.(参考:org.springframework.amqp.core.Base64UrlNamingStrategy.generateName)
- @RabbitListener
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
- Channel:
- BlockingQueueConsumer:
    1. 是否动态增加Consumer的条件(org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer.AsyncMessageProcessingConsumer.mainLoop 自循环执行)
        1.1 如果receivedOk为True
            1.1 consecutiveMessages++ 大于 consecutiveActiveTrigger(默认值 10)
            1.2 lastConsumerStarted + StartConsumerMinInterval < now()
            以上条件满足 调用 TaskExecutor 增加 一个Consumer 执行任务
        1.2 如果receivedOk为False (减少consumer)
            1.1 consecutiveIdles++ 大于 consecutiveIdleTrigger
            1.2 lastConsumerStopped + StopConsumerMinInterval < now
            以上条件满足 将当前的consumer 从 consumers 移除 并更新 lastConsumerStopped为当前时间
- AmqpAppender:
   
   1.结合logback-spring.xml直接将配置的layout形式的日志发送到指定的Exchange中
   ```
  <appender name="AMQP" class="org.springframework.amqp.rabbit.logback.AmqpAppender">
          <layout>
              <pattern><![CDATA[ %d{yyyy-MM-dd HH:mm:ss} %p %t [%c] - <%m>%n ]]></pattern>
          </layout>
          <host>127.0.0.1</host>
          <port>5672</port>
          <username>all_virtual_host_admin</username>
          <password>root</password>
          <virtualHost>virtual_host_test</virtualHost>
          
          <exchangeType>direct</exchangeType>
          <exchangeName>log</exchangeName>
          <contentType>application/json</contentType>
          <autoDelete>false</autoDelete>
          <declareExchange>true</declareExchange>
          <!--是否持久化-->
          <deliveryMode>PERSISTENT</deliveryMode>
          <!--默认值 30次-->
          <maxSenderRetries>3</maxSenderRetries>
          <!-- <abbreviation>36</abbreviation --> <!-- no category abbreviation by default -->
          <applicationId>AmqpAppenderTest</applicationId>
          <routingKeyPattern>producer-log</routingKeyPattern>
          <generateId>true</generateId>
          <charset>UTF-8</charset>
          <durable>true</durable>
      </appender>
  ```
- AbstractRoutingConnectionFactory
   1.SimpleRoutingConnectionFactory 根据virtual_host的不同,分别配置不同ConnectionFactory


# 消息的确认机制

   1.             ConfirmCallBack
        Producer  ---------------> Rabbit Cluster Broker -----> Exchange ----> Queue ----> Consumer
                  <--------------                        <-----          <----       
                   ReturnCallBack 
    
   2. ConfirmCallBack: 是Cluster Broker收到消息后给Producer的确认.
      2.0 一个RabbitTemplate只能有一个ConfigCallBack,或者在Config中全局配置或者在条用RabbitTemplate中单独配置
      2.1 CachingConnectionFactory中setConfirmCallBack()过时,由ConfirmType取缔
      2.2 ConfirmType:
          2.2.1: None : 默认的确认机制
          2.2.2: Correlated:发送者发送消息时对Message设置唯一的标识id,当Broker确认时将Correlated重新发送给生产者
          2.2.3: Simple: 
          
   3. ReturnCallBack:
      3.0 消息由Cluster Broker 投递到 Exchange,然后由Exchange是否成功投递到Queue时,返回的相关信息
      3.1 Mandatory: 设置为True;
      
# 集群
    通过主机名称链接各个节点的；先修改host文件；
    1.通过复制Erlang.Cookie实现
    2.在各个节点上，都会同步Exchange、Queue、Bindings、Vhost的元数据信息；
    3.Queue的数据信息只会保存在创建该Queue的节点上,其他的几点在同步Queue的原信息时，还会保存该Queue的owner node的指针；缺点是：当某个节点down掉时，当前节点上的数据会丢失；
    4.磁盘节点和内存节点(一个磁盘节点，其他的都为内存节点)
    5.镜像队列(version 2.6之后)，为了增加队列数据的高可用性以及容错性
        会将生产者的数据分别发送到主队列和镜像队列中，确认机制是什么？？？？
    6.高可用的队列在重新选举主节点时，会从‘最老’的节点中选举，
      对于刚加入的节点不会被选择主节点，依赖因为后加入的从节点不会同步历史数据，如果刚加入进来就被选举为主节点那么数据将会丢失
    7. HA mode (集群模式)(ha-mode=)
        7.1 all : 所有的节点
        7.2 exactly: 指定节点的数量
            ha-params=: 设置数量个数
        7.3 nodes: 按照节点的名称
            ha-params=:节点的名称,多个节点用逗号隔开
    8.当集群中重新加入节点是,数据同步的方式:(新加入的节点只会同步最新的message,旧消息需要设置同步方式)
        ha-sync-mode=:
        8.1 manual ：手动同步
        8.2 automatic : 自动同步
    9.ha-promote-on-failure
        9.1 when-sync
        9.2 always
    10.ha-promote-on-shutdown
        10.1 when-sync
        10.2 always
    11.集群中两个节点之间心跳的间隔时间是60s,当两个节点互相接收不到心跳时,就会出现网络分区,在默认的网络分区处理策略配置下会导致脑裂（split-Brain）问题出现
# QA

    1.启动时不会检查配置的有效性？
       1.1 实现[com.rabbitmq.client.ExceptionHandler]接口.其实每次启动都会检查,
            只是在日志没有被打印出来.实现上面的接口,打印日志就会发现异常信息.
       1.2 增加*spring-boot-starter-actuator* 会使用RabbitTemplate进行链接测试
    2.死信队列(Dead Latter Queue)
       2.1 在Declaring Queues时,通过指定x-dead-letter-exchange 参数指定DLQ的Exchange
                ```
                return QueueBuilder
                        .durable(RabbitMqEnum.QueueEnum.TEST_X_DEAD_LETTER_EXCHANGE_ARG.name())
                        .deadLetterExchange(RabbitMqEnum.ExchangeEnum.TEST_DEAD_LETTER_EXCHANGE.name())
                        .deadLetterRoutingKey(RabbitMqEnum.RoutingKey.TEST_DEAD_LETTER_EXCHANGE_KEY.name())
                        .build();  
                 或者:
                Map<String, Object> arguments = new HashMap<>(2);
                    arguments.put("x-dead-letter-exchange",RabbitMqEnum.ExchangeEnum.TEST_DEAD_LETTER_EXCHANGE.name());
                    arguments.put("x-dead-letter-routing-key",RabbitMqEnum.RoutingKey.TEST_DEAD_LETTER_EXCHANGE_KEY.name());
                    return QueueBuilder
                            .durable(RabbitMqEnum.QueueEnum.TEST_X_DEAD_LETTER_EXCHANGE_ARG.name())
                            .withArguments(arguments)
                            .build();
                ```
            备注: 1.在声明队列时,DLQ的Exchange可以不必声明,但在使用时,该Exchange 必须存在;
                  2.如果DLQ的Exchang的rounting key没有声明,那么使用向死信队列推送的消息的Routing Key;
          
    3.延迟队列
        3.1 方案一：在RabbitMq3.6.0中有延迟插件,在发送消息时：通过MessageProperties设置message的头部：Set the x-delay header
    4.Transactional
      4.1 XA Transaction:
        4.1.1 TM(Transaction Management)+RM(Resource Management)
        4.1.1 两阶段提交协议(2PC)
            4.1.1.1 MySql在InnoDB的数据库中支持XA协议,Mysql支持外部XA和内部XA
                4.1.1.1.1 Mysql的外部XA是在多个Mysql实列之间协调,应用层作为协调者,工具包括:网易的DDB 淘宝的TDDL
                4.1.1.1.2 Mysql的外部XA是在单个Mysql实列夸多引擎的事务,Binlog作为协调者;这时没有协调者
                    4.1.1.1.1 查看Mysql是否支持XA事务:show variables like '%xa%' --> 如果 innodb_support_xa的值为on 表示支持xa事务
      4.2 TCC(Trancsactional Confirm Cancle)
      4.2 最终一致性方案
    5.序列化和反序列化
    6.对已经持久化的队列进行属性的新增或者删除,再启动时会报错错误.因为已经持久化的队列不能再更新属性,必须先删除才能再更新
    7.怎么保证消息的顺序
    8.如果在mq服务器异步通知过程中，由于网络原因或者mq正好准备回调就挂了，导致发布者没有收到确认发送的消息怎么办？
    9.mq 迟迟未收到consumer的ack怎么处理？
    10.集群中节点之间数据同步的模式：
        10.1.Async
    11.RabbitMQ集群的分区模式有哪几种？各自的特点是什么？(脑裂问题)
        脑裂问题：在一个HA的集群中，当其中一个节点与集群断开链接时,或者心跳链接的两个节点由于网络或者中间设备断开了心跳传输,而相互又任务对方节点出现了问题。
        此时，一个完整的HA系统，分成了多个单独的节点，对于无状态链接的HA集群,单独的节点继续服务不会出现数据不一致问题，但对于有状态的HA集群，节点会出现资源抢夺以及
        各节点数据不一致的问题,MySQL的HA集群就是有状态的集群；
        RabbitMQ在产生脑裂问题时使用的是 --- 分区
        分区有一下三种模式:（cluster_partition_handling=）
       10.1 ignore:默认方式,当其中两个节点心跳无法互连时,RabbitMQ会出现分区,而这种模式下没有自动选择信任分区，导致数据还将正常发送到所有的节点,会导致数据不一致以及资源的争夺,以及丢数据问题
       10.2 pause_minority:(暂停少数节点模式,节点个数大于2,否则心跳断链后 两个都不对外服务)
            选择了CAP中分区容错性P,放弃了可用性A
            RabbitMQ在这种模式下选择节点个数大于1/2节点的分区为优胜分区，优胜分区为对外提供服务节点。失败分区的web端口15672,amqp协议端口5672，25672通信端口将会被关闭.
            erlang 6439端口保持开放，当心跳恢复时继续将失败分区的节点加入到集群中；
       10.3 pause_if_all_down:(需要配置其他的参数:恢复模式以及节点)
            cluster_partition_handling = pause_if_all_down
            
            ## Recovery strategy. Can be either 'autoheal' or 'ignore'
            cluster_partition_handling.pause_if_all_down.recover = ignore
            
            ## Node names to check
            cluster_partition_handling.pause_if_all_down.nodes.1 = rabbit@myhost1
            cluster_partition_handling.pause_if_all_down.nodes.2 = rabbit@myhost2
       10.4 autoheal(自动治愈):
            根据 链接数量的多少  node节点的数量 随机 依次决定 自动决定一个可用的分区
