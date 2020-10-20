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
7. 消息队列性能分析 [chrome-extension://ikhdkkncnoglghljlkmcimlnlhkeamad/pdf-viewer/web/viewer.html?file=https%3A%2F%2Fdigitalscholarship.unlv.edu%2Fcgi%2Fviewcontent.cgi%3Farticle%3D4749%26context%3Dthesesdissertations](chrome-extension://ikhdkkncnoglghljlkmcimlnlhkeamad/pdf-viewer/web/viewer.html?file=https%3A%2F%2Fdigitalscholarship.unlv.edu%2Fcgi%2Fviewcontent.cgi%3Farticle%3D4749%26context%3Dthesesdissertations)

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

1. [你真的了解延迟队列吗？](https://juejin.im/post/6844903651685711885)
2. 延迟队列的一个实现：[delay-queue](https://github.com/ouqiang/delay-queue), 参考了[有赞的延迟队列设计](https://tech.youzan.com/queuing_delay/)

