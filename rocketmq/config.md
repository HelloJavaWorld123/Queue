#### RocketMQ
- namesrv: 类似于 Eureka 和 zookeeper 的功能,管理Broker集群节点元数据以及节点状态
    集群形式的namesrv相互独立,每个namesrv分别保存broker集群的元数据信息.
    - 如果需要自定义配置参数,在conf 文件下 创建namesrv.properties文件  启动时 指定即可,指定的参数会覆盖默认的参数
    - mqadmin getNamesrvConfig -n ip:port
      - listenPort: name server 监听的端口
      - serverChannelMaxIdleTimeSeconds: name server节点channel最大空闲时间节点
      - serverCallbackExecutorThreads: 
      - serverAsyncSemaphoreValue:
      - serverSocketSndBufSize:
      - rocketmqHome:
      - clusterTest: 默认值false
      - serverSelectorThreads: 默认值为 3
      - useEpollNativeSelector: 默认值false
      - orderMessageEnable: 默认值 false
      - serverPooledByteBufAllocatorEnable: 默认值 true
      - kvConfigPath: 默认值 /root/namesrv/kvConfig.json
      - serverWorkerThreads: 默认值 8
      - serverSocketRcvBufSize: 默认值 65535
      - productEnvName: 默认值 center
      - serverOneWaySemaphoreValue: 256
      - configStorePath: 
    
- brokersrv:数据节点
    - broker.conf：配置Broker节点的信息
      - brokerClusterName：broker集群的名称,按照这个名称进行节点归类
      - brokerName:集群中节点的名称;集群中唯一
      - namesrvAddr: 当前节点连接的namesrv的地址(ip:port);多个地址分号隔开
      - listenPort: 当前节点监听的端口.默认是9876;当一台机器运行多个Broker的时候,需要自定义监听的地址
      - storePathRootDir: 当前节点存储数据的文件夹;指定了这个路径就在该路径下创建所有的文件包括但不限于(commitLog、ConsumerQueue、index等),与下面的文件路径互斥
      - storePathCommitLog:当前节点存储commitlog的路径
      - storePathConsumerQueue: 当前节点存储消费者队列的文件夹
      - storePathIndex: 消息索引的存储路径
      - fileReserverdTime: commitLog文件保留的最长时间;默认是 72小时;
      - deleteWhen: 当commitLog超过fileReserverdTime的时候,在几点(24小时制)执行删除动作.默认是 04(凌晨4点)
      - brokerRole: 当前节点的角色;
        - ASYNC_MASTER: 异步主节点(在主节点介绍到客户端的message时,立马返回,在异步传送给slave)
        - SYNC_MASTER：同步主节点(主节点接收到message，同时传送给slave,然后再返回给client)
        - SLAVE 在同一个集群中的从节点(名称必须是这个 否则不识别)
            - 在不能容忍消息丢失的情况下,使用AYNC_MASTER + SLAVE
            - 能够容忍部分消息丢失,但对HA有要求时,使用ASYNC+SLAVE
            - 否则可以只是用ASYNC_MASTER 
      - brokerId:0或者1 (大于0); 0 表示当前节点为 master;否则大于零的值为从节点
      - 启动时 使用 -c 指定需要使用的配置文件 
      - flushDiskType: 数据刷盘类型:
        - ASYNC_FLUSH
        - SYNC_FLUSH : 可以使用 ASYNC_MASTER + SLAVE 的方案代替  提高性能
        - ?????
      - autoCreateTopicEnable: 是否自动创建默认的topic
      - autoCreateSubscriptionGroup: 是否自动创建订阅组
    - 获取指定Broker的配置信息：mqadmin getBrokerConfig -b ip:port -n ip:port(namesrv的ip:port)
      - brokerTopicEnable: 默认值 true; 是否将broker的名称作为Topic的名称自动生成
      - clusterTopicEnale: 默认值 true；是否将Cluster的name作为topic 自动生成
      - slaveReadEnable: 默认值false;
- VipChannel：监听的端口 相差为 -2
- clientConfig
  - namesrvAddr : name Server Address
  - instanceName: 配置MqClientId
  - unitName:  MqClientId = ip@instanceName@unitName
  - Producer:
    - DefaultProducer
    - TransactionalProducer
  - Consumer:
    - DefaultMQPushConsumer
      - MessageModel (比如 总共有15条message,3个consumer集群)
        - Clustering: 消费者集群一共消费15条消息
        - Broadcasting: 每一个消费者都消费15条,总共消费45条消息
      - consumerFromWhere:
        - CONSUMER_FROM_LAST_OFFSET
        - CONSUMER_FROM_FIRST_OFFSET
        - CONSUMER_FROM_TIMESTAMP
- AllocateMessageQueueStrategy (消息分发策略)
- RemotingCommand：发送和接收的消息类
  - 序列化类型：JSON 和 ROCKETMQ (org.apache.rocketmq.remoting.protocol.SerializeType),或者通过**rocketmq.serialize.type**进行指定

#### Netty
- RemotingServer
  - serverOnewaySemaphoreValue: 利用Semaphore控制oneWay发送消息的并发量,默认值 256(公平锁)
  - serverAsyncSemaphoreValue：利用Semaphore控制发送异步消息的并发量;默认值64(公平锁)

- RemotingClient
  - org.apache.rocketmq.remoting.netty.NettyRemotingClient 客户端启动BootStrap和通过channel发送消息的逻辑
  - ChannelFuture的所有IO操作都是异步的。可以通过ChannelFutureListener监听Channel的状态。唯有wait方法是阻塞同步的,线程会阻塞直到IO完成.
  - 切记不要在ChannelHandler中调用wait()方法,ChannelHandler中的IO线程操作，如果同时调用wait()方法,ChannelHandler的IO线程将会阻塞

#### Q&A
- 消息的有序性?
- 消息的优先级?
- 消息过滤实现?
  - Notify实现
  - MessageTag、Message Header Body
  - CORBA Notification 过滤
  - Consumer 端过滤
- Persistence(消息持久化)
  - 持久化到数据库
  - 持久化到KV库
  - 文件记录形式的持久化
  - 内存数据持久化镜像
- 消息的刷盘方式
  - 同步
  - 异步
- 同步双写(version3.0以及之后)
- 分布式事务
- 延迟消息
- 消息重试

#### exception info
- org.apache.rocketmq.client.exception.MQClientException: No route info of this topic: TEST_CUSTOMER_TOPIC_TAG_ONE
  - autoCreateTopicEnable = false; 则要手动创建发送的topic,topic不存在时不会自动帮忙创建
  - 官方建议 线下启动 线上关闭
- org.apache.rocketmq.client.exception.MQClientException: allocateMessageQueueStrategy is null
  - 消费端必须配置该策略