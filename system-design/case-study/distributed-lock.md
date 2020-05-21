---
description: 分布式锁的应用非常广泛，如何设计一个高性能的高可用的分布式锁呢？
---

# 分布式锁

锁是一个应用非常广泛的技术，无论在分布式系统中，还是单进程服务中，都有对锁使用的需求。

维基百科对[锁的定义](https://en.wikipedia.org/wiki/Lock_%28computer_science%29)

> In [computer science](https://en.wikipedia.org/wiki/Computer_science), a **lock** or **mutex** \(from [mutual exclusion](https://en.wikipedia.org/wiki/Mutual_exclusion)\) is a [synchronization](https://en.wikipedia.org/wiki/Synchronization_%28computer_science%29) mechanism for enforcing limits on access to a resource in an environment where there are many [threads of execution](https://en.wikipedia.org/wiki/Thread_%28computer_science%29). A lock is designed to enforce a [mutual exclusion](https://en.wikipedia.org/wiki/Mutual_exclusion) [concurrency control](https://en.wikipedia.org/wiki/Concurrency_control) policy.

大意是：计算机科学领域，锁或者互斥锁是一种同步机制，用于在多线程执行的环境中强制对资源的访问限制。锁旨在强制执行互斥并发控制策略。

锁的应用场景

有哪些锁

1. 进程锁
2. 数据库锁
3. 分布式锁

每种锁都适用在哪些场景

如何实现一个高可用高性能的分布式锁？



### 相关文章

* [用 Redis 实现分布式锁](https://carlosbecker.com/posts/distributed-locks-redis/)

这篇文章主要介绍对已有系统中分布式锁的改造和优化，是如何做的呢？todo

Chubby: Google 的分布式锁服务

Redisson



* [如何设计更优的分布式锁](https://time.geekbang.org/column/article/125983)



