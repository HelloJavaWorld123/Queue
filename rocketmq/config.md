#### RocketMQ
- namesrv: 类似于 Eureka 和 zookeeper 的功能,管理Broker集群节点元数据以及节点状态
    集群形式的namesrv相互独立,每个namesrv分别保存broker集群的元数据信息.
- brokersrv:数据节点
    - broker.conf：配置Broker节点的信息
      - brokerClusterName：broker集群的名称,按照这个名称进行节点归类
      - brokerName:集群中节点的名称;集群中唯一
      - namesrvAddr: 当前节点连接的namesrv的地址(ip:port);多个地址分号隔开
      - listenPort: 当前节点监听的端口.默认是9876;当一台机器运行多个Broker的时候,需要自定义监听的地址
      - storePathRootDir: 当前节点存储数据的文件夹;
      - storePathCommitLog:当前节点存储commitlog的路径
      - storePathConsumerQueue: 当前节点存储消费者队列的文件夹
      - fileReserverdTime: commitLog文件保留的最长时间;默认是 72小时;
      - deleteWhen: 当commitLog超过fileReserverdTime的时候,在几点(24小时制)执行删除动作.默认是 04(凌晨4点)
      - brokerRole: 当前节点的角色;
- VipChannel：监听的端口 相差为 -2

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