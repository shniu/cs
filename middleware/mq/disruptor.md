# 高性能内存队列 Disruptor

Disruptor 是一个高性能的队列，核心的业务逻辑处理器是在内存中运行的，不仅如此，它还提供了一种编程的框架模型：基于 Disruptor 构建一个生产者多消费者模式，不同参与者之间的通信通过一个有界的内存队列进行传递，非常类似 Golang 语言中的 channel。Disruptor 是由 LMAX 交易所团队开发并开源的，能够在无锁的情况下实现 Queue 的并发操作，基于 Disruptor 开发的交易所系统单线程能支撑每秒 600 万订单。

### Disruptor 为何有如此高的性能？

LMAX 是一个面向全球的交易所，而交易所对延迟是很敏感的，因为订单之间的撮合要非常快，才不至于影响后面订单的撮合，也就是说在高并发情况下，交易所要尽可能低延迟的提高成交数量。据开源 Disruptor 的团队介绍它每秒可以处理千万级以上的消息，并且消息的平均延迟在 50 纳秒（很牛的性能数据）。之所以有这么高的性能，Disruptor 的团队探索了很多影响性能的因素，最终选择了：

1. Cache line 与硬件友好性设计，避免了伪共享
2. 内存预先分配，降低运行时的 GC，使用了 RingBuffer 这个数据结构，空间是预先分配好的且运行时不需要回收
3. 缓存友好性，利用 RingBuffer 环形数组，尽可能提高 CPU 的缓存命中率
4. 使用 CPU 级别的原子操作 CAS，运用无锁算法来提高并发操作的性能，CAS 的无锁操作比内核级别的 Lock 代价要小很多
5. 模式和框架上的创新，摒弃了传统的并发队列的做法，使用 producer sequencing 和 consumer sequencing, 并且 consumer 之间可以形成 dependency graph

### 核心设计原理

Disruptor 经典的应用场景是生产消费模型，JUC 里面也有很多这种队列，但是这些队列大多是基于条件阻塞方式的，性能还不够优秀；

```java
ArrayBlockingQueue：基于数组形式的队列，通过加锁的方式，来保证多线程情况下数据的安全；
LinkedBlockingQueue：基于链表形式的队列，也通过加锁的方式，来保证多线程情况下数据的安全；
ConcurrentLinkedQueue：基于链表形式的队列，通过compare and swap(简称CAS)协议的方式，
来保证多线程情况下数据的安全，不加锁，主要使用了Java中的sun.misc.Unsafe类来实现；
```

而 Disruptor 通过以下设计来解决队列速度慢的问题：

* RingBuffer 数据结构

为了避免垃圾回收，采用数组而非链表。同时，数组对处理器的缓存机制更加友好。

* 元素位置定位

数组长度 2^n，通过位运算，加快定位的速度。下标采取递增的形式。不用担心 index 溢出的问题。index是 long 类型，即使 100 万 QPS 的处理速度，也需要 30 万年才能用完

* 无锁设计

每个生产者或者消费者线程，会先申请可以操作的元素在数组中的位置，申请到之后，直接在该位置写入或者读取数据

### 参考资料

1. [你应该知道的高性能无锁队列 Disruptor](https://juejin.im/post/6844903648875528206#heading-10)
2. [Disruptor Github](https://github.com/LMAX-Exchange/disruptor), [Disruptor wiki](https://github.com/LMAX-Exchange/disruptor/wiki)
3. [Disruptor](https://lmax-exchange.github.io/disruptor/)
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

