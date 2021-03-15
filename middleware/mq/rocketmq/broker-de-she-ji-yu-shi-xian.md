# Broker 的设计与实现

Broker 是 RocketMQ 的核心服务，它负责消息存储、消息转发、消息过滤、缓存消费者的消费进度等，可见它是连接消息生产者和消费者的核心组件，也是实现异步、解耦合、削峰填谷、数据缓冲等的重要基础。而针对 Broker，它有自己的核心职责要完成：

1. 为了实现服务自动发现，Broker 需要有服务注册的能力，这个就是 Broker 与 Namesrv 配合的地方
2. 消费者发送消息给  Broker，Broker 需要具备高性能的网络 IO，高吞吐的消息接收能力，处理消息要低延迟，支持各种场景下的消息发送功能：同步消息发送、异步消息发送、批量消息发送等
3. Broker 收到消息后要能管理好消息，并路由转发给需要它的消费者（为了降低 Broker 的实现复杂度，都是采取消费者拉取消息的模式），这个是 Broker 最基本最核心的功能
4. 为了 Broker 具备 HA 的能力，需要做 M-S 同步，或者 Broker 集群，在遇到故障时可以做故障切换，以免因为 Broker 不可恢复的故障导致消息丢失
5. 消费者可以向 Broker 订阅某个 Topic 下的消息，一般都是以消费者组的方式进行订阅，然后使用负载均衡算法来分配具体的 MessageQueue 给到特定的消费者消费，这里也有很多需要注意的地方

针对 Broker 的权衡设计和实现，RocketMQ 的团队做了很多的努力和优化，从两个层面来分析一下 Broker：

1. 从外层看 Broker，它提供了消息接收的 API、消息订阅的 API、Broker 的 HA 保证、Topic 的 MessageQueue 的分配在多个 Broker，提供了水平扩展的能力
2. 从内部看 Broker，对于消息的存储统一抽象了内部的 MessageStore API，对于 API 协议的网络交互统一抽象了内部的基于 Netty 的 Remoting Service 和 Client，对于消息的快速查询抽象了索引服务 Index API，对于消息的过滤提供了 Filter API 等等；而这些内部实现，很多都基于底层的 OS 的特性做了优化，非常值得一探究竟

内外结合来分析 Broker，弄懂它实现的原理是基本目标。

首先我们要明白 MQ 的一个重要理论基础：日志，就像 The Log: What every software engineer should know about real-time data's unifying abstraction 里说的日志所能表现出来的诸多能力；

### 消息存储设计

RocketMQ Broker 的消息存储设计采取了当前 Broker 上的所有 MessageQueue 共享同一个 CommitLog 的方式，这里的 CommitLog 就是被接收的消息的日志，按照时间顺序的存放在文件中，在使用消息的时候也可以顺序的取出

Producer 发送一条消息时指定了 broker、topic、queueId 以及 Message 本身



