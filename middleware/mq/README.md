# MQ

Message Queue 是目前微服务架构中应用非常广泛的中间件，主要用来做异步处理、服务解耦、流量削峰等。一个高性能、低延迟、高可用、高可靠的消息队列在互联网业务中是非常必要的。而在多数互联网公司中使用的 MQ 中间件产品有：

* Kafka
  * [nakadi](https://github.com/zalando/nakadi) - A distributed event bus that implements a RESTful API abstraction on top of Kafka-like queues
  * [allegro/hermes](https://github.com/allegro/hermes) - Fast and reliable message broker built on top of Kafka.
* RocketMQ
* RabbitMQ
* Pulsar
* pmq
* qmq
* NSQ
* Redis, 其实 Redis 并不能算一个 MQ 产品，它只是提供了一些 Queue 的能力，Pub/Sub 的能力

### MQ 的几个关键问题

1. 项目中使用 MQ 解决什么问题？为什么要选择这个 MQ 产品？
2. 你了解的这个 MQ 产品的技术架构、部署架构是怎样的？它的设计做了哪些权衡？
3. MQ 怎么解决消息丢失问题？
4. MQ 怎么解决消息重复问题？
5. MQ 怎么解决消息积压问题？
6. 有没有使用过 MQ 的事务消息，怎么实现的？为什么使用？
7. MQ 的顺序消费如何实现？
8. 在生产环境中有什么好的实践来提升 MQ 的可靠性吗？在实际场景中是如何权衡消息可靠、低延迟等需要的？
9. 说一下你们公司的 MQ 的处理消息数，比如日处理消息多少，多少台机器，怎么部署的，有没有做过一些优化配置等等（实际使用运维压测经验）
10. MQ 实现的一些技术细节
    1. 消息传输协议、序列化与反序列化、内存管理、高性能网络 IO、异步设计、数据压缩
    2. 消息存储：Zero Copy、顺序写、WAL
    3. 消息索引：索引结构、高性能索引
    4. 并发控制：高性能锁设计、CAS、减少数据共享
    5. 分布式：Broker 的高可用设计、服务发现和注册、服务协调、生产和消费负载均衡、broker 间消息同步（数据一致性）



### MQ 相关文章汇总

[MQ 消息中间件分析](https://mp.weixin.qq.com/s/lqFGnIUtqTFZ_GHp46z48Q)

* 为什么使用 MQ？
* MQ 有什么优缺点？
* 几种 MQ 产品的对比，以及适用场景



