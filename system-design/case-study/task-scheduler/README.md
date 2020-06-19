---
description: 关于分布式任务调度框架
---

# 分布式任务调度

### why ?

任务调度主要解决：定时批量处理场景、时间驱动处理场景、异步执行解耦合等

任务调度需要满足：

1. 任务执行监控告警能力
2. 任务可灵活动态配置，无需重启
3. 业务透明，低耦合，配置精简，开发方便
4. 易测试
5. 高可用，无单点故障
6. 任务不可重复执行，防止逻辑异常
7. 大任务的分发并行处理能力

### 目前的解决方案

#### Java Timer

```java
TimerTask task1 = new TimerTask() {
    @Override
    public void run() {
        // logic
    }
};
Timer timer = new Timer();
timer.schedule(task1, 0, 1000);
```

1. Timer底层是使用单线程来处理多个Timer任务，这意味着所有任务实际上都是串行执行，前一个任务的延迟会影响到之后的任务的执行。
2. 由于单线程的缘故，一旦某个定时任务在运行时，产生未处理的异常，那么不仅当前这个线程会停止，所有的定时任务都会停止。
3. Timer任务执行是依赖于系统绝对时间，系统时间变化会导致执行计划的变更

可见，Timer 的方案并不可靠，在生产中不推荐使用。具体分析见：[Timer](timer.md)

#### ScheduledExecutorService

在 JDK 的 Timer 中也推荐使用 `ScheduledExecutorService` 进行替代；它的内部是使用线程池来实现，可以支持多任务并发执行，多个线程之间不会相互影响；基于时间间隔的延迟调度；

不足是：只能根据任务的延迟来进行调度，无法满足基于绝对时间和日历调度的需求；而且只能满足单机调度，提供了基本的调度功能。具体分析见：[ScheduledExecutorService](scheduledexecutorservice.md)

#### Spring Task

Spring Task 是 Spring 提供的轻量级任务调度框架，配置和使用都比较简单；但是缺点是Task 本身不支持持久化，不支持集群调度，需要开发人员自己解决，此外不支持可视化配置等；具体分析见：[Spring Task](spring-task.md)

#### Quartz



#### xxl-job

文档：[https://www.xuxueli.com/xxl-job](https://www.xuxueli.com/xxl-job)， Github: [https://github.com/xuxueli/xxl-job](https://github.com/xuxueli/xxl-job)



