---
description: 分布式锁的应用非常广泛，如何设计一个高性能的高可用的分布式锁呢？
---

# 分布式锁

锁是一个应用非常广泛的技术，无论在分布式系统中，还是单进程服务中，都有对锁使用的需求。

维基百科对[锁的定义](https://en.wikipedia.org/wiki/Lock_%28computer_science%29)

> In [computer science](https://en.wikipedia.org/wiki/Computer_science), a **lock** or **mutex** \(from [mutual exclusion](https://en.wikipedia.org/wiki/Mutual_exclusion)\) is a [synchronization](https://en.wikipedia.org/wiki/Synchronization_%28computer_science%29) mechanism for enforcing limits on access to a resource in an environment where there are many [threads of execution](https://en.wikipedia.org/wiki/Thread_%28computer_science%29). A lock is designed to enforce a [mutual exclusion](https://en.wikipedia.org/wiki/Mutual_exclusion) [concurrency control](https://en.wikipedia.org/wiki/Concurrency_control) policy.

大意是：计算机科学领域，锁或者互斥锁是一种同步机制，用于在多线程执行的环境中强制对资源的访问限制。锁旨在强制执行互斥并发控制策略。

锁的应用场景

1. 商品库存超卖问题：[https://segmentfault.com/a/1190000022935064](https://segmentfault.com/a/1190000022935064)
2. 账户资金转账

分布式锁适用的场景是在集群模式下，多个服务需要并发修改共享数据，为了保证数据的一致性，就需要适用分布式锁来解决。

设计分布式锁需要考虑哪些因素？

1. 性能
2. 可靠性

有哪些锁

1. 进程锁
2. 数据库锁
3. 分布式锁

每种锁都适用在哪些场景

如何实现一个高可用高性能高可靠的分布式锁？要根据场景做 trade-off



### 相关文章

* [用 Redis 实现分布式锁](https://carlosbecker.com/posts/distributed-locks-redis/)

这篇文章主要介绍对已有系统中分布式锁的改造和优化，是如何做的呢？todo

#### Chubby

Google 的分布式锁服务

Chubby 是 Google 实现的一个分布式锁服务，它是如何设计的？ Chubby 的主要目标是提供一个可靠的锁服务。Chubby并没有对高性能和频繁加锁的场景做优化，这些都是由Google内部的使用场景决定的（可以认为是粗粒度锁，还有细粒度锁，可以理解为频繁加锁解锁的场景，也就是说持有锁的时间的长短的区别） Chubby的一个典型应用场景是在多个应用之间选主，先获取到锁的会成为 Master

Chubby 的设计决策来自：

1. 粗粒度锁，应用不需要短暂的锁定，因为选主不是一件经常发生的事情
2. 除了锁服务之外，还需要小数据的存储能力
3. 允许成千上万的客户端观察变化，所以锁服务需要可以扩容以处理更多的客户端
4. 通知机制，当共享的文件发生变化时，可以及时通知客户端，比如主节点发生变化
5. 支持客户端缓存，用来处理需要主动轮询的客户端
6. 强大缓存的保证，来简化开发人员的使用

Chubby 的系统架构![](//note.youdao.com/src/3D75B2BF17AD4DF38B5AC39417910555)有两个主要的组件，一个是 Chubby master，一个是 Chubby client  
参考：[Chubby: A lock service for distributed coordination](https://medium.com/coinmonks/chubby-a-centralized-lock-service-for-distributed-applications-390571273052)[The Chubby lock service for loosely-coupled distributed system](https://static.googleusercontent.com/media/research.google.com/en//archive/chubby-osdi06.pdf) 论文[Chubby lock service 论文的翻译](https://blog.csdn.net/qq_38289815/article/details/103488701)[Chubby 的锁服务](http://catkang.github.io/2017/09/29/chubby.html) 读了一遍，没有读太懂

![](../../.gitbook/assets/image%20%28126%29.png)

#### zookeeper

[https://github.com/sfines/menagerie](https://github.com/sfines/menagerie) - ZooKeeper-based Java Concurrency Libraries

[Zookeeper vs Chubby](https://catkang.github.io/2017/10/10/zookeeper-vs-chubby.html)

* [https://laptrinhx.com/implementation-of-distributed-lock-with-zookeeper-326421370/](https://laptrinhx.com/implementation-of-distributed-lock-with-zookeeper-326421370/)
* [https://martin.kleppmann.com/2016/02/08/how-to-do-distributed-locking.html](https://martin.kleppmann.com/2016/02/08/how-to-do-distributed-locking.html)
* [https://programmer.group/zookeeper-distributed-lock-of-dead-java-synchronization-series.html](https://programmer.group/zookeeper-distributed-lock-of-dead-java-synchronization-series.html)

#### Redis

Redisson

Redis是一个kv的内存数据库，功能非常丰富[使用Redis实现分布式轻量级协作](https://www.ibm.com/developerworks/cn/opensource/os-cn-redis-coordinate/index.html)[用Redis实现分布式锁以及Redission](https://my.oschina.net/wangnian/blog/668830)

使用分布式锁的特性来实现 Leader 选举具有可行性，在启动多个 replicas 时，竞争锁，竞争到锁的实例被认为是 Leader，其他实例被作为 Fllower 监控 Leader 状态参考 [使用 Redis 实现轻量级的分布式系统协调服务](https://www.ibm.com/developerworks/cn/opensource/os-cn-redis-coordinate/index.html)[Redisson](https://github.com/redisson/redisson)[使用Redis实现选主](https://www.jianshu.com/p/e527f6b14605)  


* 幂等消费场景
* 库存预扣减



* [redis vs zk](https://github.com/doocs/advanced-java/blob/main/docs/distributed-system/distributed-lock-redis-vs-zookeeper.md)
* [如何设计更优的分布式锁](https://time.geekbang.org/column/article/125983)
* [分布式锁的实现之 Redis 篇](https://xiaomi-info.github.io/2019/12/17/redis-distributed-lock/) - 小米信息技术部



