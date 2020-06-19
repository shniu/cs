---
description: Spring Scheduled
---

# Spring Task

Spring 提供了两个抽象 TaskExecutor 和 TaskScheduler，主要是为了屏蔽差异，比如 Jdk 的实现、Quartz 的实现等等，我们可以实现很多版本的 Executor 和 Scheduler 来应对不同的场景，而使用 Spring 的编程模型是固定的；

此外，Spring 还提供了注解支持，来方便的使用，如 @Scheduled, @Async 等, [Spring Task and Schedule Document](https://docs.spring.io/spring/docs/5.2.7.RELEASE/spring-framework-reference/integration.html#scheduling)

`TaskScheduler` 是基于不同种类触发器的调度抽象



