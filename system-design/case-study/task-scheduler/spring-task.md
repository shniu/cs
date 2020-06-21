---
description: Spring Scheduled
---

# Spring Task

Spring 提供了两个抽象 TaskExecutor 和 TaskScheduler，主要是为了屏蔽差异，比如 Jdk 的实现、Quartz 的实现等等，我们可以实现很多版本的 Executor 和 Scheduler 来应对不同的场景，而使用 Spring 的编程模型是固定的；

此外，Spring 还提供了注解支持，来方便的使用，如 @Scheduled, @Async 等, [Spring Task and Schedule Document](https://docs.spring.io/spring/docs/5.2.7.RELEASE/spring-framework-reference/integration.html#scheduling)

`TaskScheduler` 是基于不同种类触发器的调度抽象

```java
public interface TaskScheduler {

    ScheduledFuture schedule(Runnable task, Trigger trigger);

    ScheduledFuture schedule(Runnable task, Instant startTime);

    ScheduledFuture schedule(Runnable task, Date startTime);

    ScheduledFuture scheduleAtFixedRate(Runnable task, Instant startTime, Duration period);

    ScheduledFuture scheduleAtFixedRate(Runnable task, Date startTime, long period);

    ScheduledFuture scheduleAtFixedRate(Runnable task, Duration period);

    ScheduledFuture scheduleAtFixedRate(Runnable task, long period);

    ScheduledFuture scheduleWithFixedDelay(Runnable task, Instant startTime, Duration delay);

    ScheduledFuture scheduleWithFixedDelay(Runnable task, Date startTime, long delay);

    ScheduledFuture scheduleWithFixedDelay(Runnable task, Duration delay);

    ScheduledFuture scheduleWithFixedDelay(Runnable task, long delay);
}
```

从接口定义中，可以分析出，`TaskScheduler` 提供了基本的任务调度能力，比如指定开始时间的一次性调度、使用 Fixed delay 和 Fixed Rate 的周期性任务；同时，又引入了调度更加灵活的触发器\(Trigger\)，比较常见的触发器实现如 `CronTrigger` 



