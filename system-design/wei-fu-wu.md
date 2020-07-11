# 微服务

* [ ] [搞懂什么是微服务](https://xie.infoq.cn/article/83386f6d764984f3b64b760fb?y=qun0522)
* [ ] [https://medium.com/@madhukaudantha/microservice-architecture-and-design-patterns-for-microservices-e0e5013fd58a](https://medium.com/@madhukaudantha/microservice-architecture-and-design-patterns-for-microservices-e0e5013fd58a)
* [ ] 微服务架构设计模式

#### EDA \(事件驱动架构\)

[EDA](https://pradeeploganathan.com/architecture/event-driven-architecture/) 是一种架构范式。



#### 微服务的四大技术难题

技术难题都是和数据状态有关的。

* 数据一致性分发

一份数据要在多个系统中使用，如何解决一致性是关键。

![&#x6570;&#x636E;&#x5206;&#x53D1;](../.gitbook/assets/image%20%2825%29.png)

数据分发技术是解决数据一致性、构建大规模分布式系统、异步事件驱动架构的关键。数据分发一般会借助消息队列，但是既保更新本地数据库成功，又保证发送消息成功，就会涉及到分布式事务问题，一般意义上的双写会存在很大问题，如何解决事务性双写是关键。

事务性双写的解决方案

**模式1 事务性发件箱 \(transactional outbox\)**

![Transactional Outbox](../.gitbook/assets/image%20%2826%29.png)

事务发件箱的一个实现：[killbill/killbill-commons/queue](https://github.com/killbill/killbill-commons/tree/master/queue)

参考：[Transactional Outbox Pattern](https://pradeeploganathan.com/patterns/transactional-outbox-pattern/)

**模式2 变更数据捕获 \(Change Data Capture, CDC\)**

1. **Canal** 可以用于 CDC 模式的实现 \(推荐使用\)
2. Readhat Debezium
3. Zendesk Maxwell
4. SpinalTap

数据分发需要遵循一个原则：Single Source of Truth, 某一个服务是某些数据的唯一主人，其他的数据拷贝都是只读的。

还有一种模式，**RocketMQ 提供了事务消息**，在出现异常时，通过反查业务服务的接口来补偿，但是会带来更严重的耦合。

* 数据聚合 Join

服务拆分后，查询数据时需要数据聚合，然后返回用户使用

一般的做法是使用 **Aggregator / BFF\(Backend For Frontend\) 聚合服务层**，但是也存在一些问题，聚合层需要调用后台其他服务的接口，然后在本地做数据聚合，返回给前端，问题：

1. N + 1 问题：有时在调用服务时，需要调用很多次后端服务才能补齐数据
2. 数据量问题：聚合层需要把数据从其他服务拉取过来，放在本地做聚合，当访问量较大时，会占用大量的内存空间
3. 性能开销：随着后端服务的数量增加，性能会越来越低

改进方案：**数据分发 + 数据预聚合模式，也叫做 CQRS 模式**

当服务产生变更时，把变更数据量以数据流的方式，通过MQ发送到一个专门做预聚合的服务，近实时的进行聚合计算。

![](../.gitbook/assets/image%20%2824%29.png)

* 分布式事务

单机事务目前很成熟，但是服务拆分后，同时更新多个服务，就涉及到分布式事务，怎么保证分布式事务的可靠和安全呢？



资源参考：

1. [一种基于 Java 代理协调技术的分布式事务系统](https://github.com/codingapi/tx-lcn/blob/dev6.0/LCN%E5%88%86%E5%B8%83%E5%BC%8F%E4%BA%8B%E5%8A%A1%E6%A1%86%E6%9E%B6-20200102.pdf) and [分布式事务介绍](https://www.bilibili.com/video/av80626430) todo
2. Alibaba Seata
3. 
* 单体系统解耦拆分

单体系统是最开始的服务架构模式，但是到达一定

