# Queue
--
- [RabbitMQ Config Info](https://github.com/HelloJavaWorld123/Queue/tree/master/rabbitmq/config.md)
- [RocketMQ Config Info](https://github.com/HelloJavaWorld123/Queue/tree/master/rocketmq/config.md)
- [Kafka Config Info](https://github.com/HelloJavaWorld123/Queue/tree/master/kafka/config.md)



# Extend
    -贫血领域对象(仅仅用作数据的载体,而没有实际的行为和动作的领域对象)
           |
           |
          \|/
    -贫血症引发的失忆症 
           |
           |
          \|/
    - 领域模型开发(DDD)
      
      
- [领域模型开发(DDD)](https://mp.weixin.qq.com/s?__biz=MjM5NjQ5MTI5OA==&mid=2651747236&idx=1&sn=baf67052ec1961c3c6de1af26fba9b22&chksm=bd12aae98a6523ff90b3461d00fee548554fdeb2112b541de87d0c59dea45bc60d2f5211d6a6&scene=21#wechat_redirect)
  - 1.设计DDD的步骤:
    - 1.1 根据需求划分出初步的领域和界限上下文,以及上下文之间的关系
    - 1.2 进一步分析每个界限上下文内部,识别出那些是实体,那些是值对象
    - 1.3 对实体,值对象进行关联和聚合,划分出聚合的范畴和聚合根
    - 1.4 为聚合根设计仓储,并思考实体和值对象的设计方式
    - 1.5 在工程中实践领域模型,并验证领域模型的合理性,倒推模型中不足的地方并重构.