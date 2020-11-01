# 微服务与云原生

* [ ] [搞懂什么是微服务](https://xie.infoq.cn/article/83386f6d764984f3b64b760fb?y=qun0522)
* [ ] [https://medium.com/@madhukaudantha/microservice-architecture-and-design-patterns-for-microservices-e0e5013fd58a](https://medium.com/@madhukaudantha/microservice-architecture-and-design-patterns-for-microservices-e0e5013fd58a)
* [ ] 微服务架构设计模式
* [ ] [https://microservices.io/](https://microservices.io/)
* [ ] [https://github.com/iambowen/cloud-design-patterns/](https://github.com/iambowen/cloud-design-patterns/) 云设计模式
* [ ] [https://docs.microsoft.com/en-us/azure/architecture/](https://docs.microsoft.com/en-us/azure/architecture/)
* [ ] [https://github.com/DocsHome/microservices](https://github.com/DocsHome/microservices) 微服务：从设计到部署
* [ ] 云原生：[https://jimmysong.io/awesome-cloud-native/](https://jimmysong.io/awesome-cloud-native/)
* [ ] [https://jimmysong.io/blog/must-read-for-cloud-native-beginner/](https://jimmysong.io/blog/must-read-for-cloud-native-beginner/)

### 微服务

> 出自 Martin Fowler 
>
> 简而言之，微服务架构风格是一种将单个应用程序开发为一组小型服务的方法，每个服务都跑在一个独立的进程中，并使用一种轻量级的方式进行通信，通常是HTTP资源API。 这些服务围绕业务能力构建，可通过全自动部署机制独立部署；这些服务几乎没有集中管理，而且他们可以用不同的语言编写，使用不同的数据存储技术。

对于微服务的理解，真的需要先看看 Martin Fowler 的 [Microservice Architecture](https://martinfowler.com/articles/microservices.html) , 微服务的开山之作。

微服务架构定义为面向服务的架构，它们由松耦合和具有边界上下文的元素组成。

[微服务的难题](https://eventuate.io/whyeventdriven.html)之一：分布式数据管理；每个服务都有自己的私有数据库，这样就可以做到服务的松耦合，所以保证多个服务的数据一致性是一个挑战，查询也是一个挑战（因为要将多个服务的数据做聚合）

通常的解决办法：服务间使用异步通信，比如领域事件、命令/回复消息。

> #### The problem of atomically updating the database and publishing messages: <a id="the-problem-of-atomically-updating-the-database-and-publishing-messages"></a>
>
> #### You can use an asynchronous architecture to solve the distributed data management challenges in a microservices architecture. However, one major challenge with implementing an asynchronous architecture is atomically updating the database and sending a message. <a id="the-problem-of-atomically-updating-the-database-and-publishing-messages"></a>



#### Event Sourcing

事件溯源是原子性的更新状态和发布事件的好方法。业务对象通过存储一系列的状态改变事件来持久化。

1. [如何理解事件溯源？](https://www.infoq.cn/article/2017/09/How-understand-event-traceabilit)
2. [Event Sourcing and Concurrent Updates](https://medium.com/@teivah/event-sourcing-and-concurrent-updates-32354ec26a4c)
3. [https://stitcher.io/blog/combining-event-sourcing-and-stateful-systems](https://stitcher.io/blog/combining-event-sourcing-and-stateful-systems)
4. [事件溯源模式](https://iambowen.gitbooks.io/cloud-design-pattern/content/patterns/event-sourcing.html)
5. Microsoft: [Introducing event sourcing](https://docs.microsoft.com/en-us/previous-versions/msp-n-p/jj591559%28v=pandp.10%29?redirectedfrom=MSDN)
6. [CQRS 和事件溯源](https://www.jdon.com/49501)
7. [LMAX 和 Event Sourcing 的思考](https://www.jdon.com/45031) LMAX 架构利用 Disruptor 的多消费端的并发处理能力，将 input event 持久化到 log 中，在崩溃重启时可以 replay 这些 input event

> 确保对应用程序状态的所有更改动作都存储为一系列事件。这意味着我们不存储对象的状态，相反，我们存储影响其状态的所有事件；然后，为了检索一个对象状态，我们必须读取与这个对象相关的不同事件，并逐一应用它们。



#### EDA \(事件驱动架构\)

[EDA](https://pradeeploganathan.com/architecture/event-driven-architecture/) 是一种架构范式。



#### 微服务的四大技术难题

技术难题都是和数据状态有关的。

* 数据一致性分发

一份数据要在多个系统中使用，如何解决一致性是关键。

![&#x6570;&#x636E;&#x5206;&#x53D1;](../../.gitbook/assets/image%20%2826%29.png)

数据分发技术是解决数据一致性、构建大规模分布式系统、异步事件驱动架构的关键。数据分发一般会借助消息队列，但是既保更新本地数据库成功，又保证发送消息成功，就会涉及到分布式事务问题，一般意义上的双写会存在很大问题，如何解决事务性双写是关键。

事务性双写的解决方案, [如何解决微服务的数据一致性分发问题](https://blog.csdn.net/hellozhxy/article/details/108369406)

**模式1 事务性发件箱 \(transactional outbox\)**

![Transactional Outbox](../../.gitbook/assets/image%20%2827%29.png)

事务发件箱的一个实现：[killbill/killbill-commons/queue](https://github.com/killbill/killbill-commons/tree/master/queue)

参考：

1. [Transactional Outbox Pattern](https://pradeeploganathan.com/patterns/transactional-outbox-pattern/)
2. [通过 Kafka 分布式事务实现微服务数据交换与发件箱模式](https://zhuanlan.zhihu.com/p/61641543)

**模式2 变更数据捕获 \(Change Data Capture, CDC\)**

1. **Canal** 可以用于 CDC 模式的实现 \(推荐使用\)
2. [Readhat Debezium](https://github.com/debezium/debezium), [tutorial](https://debezium.io/documentation/reference/1.2/tutorial.html) \([mysql connector java](https://github.com/osheroff/mysql-binlog-connector-java)\)
3. Zendesk Maxwell
4. SpinalTap

[Eventuate CDC Services ](https://eventuate.io/docs/manual/eventuate-tram/latest/cdc-configuration.html)支持两种事务性消息的实现：Polling table or log tailing，但是目前并不推荐应用在生产环境中，但是可以用来学习。实现的基本架构：

![Transactional outbox and CDC](../../.gitbook/assets/image%20%2862%29.png)

其他 eventuate 资料：[documentation](https://eventuate.io/docs/manual/eventuate-tram/latest/), [configure cdc service](https://eventuate.io/docs/manual/eventuate-tram/latest/cdc-configuration.html), [https://microservices.io/index.html](https://microservices.io/index.html)

数据分发需要遵循一个原则：Single Source of Truth, 某一个服务是某些数据的唯一主人，其他的数据拷贝都是只读的。

还有一种模式，**RocketMQ 提供了事务消息**，在出现异常时，通过反查业务服务的接口来补偿，但是会带来更严重的耦合。

* 数据聚合 Join

服务拆分后，查询数据时需要数据聚合，然后返回用户使用

一般的做法是使用 **Aggregator / BFF\(Backend For Frontend\) 聚合服务层**，但是也存在一些问题，聚合层需要调用后台其他服务的接口，然后在本地做数据聚合，返回给前端，问题：

1. N + 1 问题：有时在调用服务时，需要调用很多次后端服务才能补齐数据
2. 数据量问题：聚合层需要把数据从其他服务拉取过来，放在本地做聚合，当访问量较大时，会占用大量的内存空间
3. 性能开销：随着后端服务的数量增加，性能会越来越低

改进方案：**数据分发 + 数据预聚合模式，也叫做 CQRS 模式**

当服务产生变更时，把变更数据量以数据流的方式，通过MQ发送到一个专门做预聚合的服务，近实时的进行聚合计算。

![](../../.gitbook/assets/image%20%2824%29.png)

* 分布式事务

单机事务目前很成熟，但是服务拆分后，同时更新多个服务，就涉及到分布式事务，怎么保证分布式事务的可靠和安全呢？



资源参考：

1. [一种基于 Java 代理协调技术的分布式事务系统](https://github.com/codingapi/tx-lcn/blob/dev6.0/LCN%E5%88%86%E5%B8%83%E5%BC%8F%E4%BA%8B%E5%8A%A1%E6%A1%86%E6%9E%B6-20200102.pdf) and [分布式事务介绍](https://www.bilibili.com/video/av80626430) todo
2. Alibaba Seata
3. 
* 单体系统解耦拆分

单体系统是最开始的服务架构模式，但是到达一定



### 云原生

Github 仓库：[https://github.com/shniu/cloud-native-infrastructure](https://github.com/shniu/cloud-native-infrastructure)

> [官方定义](https://github.com/cncf/toc/blob/master/DEFINITION.md)：
>
> 云原生技术有利于各组织在公有云、私有云和混合云等新型动态环境中，构建和运行可弹性扩展的应用。云原生的代表技术包括容器、服务网格、微服务、不可变基础设施和声明式API。
>
> 这些技术能够构建容错性好、易于管理和便于观察的松耦合系统。结合可靠的自动化手段，云原生技术使工程师能够轻松地对系统作出频繁和可预测的重大变更。
>
> 云原生计算基金会（CNCF）致力于培育和维护一个厂商中立的开源生态系统，来推广云原生技术。我们通过将最前沿的模式民主化，让这些创新为大众所用。

什么是云？云是一个提供资源的平台；云计算的本质是按需分配资源和弹性计算；

什么是云原生？云原生应用即专门为在云平台部署和运行而设计的应用，让应用能够利用云平台实现资源的按需分配和弹性伸缩，是云原生应用被重点关注的地方。云原生还关注规模，分布式系统应该具备将节点扩展到成千上万个的能力，并且这些节点应具有多租户和自愈能力。

从本质上说，云原生是一种设计模式，它要求云原生应用具备可用性和伸缩性，以及自动化部署和管理的能力，可随处运行，并且能够通过持续集成、持续交付工具提升研发、测试与发布的效率。

云原生资料：

1. 未来架构：从服务化到云原生
2. 云原生模式：设计拥抱变化的软件
3. [Serverless Handbook：无服务架构实践手册](https://github.com/rootsongjc/serverless-handbook)
4. 云原生基础架构
5. 云原生 Java
6. 云原生 Go
7. [Kubernetes Handbook](https://github.com/rootsongjc/kubernetes-handbook), 值得读一下
8. Istio Handbook
9. [Programming Kubernetes](https://programming-kubernetes.info/), [https://github.com/programming-kubernetes](https://github.com/programming-kubernetes)

CNCF 总经理对[云原生的理解](https://mp.weixin.qq.com/s/NXSykNwwAKPMNzxWxLOr6g)：

> 云原生技术是指工程师和软件人员利用云计算构建更快、更有弹性的技术，这样做是为了快速满足客户的需求。
>
> 1. 要理解云原生技术，理解云计算很重要。
> 2. 有了云环境以后，最显著的变化是从由许多依赖组件组成的紧耦合系统转向由可以准独立运行的微小组件组成的松耦合系统。
> 3. 多亏了容器技术Docker，技术专家们找到了如何将软件与所依赖的库打包和装箱的方式，这样软件就可以在任何地方运行了，组件就是容器。
> 4. 云原生是一个生态系统，是一种架构模式和架构理念
>
> 云原生优势：
>
> 1. 快速部署
> 2. 更容易自动化
> 3. 可伸缩性和可靠性
> 4. 成本节约。简单的可伸缩性使得优化更容易。特别是对于托管的云服务，云服务提供商或中介帮助公司分配计算负载，公司只支付他们所需的资源，而不是让固定数量的服务器一直运行
>
> 云原生的挑战：
>
> 1. 可观测性，或者说是看到计算机系统内部发生了什么的能力，是一个很大的挑战。复杂、松散耦合的分布式系统的一个缺点是，随着公司规模的扩大，一个人很难完全理解它。所有这些容器都运行在不同的服务器上。容器可以在不同的服务器之间移动，但谁在跟踪呢？如果系统的可观测性很差，就很难理解在提交请求之后会发生什么。
>
> 云原生的下一个发展方向：边缘计算

Kubernetes Handbook 中对云原生的理解：

> 要想搞明云原生的未来，首先我们要弄明白云原生是什么。CNCF给出的定义是：
>
> * 容器化
> * 微服务
> * 容器可以动态调度
>
> 我认为云原生实际上是一种理念或者说是方法论，它包括如下四个方面：
>
> * 容器化：作为应用包装的载体
> * 持续交付：利用容器的轻便的特性，构建持续集成和持续发布的流水线
> * DevOps：开发与运维之间的协同，上升到一种文化的层次，能够让应用快速的部署和发布
> * 微服务：这是应用开发的一种理念，将单体应用拆分为微服务才能更好的实现云原生，才能独立的部署、扩展和更新
>
> 一句话解释什么是云原生应用：云原生应用就是为了在云上运行而开发的应用。



#### 服务网格

什么是 Service Mesh？\(what）服务网格是什么呢？服务网格是可配置的、低延迟的基础设施层，旨在使用应用程序编程接口（API）处理应用程序基础设施服务之间的大量基于网络的进程间通信。服务网格提供了关键的能力：服务发现，负载均衡，加密，可观察性，可追溯性，身份验证和授权，以及对断路器模式的支持。  
服务网格的实现一般是通过一个叫做 sidecar 的代理实例，sidecar 代理来处理服务间的通信、监控和安全等，这种代理方式可以处理从服务中抽象出来的任何东西，可以总结一下服务的要点：

1. 容器编排框架，服务会越来越多的被加入，容器也会越来越多，对容器的编排很有必要，kubernates是未来的方向
2. 服务和实例
3. 边车代理模式
4. 服务发现
5. 负载均衡
6. 加解密
7. 认证和授权
8. 支持断路器模式

![](../../.gitbook/assets/image%20%2870%29.png)

Service Mesh 这个服务网络专注于处理服务和服务间的通讯。其主要负责构造一个稳定可靠的服务通讯的基础设施，并让整个架构更为的先进和 Cloud Native。在工程中，Service Mesh 基本来说是一组轻量级的服务代理和应用逻辑的服务在一起，并且对于应用服务是透明的。

1. Service Mesh 是一个基础设施
2. Service Mesh 是一个轻量的服务通讯的网络代理
3. Service Mesh 对于应用服务来说是透明无侵入的
4. Service Mesh 用于解耦和分离分布式系统架构中控制层面上的东西

![sidecar &#x6A21;&#x5F0F;&#x89E3;&#x51B3;&#x63A7;&#x5236;&#x903B;&#x8F91;&#x76F8;&#x5173;&#x7684;&#x95EE;&#x9898;&#xFF0C;&#x548C;&#x5E94;&#x7528;&#x903B;&#x8F91;&#x8FDB;&#x884C;&#x5206;&#x79BB;](../../.gitbook/assets/image%20%2869%29.png)

what is a service mesh 系列：

1. [What is a service mesh?](https://www.redhat.com/en/topics/microservices/what-is-a-service-mesh) by read hat
2. [what is a service mesh?](https://www.nginx.com/blog/what-is-a-service-mesh/) by nginx.com
3. [What is a service mesh?](https://istio.io/docs/concepts/what-is-istio/) by Istio
4. [what is a service mesh?](https://jimmysong.io/blog/what-is-a-service-mesh/) by Jimmy song

> 服务网格（Service Mesh）是处理服务间通信的基础设施层。它负责构成现代云原生应用程序的复杂服务拓扑来可靠地交付请求。在实践中，Service Mesh 通常以轻量级网络代理阵列的形式实现，这些代理与应用程序代码部署在一起，对应用程序来说无需感知代理的存在。

[Pattern: Service Mesh](https://philcalcado.com/2017/08/03/pattern_service_mesh.html) 详细介绍了 Service Mesh 的演化，也可以参考 [管理设计篇之服务网格](https://time.geekbang.org/column/article/5920)

前沿技术分享：[Kong 公司的 CTO 关于云原生技术的分享，主要是基于服务网格](https://www.youtube.com/watch?v=afO0znHDmj4&list=PL95Ey4rht79-OV_5B_diiCQV61GA25Hoy) （WIP）

#### 服务网格的落地技术

比较成熟的解决方案有 Istio：[https://istio.io/](https://istio.io/)  和 [https://conduit.io/](https://conduit.io/)；Istio 是主流的方案，其核心的 Sidecar 被叫做 Envoy（使者），用来协调服务网格中所有服务的出入站流量，并提供服务发现、负载均衡、限流熔断等能力，还可以收集大量与流量相关的性能指标。在 Service Mesh 控制面上，有一个叫 Mixer 的收集器，用来从 Envoy 收集相关的被监控到的流量特征和性能指标。然后，通过 Pilot 的控制器将相关的规则发送到 Envoy 中，让 Envoy 应用新的规则。最后，还有一个为安全设计的 lstio-Auth 身份认证组件，用来做服务间的访问安全控制。

![Istio &#x6574;&#x4F53;&#x67B6;&#x6784;](../../.gitbook/assets/image%20%2872%29.png)

* [Istio handbook](https://jimmysong.io/istio-handbook/concepts/istio-architecture.html)

#### 边车模式 Sidecar

什么是边车模式（sidecar）？边车模式的主要目的是实现“控制”和“逻辑”的分离。比如我们不需要在服务中实现控制面上的东西，如监视、日志记录、限流、熔断、服务注册、协议适配转换等这些属于控制面上的东西，而只需要专注地做好和业务逻辑相关的代码，然后，由“边车”来实现这些与业务逻辑没有关系的控制功能。  
Sidecar 模式有点像一个服务的Agent，编程的本质就是将控制和逻辑分离和解耦，而边车模式也是异曲同工。  
实现方式有两种基本的方式：

* 通过SDK、Lib或Framework等，在开发时与应用进行集成
* 还有一种是通过类似Sidecar的方式，在运维时与真实应用集成起来

![](../../.gitbook/assets/image%20%2873%29.png)

Sidecar 解决什么问题？主要解决控制和逻辑的分离，服务调用中上下文的问题；熔断、路由、服务发现、计量、流控、监视、重试、幂等、鉴权等控制面上的功能，很大程度上和应用逻辑的关系并不大，而且和逻辑耦合在一起的话，随着系统复杂度的提升，将变得非常难于维护。 Sidecar 的适用场景？把控制和逻辑分离，标准化控制面上的动作和技术，从而提高系统整体的稳定性和可用性；一个比较明显的场景是对老应用系统的改造和扩展；另一个是对由多种语言混合出来的分布式服务系统进行管理和扩展。

Microsoft 关于边车模式的讲解 [Sidecar pattern](https://docs.microsoft.com/en-us/azure/architecture/patterns/sidecar)，使用sidecar的优势：

1. 就运行时环境和编程语言而言Sidecar和主应用之间是相互独立的，所以不需要为每个语言都开发一个sidecar
2. sidecar 可以访问和主应用程序相同的资源，比如sidecar可以监控由应用和sidecar使用的系统资源
3. 由于sidecar靠近应用，所以他们在通信时几乎没有延迟
4. 即使对于不提供扩展机制的应用程序，您也可以使用sidecar来扩展功能，方法是将其作为自己的进程附加到与主应用程序相同的主机或子容器中。

### 参考资源

* [https://eventuate.io/](https://eventuate.io/)

Solving distributed data management problems in a microservice architecture

* [https://github.com/eventuate-tram](https://github.com/eventuate-tram)
* [https://eventuate.io/exampleapps.html](https://eventuate.io/exampleapps.html)
* [https://microservices.io/patterns/](https://microservices.io/patterns/)

