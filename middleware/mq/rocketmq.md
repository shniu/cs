# RocketMQ



### 安装

### 基本使用



### 基本概念和特性

via: [https://github.com/apache/rocketmq/blob/master/docs/cn/concept.md](https://github.com/apache/rocketmq/blob/master/docs/cn/concept.md)

via: [https://github.com/apache/rocketmq/blob/master/docs/cn/features.md](https://github.com/apache/rocketmq/blob/master/docs/cn/features.md)

### 技术架构

![RocketMQ &#x6280;&#x672F;&#x67B6;&#x6784;](../../.gitbook/assets/image%20%2877%29.png)

主要由 4 个部分组成：

* Producer：消息发布的角色，支持分布式集群方式部署。Producer通过MQ的负载均衡模块选择相应的Broker集群队列进行消息投递，投递的过程支持快速失败并且低延迟。（Producer 如何实现的，以及如何做的快速失败和低延迟？）
* Consumer：消息消费的角色，支持分布式集群方式部署。支持以push推，pull拉两种模式对消息进行消费。同时也支持集群方式和广播方式的消费，它提供实时消息订阅机制，可以满足大多数用户的需求。
* NameServer：NameServer是一个非常简单的Topic路由注册中心，其角色类似Dubbo中的zookeeper，支持Broker的动态注册与发现。主要包括两个功能：Broker管理，NameServer接受Broker集群的注册信息并且保存下来作为路由信息的基本数据。然后提供心跳检测机制，检查Broker是否还存活；路由信息管理，每个NameServer将保存关于Broker集群的整个路由信息和用于客户端查询的队列信息。然后Producer和Conumser通过NameServer就可以知道整个Broker集群的路由信息，从而进行消息的投递和消费。NameServer通常也是集群的方式部署，各实例间相互不进行信息通讯。Broker是向每一台NameServer注册自己的路由信息，所以每一个NameServer实例上面都保存一份完整的路由信息。当某个NameServer因某种原因下线了，Broker仍然可以向其它NameServer同步其路由信息，Producer,Consumer仍然可以动态感知Broker的路由的信息。
* BrokerServer：Broker主要负责消息的存储、投递和查询以及服务高可用保证，为了实现这些功能，Broker包含了以下几个重要子模块。
  1. Remoting Module：整个Broker的实体，负责处理来自clients端的请求。
  2. Client Manager：负责管理客户端\(Producer/Consumer\)和维护Consumer的Topic订阅信息
  3. Store Service：提供方便简单的API接口处理消息存储到物理硬盘和查询功能。
  4. HA Service：高可用服务，提供Master Broker 和 Slave Broker之间的数据同步功能。
  5. Index Service：根据特定的Message key对投递到Broker的消息进行索引服务，以提供消息的快速查询。

![Broker &#x67B6;&#x6784;](../../.gitbook/assets/image%20%2879%29.png)

### 部署架构

![RocketMQ &#x90E8;&#x7F72;&#x67B6;&#x6784;](../../.gitbook/assets/image%20%2878%29.png)

* NameServer是一个几乎无状态节点，可集群部署，节点之间无任何信息同步。
* Broker部署相对复杂，Broker分为Master与Slave，一个Master可以对应多个Slave，但是一个Slave只能对应一个Master，Master与Slave 的对应关系通过指定相同的BrokerName，不同的BrokerId 来定义，BrokerId为0表示Master，非0表示Slave。Master也可以部署多个。每个Broker与NameServer集群中的所有节点建立长连接，定时注册Topic信息到所有NameServer。 注意：当前RocketMQ版本在部署架构上支持一Master多Slave，但只有BrokerId=1的从服务器才会参与消息的读负载 （？什么意思）。
* Producer与NameServer集群中的其中一个节点（随机选择）建立长连接，定期从NameServer获取Topic路由信息，并向提供Topic 服务的Master建立长连接，且定时向Master发送心跳。Producer完全无状态，可集群部署。
* Consumer与NameServer集群中的其中一个节点（随机选择）建立长连接，定期从NameServer获取Topic路由信息，并向提供Topic服务的Master、Slave建立长连接，且定时向Master、Slave发送心跳。Consumer既可以从Master订阅消息，也可以从Slave订阅消息，消费者在向Master拉取消息时，Master服务器会根据拉取偏移量与最大偏移量的距离（判断是否读老消息，产生读I/O），以及从服务器是否可读等因素建议下一次是从Master还是Slave拉取。

结合部署架构图，描述集群工作流程：

* 启动NameServer，NameServer起来后监听端口，等待Broker、Producer、Consumer连上来，相当于一个路由控制中心。
* Broker启动，跟所有的NameServer保持长连接，定时发送心跳包。心跳包中包含当前Broker信息\(IP+端口等\)以及存储所有Topic信息。注册成功后，NameServer集群中就有Topic跟Broker的映射关系。
* 收发消息前，先创建Topic，创建Topic时需要指定该Topic要存储在哪些Broker上，也可以在发送消息时自动创建Topic。
* Producer发送消息，启动时先跟NameServer集群中的其中一台建立长连接，并从NameServer中获取当前发送的Topic存在哪些Broker上，轮询从队列列表中选择一个队列，然后与队列所在的Broker建立长连接从而向Broker发消息。
* Consumer跟Producer类似，跟其中一台NameServer建立长连接，获取当前订阅Topic存在哪些Broker上，然后直接跟Broker建立连接通道，开始消费消息。

via: [https://github.com/apache/rocketmq/blob/master/docs/cn/architecture.md](https://github.com/apache/rocketmq/blob/master/docs/cn/architecture.md)

### 设计

#### 消息存储设计

主要从存储整体架构、PageCache与Mmap内存映射以及RocketMQ中两种不同的刷盘方式来介绍。

**存储整体架构**

* CommitLog: 消息主体以及元数据的存储主体，存储Producer端写入的消息主体内容,消息内容不是定长的。单个文件默认是 1G，文件名长度为20位，左边补零，剩余为起始偏移量，比如00000000000000000000代表了第一个文件，起始偏移量为0，文件大小为1G=1073741824；当第一个文件写满了，第二个文件为00000000001073741824，起始偏移量为1073741824，以此类推。消息主要是顺序写入日志文件，当文件满了，写入下一个文件；（**这样命名文件的好处是可以快速定位某个偏移量的消息在哪个文件中存储，比如可以使用二分查找等**）
* ConsumeQueue: 消息消费队列，引入的目的主要是提高消息消费的性能，由于RocketMQ是基于主题topic的订阅模式，消息消费是针对主题进行的，如果要遍历commitlog文件中根据topic检索消息是非常低效的。Consumer即可根据ConsumeQueue来查找待消费的消息。其中，ConsumeQueue（逻辑消费队列）作为消费消息的索引，保存了指定Topic下的队列消息在CommitLog中的起始物理偏移量offset，消息大小size和消息Tag的HashCode值。consumequeue文件可以看成是基于topic的commitlog索引文件，故consumequeue文件夹的组织方式如下：topic/queue/file三层组织结构，具体存储路径为：$HOME/store/consumequeue/{topic}/{queueId}/{fileName}。同样consumequeue文件采取定长设计，每一个条目共20个字节，分别为8字节的commitlog物理偏移量、4字节的消息长度、8字节tag hashcode，单个文件由30W个条目组成，可以像数组一样随机访问每一个条目，每个ConsumeQueue文件大小约5.72M；
* IndexFile: IndexFile（索引文件）提供了一种可以通过key或时间区间来查询消息的方法。Index文件的存储位置是：$HOME \store\index${fileName}，文件名fileName是以创建时的时间戳命名的，固定的单个IndexFile文件大小约为400M，一个IndexFile可以保存 2000W个索引，IndexFile的底层存储设计为在文件系统中实现HashMap结构，故rocketmq的索引文件其底层实现为hash索引。

存储模块的设计还可以参考 [http://zjykzk.github.io/post/cs/rocketmq/store/](http://zjykzk.github.io/post/cs/rocketmq/store/)

#### 通信机制

* 协议设计
* 通信方式和流程
* 网络模型：Reactor 多线程设计

via: [https://github.com/apache/rocketmq/blob/master/docs/cn/design.md](https://github.com/apache/rocketmq/blob/master/docs/cn/design.md) （todo\)



### 实现

#### 存储模块实现 \(rocketmq/store 模块\)

存储模块的实现依赖于 `io.openmessaging.storage:dledger` , 有关 DLedger 的解读看：[阿里数据一致性实践：DLedger 技术在消息领域的应用](https://www.infoq.cn/article/f6y4QRiDitBN6uRKp*fq) 和 [DLedger - 基于 raft 协议的 commitlog 存储库](https://juejin.im/post/6844903913045360654)， [https://yq.aliyun.com/articles/718344](https://yq.aliyun.com/articles/718344)

代码仓库：[https://github.com/openmessaging/openmessaging-storage-dledger](https://github.com/openmessaging/openmessaging-storage-dledger)



### 基础模块 - remoting

remoting 对服务端和客户端通信做了抽象，提供了 `RemotingClient` 和 `RemotingServer` 两个接口，RocketMQ 的网络相关的功能都由该模块承担。

```java
// 通信的基础
public interface RemotingService {
    void start();

    void shutdown();

    void registerRPCHook(RPCHook rpcHook);
}

// Client 的抽象
public interface RemotingClient extends RemotingService {

    void updateNameServerAddressList(final List<String> addrs);

    List<String> getNameServerAddressList();

    RemotingCommand invokeSync(final String addr, final RemotingCommand request,
        final long timeoutMillis) throws InterruptedException, RemotingConnectException,
        RemotingSendRequestException, RemotingTimeoutException;

    void invokeAsync(final String addr, final RemotingCommand request, final long timeoutMillis,
        final InvokeCallback invokeCallback) throws InterruptedException, RemotingConnectException,
        RemotingTooMuchRequestException, RemotingTimeoutException, RemotingSendRequestException;

    void invokeOneway(final String addr, final RemotingCommand request, final long timeoutMillis)
        throws InterruptedException, RemotingConnectException, RemotingTooMuchRequestException,
        RemotingTimeoutException, RemotingSendRequestException;

    void registerProcessor(final int requestCode, final NettyRequestProcessor processor,
        final ExecutorService executor);

    void setCallbackExecutor(final ExecutorService callbackExecutor);

    ExecutorService getCallbackExecutor();

    boolean isChannelWritable(final String addr);
}

// Server 端的抽象
public interface RemotingServer extends RemotingService {

    void registerProcessor(final int requestCode, final NettyRequestProcessor processor,
        final ExecutorService executor);

    void registerDefaultProcessor(final NettyRequestProcessor processor, final ExecutorService executor);

    int localListenPort();

    Pair<NettyRequestProcessor, ExecutorService> getProcessorPair(final int requestCode);

    RemotingCommand invokeSync(final Channel channel, final RemotingCommand request,
        final long timeoutMillis) throws InterruptedException, RemotingSendRequestException,
        RemotingTimeoutException;

    void invokeAsync(final Channel channel, final RemotingCommand request, final long timeoutMillis,
        final InvokeCallback invokeCallback) throws InterruptedException,
        RemotingTooMuchRequestException, RemotingTimeoutException, RemotingSendRequestException;

    void invokeOneway(final Channel channel, final RemotingCommand request, final long timeoutMillis)
        throws InterruptedException, RemotingTooMuchRequestException, RemotingTimeoutException,
        RemotingSendRequestException;

}
```



### Client - Producer 实现



```text
org.apache.rocketmq.client.impl.producer.DefaultMQProducerImpl
 > defaultAsyncSenderExecutor: 创建了一个队列大小为 50000 的线程池，线程数量为cpu核数
 > asyncSenderExecutor: 还可以自定义线程池
 
 // 默认的异步发送流程
 1. getAsyncSenderExecutor() 获取到异步发送线程池
 2. 提交任务到线程池，用 Runnable 包裹，注册 callback，判断超时时间等
 3. 线程池调度到发送任务后，调用 sendDefaultImpl()
 4. 消息发送的核心逻辑
   - 4.1 判断服务状态是否 RUNNING 等
   - 4.2 根据 topic 找路由信息
   - 4.3 org.apache.rocketmq.client.latency.MQFaultStrategy#selectOneMessageQueue 选择一个 MessageQueue
     a. 默认策略是随机选择一个 MessageQueue，对每个线程维护一个index，每次取的时候+1，最终的效果有点像轮询
     b. 容错退避策略
```

消息发送的负载均衡

生产者在发送消息时，会首先查找 `TopicPublishInfo` ，如果本地找不到就去 NameSrv 去请求，最后还是没有找到就会异常退出；`TopicPublishInfo` 包括了 MessageQueue 的列表、是否顺序消息、本地线程的索引计数器、Topic 路由数据等，可以从 `TopicPublishInfo` 来选择一个 MessageQueue，具体选择的策略有两种：一种是如果没有 enable latencyFaultTolerance，就用递增取模的方式选择。一种是如果 enable 了，在递增取模的基础上，再过滤掉 not available 的。这里所谓的 latencyFaultTolerance, 是指对之前失败的，按一定的时间做退避，如果上次请求的 latency 超过 550L ms, 就退避 3000L ms；超过 1000L，就退避 60000L。参考 `org.apache.rocketmq.client.latency.MQFaultStrategy`

### Resource

* [https://rocketmq.apache.org/](https://rocketmq.apache.org/)
* [RocketMQ 官方中文架构文档](https://github.com/apache/rocketmq/tree/master/docs/cn)
* [https://www.jianshu.com/p/7f772c3eccd6](https://www.jianshu.com/p/7f772c3eccd6) consumer 流程
* [如何保证rocketmq不丢失消息](https://juejin.im/post/6844904102011338760)
* [如何保证kafka不丢失消息](https://mp.weixin.qq.com/s/qttczGROYoqSulzi8FLXww)

