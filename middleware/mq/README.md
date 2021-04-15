# MQ

Message Queue \(简称 MQ\) 是目前微服务架构中应用非常广泛的中间件，主要用来做异步处理、服务解耦、流量削峰等。一个高性能、低延迟、高可用、高可靠的消息队列在互联网业务中是非常必要的。

而在多数互联网公司中使用的 MQ 中间件产品有：

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
* 适用于 IoT 的 [emqx](https://github.com/emqx/emqx) - [https://www.emqx.cn/](https://www.emqx.cn/)

我们需要明白一个道理，**不存在一个完美的东西可以解决所有问题，往往是在引入一个新的东西时解决了我们的痛点，但是同样会引入其他复杂度，而这个引入的复杂度是否能被接受要看具体的场景进行权衡**；这个道理同样适用于 MQ，所以 MQ 不是银弹。

那么 MQ 主要解决了什么问题呢？MQ 很好的解决了耦合性问题、数据分发问题、流量激增问题，比如服务间的耦合，通常情况下服务间使用 RPC 的方式直接调用，这会带来服务间的耦合性比较大，引入 MQ 后可以利用发布-订阅模型，分离了调用方和被调用方，也就是实现了生产者和消费者；再比如我们需要将一份数据（或者一个事件）发送到多个服务中（因为我们使用了微服务架构，做了服务拆分），这个时候可以利用 MQ 解决数据分发问题；再比如在一个高并发的系统中，MQ 有缓冲数据的能力，所以对流量也有控制的能力，也就是削峰，不至于超大流量压垮系统。

此外由于 MQ 的这些能力，MQ 也被用来解决分布式事务问题，来实现数据的最终一致性：

1. 比如事务性消息的实现，RocketMQ 就实现了事务性消息，将数据的发送分为两个阶段，第一阶段是 Prepare 阶段的 Half Message，然后是在本地事务提交或回滚后触发 Commit 阶段的提交或回滚；此外还有一个补偿机制，当 Half Message 消息在一定时间后没有收到 Commit 消息，则会反查业务系统，这样就保证了数据发送的原子性，而这种保证是最终一致性的；消息发送成功后，消息是否被消费者正确消费，依赖于消费者自己的实现
2. 比如应用在分布式事务中，sagas 的实现可以基于 MQ 的通道来解耦合，减少服务间的直接调用，增加吞吐量和实现异步

还有在大数据的应用场景中，MQ 作为一个管道连接了数据和流计算任务，作为数据的中间存储。

当然，MQ 也会带来一些问题

1. MQ 增加了系统复杂度，比如 MQ 由多个 Broker 组成，肯定要解决 Broker 的可用性问题，服务发现问题、性能问题（延迟和吞吐量的权衡）；再比如发送给 MQ 的消息怎么做到不丢失，这个问题很关键；再比如 MQ 可以缓冲消息，如果消息积压了要能快速解决
2. 使用了 MQ 会把原本直接调用的方式转为异步的模式，这无形中增加了延迟，因为消息要经过 MQ 的 Broker 才能到达消费方，消费方成功消费后才能把结果反馈出来
3. 在整个的消息处理的过程中，可能会带来数据的不一致，比如订单创建成功了，但是可能还没有生成发货的订单，但这种不一致我们是可以接受的

上面讨论了引入 MQ 带来的一些好处和一些问题，一般情况下，当我们的业务到达一定阶段的时候，这个时候大泥球式的单体架构无法满足业务发展，开发的复杂度也会日益增加，开发效率日益下降，势必会做业务服务拆分，分开治理和独立演化，此外由于数据量的增加，需要将数据分发到大数据平台做离线或者实时的分析，到达这个阶段时 MQ 就是一个非常重要基础组件，同样的我们也要承受引入 MQ 带来的问题，那么引入 MQ 会带来哪些具体的问题呢？

1. 如何选择 MQ 产品？自研还是开源方案？为什么要这么选择？
2. 使用 MQ 后如何应对和解决消息丢失问题？
3. 使用 MQ，如何解决消息重复问题？
4. 使用 MQ，如何解决消息积压问题？
5. 使用 MQ，如何实现分布式事务？什么是事务消息？适合用在什么场景下？
6. MQ 的顺序消息如何保证？局部有序 or 全局有序？
7. 针对 MQ 本身
   1. MQ 的技术架构、部署架构等？有哪些设计亮点，适合用在什么场景下？
   2. 如何实现高可用？多副本，日志复制，自动 failover
   3. 如何实现高性能？低延迟和高吞吐的权衡
   4. MQ 间的数据一致性
   5. MQ 服务注册和发现
   6. MQ 治理，消息监控（如堆积 \(lag\) 情况）、Broker 监控等
   7. 实现 MQ 时用到的一些底层技术
      1. 消息传输协议、序列化与反序列化、内存管理、高性能网络 IO、异步设计、数据压缩
      2. 消息存储：Zero Copy、顺序写、WAL
      3. 消息索引：索引结构、高性能索引
      4. 并发控制：高性能锁设计、CAS、减少数据共享
      5. 分布式：Broker 的高可用设计、服务发现和注册、服务协调、生产和消费负载均衡、broker 间消息同步（数据一致性）
8. MQ 生产实践
   1. 在生产环境中有什么好的实践来提升 MQ 的可靠性吗？在实际场景中是如何权衡消息可靠、低延迟等需要的？
   2. 说一下你们公司的 MQ 的处理消息数，比如日处理消息多少，多少台机器，怎么部署的，有没有做过一些优化配置等等（实际使用运维压测经验）
   3. MQ 调优
9. 如何设计一个 MQ？\(要先和需求方沟通 MQ 的基本需求，要使用的场景，充分了解了业务，然后再给出一些解决方案，可能有些场景需要低延迟高可靠，有些场景需要高吞吐可容忍数据在极端情况下的丢失等\)
10. ...

接下来重点去分析引入 MQ 需要关注的这些问题，该如何解决。 

### MQ 通用性问题

这类问题是所有 MQ 产品都需要面对的通用问题，所以更加注重解决问题的方法、思想和权衡等。

#### 如何选择 MQ

在需要用 MQ 时，我们面临诸多选择，第一想到的肯定是看一下目前已经存在的 MQ 产品中是否有满足当前需求而且也能为未来留有空间的产品；如果找不到，那只能借鉴已有的 MQ 产品的一些特性做自研，当然选择自研一定要慎重，需要团队有深厚的技术实力。横向对比一下常用的 MQ 产品：

<table>
  <thead>
    <tr>
      <th style="text-align:left">&#x7279;&#x6027;</th>
      <th style="text-align:left">RocketMQ</th>
      <th style="text-align:left">Kafka</th>
      <th style="text-align:left">Pulsar</th>
      <th style="text-align:left">RabbitMQ</th>
      <th style="text-align:left">NSQ</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td style="text-align:left">&#x5355;&#x673A;&#x541E;&#x5410;&#x91CF;</td>
      <td style="text-align:left"></td>
      <td style="text-align:left"></td>
      <td style="text-align:left"></td>
      <td style="text-align:left"></td>
      <td style="text-align:left"></td>
    </tr>
    <tr>
      <td style="text-align:left">&#x65F6;&#x6548;(&#x5EF6;&#x8FDF;) ms</td>
      <td style="text-align:left"></td>
      <td style="text-align:left"></td>
      <td style="text-align:left"></td>
      <td style="text-align:left"></td>
      <td style="text-align:left"></td>
    </tr>
    <tr>
      <td style="text-align:left">&#x53EF;&#x7528;&#x6027;</td>
      <td style="text-align:left"></td>
      <td style="text-align:left"></td>
      <td style="text-align:left"></td>
      <td style="text-align:left"></td>
      <td style="text-align:left"></td>
    </tr>
    <tr>
      <td style="text-align:left">
        <p>Topic &#x6570;&#x91CF;</p>
        <p>&#x5BF9;&#x541E;&#x5410;&#x7684;&#x5F71;&#x54CD;</p>
      </td>
      <td style="text-align:left"></td>
      <td style="text-align:left"></td>
      <td style="text-align:left"></td>
      <td style="text-align:left"></td>
      <td style="text-align:left"></td>
    </tr>
    <tr>
      <td style="text-align:left">&#x6D88;&#x606F;&#x53EF;&#x9760;&#x6027;</td>
      <td style="text-align:left"></td>
      <td style="text-align:left"></td>
      <td style="text-align:left"></td>
      <td style="text-align:left"></td>
      <td style="text-align:left"></td>
    </tr>
    <tr>
      <td style="text-align:left">&#x6269;&#x5C55;&#x6027;</td>
      <td style="text-align:left"></td>
      <td style="text-align:left"></td>
      <td style="text-align:left"></td>
      <td style="text-align:left"></td>
      <td style="text-align:left"></td>
    </tr>
    <tr>
      <td style="text-align:left">&#x529F;&#x80FD;&#x652F;&#x6301;</td>
      <td style="text-align:left"></td>
      <td style="text-align:left"></td>
      <td style="text-align:left"></td>
      <td style="text-align:left"></td>
      <td style="text-align:left"></td>
    </tr>
    <tr>
      <td style="text-align:left">
        <p>&#x5468;&#x8FB9;&#x914D;&#x5957;(&#x5982;</p>
        <p>SDK, &#x76D1;&#x63A7;&#x65B9;&#x6848;,</p>
        <p>&#x6D88;&#x606F;&#x7BA1;&#x7406;, Example&#x7B49;)</p>
      </td>
      <td style="text-align:left"></td>
      <td style="text-align:left"></td>
      <td style="text-align:left"></td>
      <td style="text-align:left"></td>
      <td style="text-align:left"></td>
    </tr>
    <tr>
      <td style="text-align:left">&#x8FD0;&#x7EF4;&#x96BE;&#x5EA6;</td>
      <td style="text-align:left"></td>
      <td style="text-align:left"></td>
      <td style="text-align:left"></td>
      <td style="text-align:left"></td>
      <td style="text-align:left"></td>
    </tr>
    <tr>
      <td style="text-align:left">&#x4E91;&#x539F;&#x751F;&#x652F;&#x6301;</td>
      <td style="text-align:left"></td>
      <td style="text-align:left"></td>
      <td style="text-align:left"></td>
      <td style="text-align:left"></td>
      <td style="text-align:left"></td>
    </tr>
    <tr>
      <td style="text-align:left">&#x793E;&#x533A;</td>
      <td style="text-align:left"></td>
      <td style="text-align:left"></td>
      <td style="text-align:left"></td>
      <td style="text-align:left"></td>
      <td style="text-align:left"></td>
    </tr>
  </tbody>
</table>

**总结**

根据如上的对比分析，可以得出一些指导性的原则，当然还是需要根据实际场景来选择。

1. TODO
2. TODO



### MQ 开源产品 - RocketMQ

TODO

### MQ 开源产品 - Kafka

TODO

### MQ 开源产品 - NSQ

TODO

### MQ 开源产品 - Pulsar

TODO

### 如何设计实现一个 MQ ？

#### 单机下百万队列存储设计 \(天池复赛题目）

题面描述很简单：使用 Java 或者 C++ 实现一个进程内的队列引擎，单机可支持 100 万队列以上

```java
public abstract class QueueStore {
    abstract void put(String queueName, byte[] message);
    abstract Collection<byte[]> get(String queueName, long offset, long num);
}
```

编写如上接口的实现。

put 方法将一条消息写入一个队列，这个接口需要是线程安全的，评测程序会并发调用该接口进行 put，每个 queue 中的内容按发送顺序存储消息（可以理解为 Java 中的 List），同时每个消息会有一个索引，索引从 0 开始，不同 queue 中的内容，相互独立，互不影响，queueName 代表队列的名称，message 代表消息的内容，评测时内容会随机产生，大部分长度在 58 字节左右，会有少量消息在 1k 左右。

get 方法从一个队列中读出一批消息，读出的消息要按照发送顺序来，这个接口需要是线程安全的，也即评测程序会并发调用该接口进行 get，返回的 Collection 会被并发读，但不涉及写，因此只需要是线程读安全就可以了，queueName 代表队列的名字，offset 代表消息的在这个队列中的起始索引，num 代表读取的消息的条数，如果消息足够，则返回 num 条，否则只返回已有的消息即可，若消息不足，则返回一个空的集合。

参考：

1. [https://www.cnkirito.moe/mq-million-queue/](https://www.cnkirito.moe/mq-million-queue/)
2. [http://www.wangyapu.com/2018/08/01/tianchi-mq/](http://www.wangyapu.com/2018/08/01/tianchi-mq/)
3. [https://code.aliyun.com/18868106990/queuerace2018/tree/thread](https://code.aliyun.com/18868106990/queuerace2018/tree/thread)
4. [https://developer.aliyun.com/article/742917](https://developer.aliyun.com/article/742917)
5. [https://github.com/HelloYym/queue-race](https://github.com/HelloYym/queue-race)

### Reference

#### 经典必读

* [日志：每个软件工程师都应该知道的有关实时数据的统一概念](https://www.kancloud.cn/kancloud/log-real-time-datas-unifying/58708) - 经典必读， [英文版](https://engineering.linkedin.com/distributed-systems/log-what-every-software-engineer-should-know-about-real-time-datas-unifying)，[另外一个中文版地址](https://github.com/oldratlee/translations/tree/master/log-what-every-software-engineer-should-know-about-real-time-datas-unifying)
* [消息队列通用问题](https://github.com/doocs/advanced-java#%E6%B6%88%E6%81%AF%E9%98%9F%E5%88%97) - 面试必备
* [Twitter 高性能分布式日志系统架构解析](https://mp.weixin.qq.com/s/g36Lf_0lMfuNYkzEwbvo7g)
  * [https://conferences.oreilly.com/strata/strata-ca-2018/public/schedule/speaker/267620.html](https://conferences.oreilly.com/strata/strata-ca-2018/public/schedule/speaker/267620.html)
  * 

#### 其他

* [MQ 消息中间件分析](https://mp.weixin.qq.com/s/lqFGnIUtqTFZ_GHp46z48Q)
  * 为什么使用 MQ？
  * MQ 有什么优缺点？
  * 几种 MQ 产品的对比，以及适用场景



