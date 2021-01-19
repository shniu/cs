---
description: 关于消息队列
---

# 消息队列设计

MQ 是现代微服务架构和云原生架构的基础服务组件，主要用来做异步处理、服务解耦、流量削峰等。一个高性能、低延迟、高可用、高可靠的消息队列在互联网业务中是非常必要的。

常见的 MQ：

1. Kafka
2. RocketMQ
3. RabbitMQ
4. Pulsar
5. [pmq](https://github.com/ppdaicorp/pmq) paipaidai mq
6. [qmq](https://github.com/qunarcorp/qmq)  去哪儿的消息队列



### 资源参考

1. [美团技术 - 消息队列设计精要](https://tech.meituan.com/2016/07/01/mq-design.html)
2. [去哪儿消息队列架构设计](https://github.com/qunarcorp/qmq/blob/master/docs/cn/arch.md)
3. [bigqueue](https://github.com/bulldog2011/bigqueue) A big, fast and persistent queue based on memory mapped file.
4. [四个架构设计案例及其思维方式](https://mp.weixin.qq.com/s/5e-yC0r18FZ04vMvGxIL-w)
5. [PMQ 设计](https://github.com/ppdaicorp/pmq/wiki/PMQ%E8%AE%BE%E8%AE%A1)
6. [killbill-commons/queue](https://github.com/killbill/killbill-commons/tree/master/queue)  Notification Queue: persistent job queue, used for jobs scheduling
7. 消息队列性能分析 [chrome-extension://ikhdkkncnoglghljlkmcimlnlhkeamad/pdf-viewer/web/viewer.html?file=https%3A%2F%2Fdigitalscholarship.unlv.edu%2Fcgi%2Fviewcontent.cgi%3Farticle%3D4749%26context%3Dthesesdissertations](chrome-extension://ikhdkkncnoglghljlkmcimlnlhkeamad/pdf-viewer/web/viewer.html?file=https%3A%2F%2Fdigitalscholarship.unlv.edu%2Fcgi%2Fviewcontent.cgi%3Farticle%3D4749%26context%3Dthesesdissertations)  [https://digitalscholarship.unlv.edu/cgi/viewcontent.cgi?article=4749&context=thesesdissertations](https://digitalscholarship.unlv.edu/cgi/viewcontent.cgi?article=4749&context=thesesdissertations)
8. [https://github.com/obsidiandynamics/meteor](https://github.com/obsidiandynamics/meteor)

博客

1. [金融级别：新一代云原生消息队列在腾讯计费的实践](https://cloud.tencent.com/developer/salon/live-1253?channel=hlwjgs)
2. [云原生时代消息中间件的演进路线](https://www.infoq.cn/article/XJHaDxGKIRL3AtvWPx5c)

### Big Queue

一个基于内存映射文件的大型、快速且持久的队列。

应用场景

1. 大数据场景下的日志收集
2. 大数据场景下的排序和搜索
3. 作为分布式消息队列的基础组件

前置知识

* 什么是 Memory-Mapped file？（[内存映射文件原理探索](https://blog.csdn.net/mg0832058/article/details/5890688)，[认真分析mmap](https://www.cnblogs.com/huxiao-tee/p/4660352.html)，[图文详解内存映射](https://www.jianshu.com/p/719fc4758813)）
* 数据结构：队列
* In message-oriented middleware solutions, fan-out is a messaging pattern used to model an information exchange that implies the delivery \(or spreading\) of a message to one or multiple destinations possibly in parallel, and not halting the process that executes the messaging to wait for any response to that message

设计

整体架构设计（模型） / 接口设计 / 使用和实现



### Messaging

* [Spring-messaging](https://github.com/spring-projects/spring-framework/tree/master/spring-messaging)
* [Spring Message](https://docs.spring.io/spring-integration/reference/html/message.html) and  [Core Message](https://docs.spring.io/spring-integration/reference/html/core.html#spring-integration-core-messaging)
* [Spring 集成](https://docs.spring.io/spring-integration/reference/html/index.html)
* [统一的消息模型和spring 集成在 Spring Cloud Stream 的应用](https://fangjian0423.github.io/2019/04/03/spring-cloud-stream-intro/)

### 内存队列

* JUC ArrayBlockingQueue
* JUC PriorityBlockingQueue
* [支持内部晋升的无锁并发优先级队列](https://my.oschina.net/u/2447383/blog/3156042)



* [Disruptor](https://lmax-exchange.github.io/disruptor/)

1. [你应该知道的高性能无锁队列 Disruptor](https://juejin.im/post/5b5f10d65188251ad06b78e3)
2. [The LAMX Architecture](https://martinfowler.com/articles/lmax.html) by Martin Fowler, [中文版翻译](http://ifeve.com/lmax/)
3. [https://lmax-exchange.github.io/disruptor/](https://lmax-exchange.github.io/disruptor/)
4. [https://github.com/shniu/disruptor.git](https://github.com/shniu/disruptor.git)
5. Introduction to the Disruptor
6. [高性能 Disruptor - 美团技术](https://tech.meituan.com/2016/11/18/disruptor.html)
7. [Disruptor wiki](https://github.com/LMAX-Exchange/disruptor/wiki/Introduction)
8. [极客专栏 - 队列](https://time.geekbang.org/column/article/41330)
9. [Low latency Trading Architecture at LMAX Exchange](https://www.infoq.com/presentations/lmax-trading-architecture/)
10. [Building Scalable Architecture](https://medium.com/koinex-crunch/building-scalable-architecture-85ea199aec67)
11. [Sharing data between threads without contention](http://ifeve.com/sharing-data-among-threads-without-contention-2/)
12. [Single Writer Principle](https://mechanical-sympathy.blogspot.com/2011/09/single-writer-principle.html)
13. [LSM Tree](https://medium.com/swlh/log-structured-merge-trees-9c8e2bea89e8)
14. [Disruptor 为什么这么快？](http://ifeve.com/locks-are-bad/)[汇总](https://coolshell.cn/articles/9169.html)
15. [https://juejin.im/post/6844903976924610574](https://juejin.im/post/6844903976924610574)
16. [https://juejin.im/post/6844903609591660552](https://juejin.im/post/6844903609591660552)
17. [Disruptor 源码阅读](https://coderbee.net/index.php/open-source/20130812/400)
18. [https://wiki.jikexueyuan.com/project/disruptor-getting-started/lmax-framework.html](https://wiki.jikexueyuan.com/project/disruptor-getting-started/lmax-framework.html)

### 延迟队列

[延迟队列](https://medium.com/@cheukfung/redis%E5%BB%B6%E8%BF%9F%E9%98%9F%E5%88%97-c940850a264f)的主要特性是进入队列的消息会被推迟到指定的时间才出队被消费。

1. [你真的了解延迟队列吗](https://juejin.im/post/6844903651685711885) ？ [你真的了解延迟队列吗（一）](https://juejin.im/post/6844903648397525006)
2. 延迟队列的一个实现：[delay-queue](https://github.com/ouqiang/delay-queue), 参考了[有赞的延迟队列设计](https://tech.youzan.com/queuing_delay/)
3. JUC 延迟队列实现：DelayQueue, 实现思路是 lock + 优先级队列, \([Leader/Follower 模式](https://blog.csdn.net/goldlevi/article/details/7705180)\), [并发模式](https://github.com/robbie-cao/note/blob/master/concurrency-pattern.md)
4. [https://juejin.im/post/6844904150703013901](https://juejin.im/post/6844904150703013901)
5. [https://www.cnblogs.com/rickiyang/p/12237612.html](https://www.cnblogs.com/rickiyang/p/12237612.html)
6. [https://www.jianshu.com/p/8e0886c3c761](https://www.jianshu.com/p/8e0886c3c761)
7. [https://xiazemin.github.io/MyBlog/web/2020/01/15/DelayQue.html](https://xiazemin.github.io/MyBlog/web/2020/01/15/DelayQue.html)
8. [你真的知道怎么实现一个延迟队列吗？](https://mp.weixin.qq.com/s/A85ievNNzHDrQv67yBkbtA)
9. [延迟队列浅析](https://mp.weixin.qq.com/s/xMM8GDNSIDh9ekzYds3YDg)
10. [https://www.baeldung.com/java-delay-queue](https://www.baeldung.com/java-delay-queue)

延迟队列场景：

1. 订单在30分钟之内未支付则自动取消。
2. 重试机制实现,把调用失败的接口放入一个固定延时的队列,到期后再重试。
3. 新创建的店铺，如果在十天内都没有上传过商品，则自动发送消息提醒。
4. 用户发起退款，如果三天内没有得到处理则通知相关运营人员。
5. 预定会议后，需要在预定的时间点前十分钟通知各个与会人员参加会议。
6. 关闭空闲连接，服务器中，有很多客户端的连接，空闲一段时间之后需要关闭之。
7. 清理过期数据业务。比如缓存中的对象，超过了空闲时间，需要从缓存中移出。
8. 多考生考试,到期全部考生必须交卷,要求时间非常准确的场景。

解决方案：

1. 定期轮询（数据库等）
2. JDK DelayQueue
3. JDK Timer
4. ScheduledExecutorService 周期性线程池
5. 时间轮\(kafka\)
6. 时间轮\(Netty的HashedWheelTimer\)
7. Redis有序集合（zset）
8. zookeeper之curator
9. RabbitMQ
10. Quartz,xxljob等定时任务框架
11. Koala\(考拉\)
12. JCronTab\(仿crontab的java调度器\)
13. SchedulerX（阿里）
14. 有赞延迟队列

#### 时间轮算法

如果一个系统中存在着大量的调度任务，而大量的调度任务如果每一个都使用自己的调度器来管理任务的生命周期的话，浪费cpu的资源并且很低效。

时间轮是一种高效来利用线程资源来进行批量化调度的一种调度模型。把大批量的调度任务全部都绑定到同一个的调度器上面，使用这一个调度器来进行所有任务的管理（manager），触发（trigger）以及运行（runnable）。能够高效的管理各种延时任务，周期任务，通知任务等等。

不过，时间轮调度器的时间精度可能不是很高，对于精度要求特别高的调度任务可能不太适合。因为时间轮算法的精度取决于，时间段“指针”单元的最小粒度大小，比如时间轮的格子是一秒跳一次，那么调度精度小于一秒的任务就无法被时间轮所调度。

![HashedWheelTimer](../../.gitbook/assets/image%20%2897%29.png)



* [时间轮算法是如何实现的？](https://www.cnblogs.com/luozhiyun/p/12075326.html)
* [时间轮算法](https://yfscfs.gitee.io/post/%E4%BB%A4%E4%BA%BA%E6%83%8A%E8%89%B3%E7%9A%84%E6%97%B6%E9%97%B4%E8%BD%AE%E7%AE%97%E6%B3%95timingwheel/)

