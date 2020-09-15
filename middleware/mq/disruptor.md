# 高性能内存队列 Disruptor

Disruptor 是一个高性能的队列，核心的业务逻辑处理器是在内存中运行的，不仅如此，它还提供了一种编程的框架模型：基于 Disruptor 构建一个生产者多消费者模式，不同参与者之间的通信通过一个有界的内存队列进行传递，非常类似 Golang 语言中的 channel。Disruptor 是由 LMAX 交易所团队开发并开源的，能够在无锁的情况下实现 Queue 的并发操作，基于 Disruptor 开发的交易所系统单线程能支撑每秒 600 万订单。

### Disruptor 为何有如此高的性能？

LMAX 是一个面向全球的交易所，众所周知交易所对延迟是很敏感的，因为订单之间的撮合要非常快，才不至于影响后面订单的撮合，也就是说在高并发情况下，交易所要尽可能低延迟的提高成交数量。据开源 Disruptor 的团队介绍它每秒可以处理千万级以上的消息，并且消息的平均延迟在 50 纳秒（很牛的性能数据）。之所以有这么高的性能，Disruptor 的团队探索了很多影响性能的因素，最终选择了：

1. Cache line 与硬件友好性设计，避免了伪共享
2. 内存预先分配，降低运行时的 GC，使用了 RingBuffer 这个数据结构，空间是预先分配好的且运行时不需要回收
3. 缓存友好性，利用 RingBuffer 环形数组，尽可能提高 CPU 的缓存命中率
4. 使用 CPU 级别的原子操作 CAS，运用无锁算法来提高并发操作的性能，CAS 的无锁操作比内核级别的 Lock 代价要小很多
5. 模式和框架上的创新，摒弃了传统的并发队列的做法，使用 producer sequencing 和 consumer sequencing, 并且 consumer 之间可以形成 dependency graph
