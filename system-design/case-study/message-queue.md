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



资源参考

1. [美团技术 - 消息队列设计精要](https://tech.meituan.com/2016/07/01/mq-design.html)
2. [去哪儿消息队列架构设计](https://github.com/qunarcorp/qmq/blob/master/docs/cn/arch.md)
3. [bigqueue](https://github.com/bulldog2011/bigqueue) A big, fast and persistent queue based on memory mapped file.
4. [四个架构设计案例及其思维方式](https://mp.weixin.qq.com/s/5e-yC0r18FZ04vMvGxIL-w)
5. [PMQ 设计](https://github.com/ppdaicorp/pmq/wiki/PMQ%E8%AE%BE%E8%AE%A1)
6. [killbill-commons/queue](https://github.com/killbill/killbill-commons/tree/master/queue)  Notification Queue: persistent job queue, used for jobs scheduling



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
