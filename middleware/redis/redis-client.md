# Redis 客户端



#### Redisson

Redisson 初始化流程

RedissonClient redissonClient = Redisson.create\(config\);

1. ConnectionManager: org.redisson.config.ConfigSupport\#createConnectionManager 根据 config 配置的 redis server 类型来决定使用哪个 ConnectionManager, 类型如下 a. org.redisson.connection.SingleConnectionManager extends MasterSlaveConnectionManager b. org.redisson.connection.MasterSlaveConnectionManager c. org.redisson.connection.SentinelConnectionManager d. org.redisson.cluster.ClusterConnectionManager e. org.redisson.connection.ReplicatedConnectionManager f. 应该也支持自定义的 ConnectionManager

   接着主要看一下 MasterSlaveConnectionManager 的初始化过程 1.1. 根据配置的 TransportMode 来初始化 EventLoopGroup \(NIO, Epoll, KQueue\), nettyThreads 默认是 32, EventLoopGroup 也支持 config 传入，也就是在外部初始化好 EventLoopGroup 后，传给 Redisson 使用；根据 TransportMode 来赋值 socketChannelClass；这里决定了 ConnectionManager 要使用什么样的网络模型和线程模型，底层是使用的 Netty 的 EventLoop 模型，需要对 Netty 进行进一步学习

1.2. resolverGroup 的用户暂时不清楚 1.3. 初始化 executorService, 可以外部指定，如果不指定默认会创建一个固定线程大小的线程池，默认配置是 16，如果不想指定，就设置 config.setThreads\(0\) 1.4. 保存 config, codec 1.5. 初始化 `CommandSyncService commandExecutor`, 目前还不清楚有什么用 1.6. 初始化定时器，使用 HashedWheelTimer 时间轮 1.7. 初始化一个空闲连接监控器 org.redisson.connection.IdleConnectionWatcher connectionWatcher 1.8. 初始化一个发布订阅服务 org.redisson.pubsub.PublishSubscribeService\#PublishSubscribeService 1.9. 初始化 masterSlaveEntry，然后 setupMasterEntry 创建一个 RedisClient 的实例，连接到 Master Redis，被 RFuture 包裹, syncUninterruptibly 有什么用呢？

> org.redisson.connection.MasterSlaveConnectionManager\#initSingleEntry org.redisson.connection.MasterSlaveEntry\#setupMasterEntry\(org.redisson.misc.RedisURI\) org.redisson.connection.ConnectionManager\#createClient org.redisson.connection.MasterSlaveConnectionManager\#createClient org.redisson.connection.MasterSlaveConnectionManager\#createClient\(org.redisson.api.NodeType, org.redisson.misc.RedisURI, int, int, java.lang.String\) org.redisson.client.RedisClient\#create new RedisClient\(config\) 创建 netty bootstrap 和 netty pubSubBootstrap: new BootStrap\(\) org.redisson.connection.MasterSlaveEntry\#setupMasterEntry\(redisClient\) redisClient.resolveAddr

1.10. 如果是 Master-Slave 模式，会在初始化 Slave Entry 1.11. 开启 DNSMonitoring，监控 Master Slave 的变化

1. 初始化一个 Eviction scheduler \(驱逐调度器\)，用于删除在 5s 和 2h 之间的过期项, 它分析已删除的过期键数量，并根据它来 "调整" 下一次的执行延迟。
2. 初始化 writeBehindService，这是一个后台写入服务，用来处理后台写入相关的任务

