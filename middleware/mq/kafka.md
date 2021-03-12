# Kafka



### Reference

* [日志：每个软件工程师都应该知道的有关实时数据的统一概念](https://www.kancloud.cn/kancloud/log-real-time-datas-unifying/58708) - 经典必读， [英文版](https://engineering.linkedin.com/distributed-systems/log-what-every-software-engineer-should-know-about-real-time-datas-unifying)

日志可能是一种最简单的不能再简单的存储抽象，只能追加、按照时间完全有序（`totally-ordered`）的记录序列。

日志解决了两个核心的问题：变更动作的顺序（排序）和数据分发（通过日志把变更序列传输到其他服务、Slave or Replica。

状态机复制原理：**如果两个相同的、确定性的进程从同一状态开始，并且以相同的顺序获得相同的输入，那么这两个进程将会生成相同的输出，并且结束在相同的状态。**

* \*\*\*\*[**The Log: an epic software engineering article**](http://bryanpendleton.blogspot.com/2014/01/the-log-epic-software-engineering.html)\*\*\*\*

日志在过去几十年的时间里在数据库事务中扮演了很重要的角色，它记录了变更的详细信息（什么时间对哪个对象做了什么事情，以及事情发生的顺序），当出现应用异常终止、存储介质故障、断电宕机等情况时，可以利用日志来恢复或者重做。

对日志更深入的理解是日志具备二象性，即有表达类似于表的静态数据的能力（可以通过应用变更日志流来得到某个时间点的状态），也有表达类似于事件流的变更（日志就是记录了这种变更，这样就可以做很多事情，如重放、复制、衍生其他数据、方便数据分发）

在分布式状态机中：

> You can reduce the problem of making multiple machines all do the same thing to the problem of implementing a distributed consistent log to feed these processes input. The purpose of the log here is to squeeze all the non-determinism out of the input stream to ensure that each replica processing this input stays in sync.

日志可以用于构建大型的系统，至少在如下三个场景：

1. 数据集成 - 使组织的所有数据可轻松地在其所有存储和处理系统中使用
2. 实时数据流处理 - 计算派生数据流
3. 分布式系统设计 - 以日志为中心的设计如何简化实际系统。

关于数据集成：数据集成涉及一个普遍的问题，即通过安排从一个系统中导出数据并将其导入另一个系统中来从您管理的数据中获得更多价值，从而使这些数据可以以适合他们的最佳方式被其他系统重用。 

> The log also acts as a buffer that makes data production asynchronous from data consumption. This is important for a lot of reasons, but particularly when there are multiple subscribers that may consume at different rates. This means a subscribing system can crash or go down for maintenance and catch up when it comes back: the subscriber consumes at a pace it controls. A batch system such as Hadoop or a data warehouse may consume only hourly or daily, whereas a real-time query system may need to be up-to-the-second. Neither the originating data source nor the log has knowledge of the various data destination systems, so consumer systems can be added and removed with no change in the pipeline.

有人可能会担心在日志落盘时的性能和效率问题，这一点完全不必担心，顺序读写以及批量数据操作可以有很高的性能和效率

> A log, like a filesystem, is easy to optimize for linear read and write patterns. The log can group small reads and writes together into larger, high-throughput operations. Kafka pursues this optimization aggressively. Batching occurs from client to server when sending data, in writes to disk, in replication between servers, in data transfer to consumers, and in acknowledging committed data.

对于实时数据处理：

> The real driver for the processing model is the method of data collection. Data which is collected in batch is naturally processed in batch. When data is collected continuously, it is naturally processed continuously.

使用持续的日志数据流可以实现持续的数据处理，这样做既能解决数据处理的需求又能降低延迟，从而实现了实时流数据处理，而这个基础是基于日志的基础设施能很好的实时收集数据。

对于分布式系统设计的指导

在逻辑层面系统可以被分为两部分：日志层和服务层；日志层顺序捕获数据状态变更；服务层用来构建这个服务需要使用的索引结构以方便应对实际需求的查询，比如一个 kv 服务需要把数据构建成 btree 索引或 sstable 索引，这样更有利于提高查询效率；

* \*\*\*\*[**学习笔记：The Log**](https://www.cnblogs.com/foreach-break/p/notes_about_distributed_system_and_The_log.html)\*\*\*\*

