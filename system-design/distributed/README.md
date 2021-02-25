# 分布式系统

{% hint style="info" %}
分布式系统是一系列独立节点的集合，它们对外表现为一致的系统状态。
{% endhint %}

### 分布式系统基础理论

#### CAP 理论

CAP 适用的场景：CAP 适用于互连且共享数据的分布式系统中，并且对数据做读写操作。比如 Memchache 可以作为集群存在，但是它并不是 CAP 讨论的范围，因为它的数据节点之间不互连也不共享数据；但是如 MySQL、Redis、Mongodb 就是 CAP 讨论的范围，因为它们有主从复制，需要数据同步；此外，CAP 讨论的是对数据的读写，比如 Zookeeper 的选主就不是 CAP 要考虑的。



![](../../.gitbook/assets/image%20%28126%29.png)

* [不要再将数据库称作 CP 或 AP](https://zhuanlan.zhihu.com/p/55053121) 
* [Jepsen: Redis](https://aphyr.com/posts/283-jepsen-redis)
* A Critique of the CAP Theorem - [https://arxiv.org/pdf/1509.05393.pdf](https://arxiv.org/pdf/1509.05393.pdf) 
* [CAP 理论回顾：规则变了](https://www.infoq.cn/article/cap-twelve-years-later-how-the-rules-have-changed/)
* [What is Redis in the context of the CAP Theorem?](https://www.quora.com/What-is-Redis-in-the-context-of-the-CAP-Theorem)

#### BASE 理论

* [BASE: 一种 ACID 的替代方案](https://www.cnblogs.com/savorboard/p/base-an-acid-alternative.html)，也可以理解为它是分布式事务的一种替代方案，不是所有场景都需要保障强事务性的 \(在对数据库进行分区后, 为了可用性（Availability）牺牲部分一致性（Consistency）可以显著地提升系统的可伸缩性 \(Scalability\)\)
* [最终一致性](https://www.allthingsdistributed.com/2008/12/eventually_consistent.html)

### 分布式一致性协议

构建数据密集型应用：[一致性与共识](https://github.com/Vonng/ddia/blob/master/ch9.md)

#### Raft

* [分布式一致性协议 Raft 原理](https://wingsxdu.com/post/algorithms/raft/#gsc.tab=0)
* Raft 的论文：[In Search of an Understandable Consensus Algorithm](https://raft.github.io/raft.pdf)
* Raft 的 Website: [https://raft.github.io/](https://raft.github.io/)
* [寻找一种易于理解的一致性算法（扩展版）](https://github.com/maemual/raft-zh_cn/blob/master/raft-zh_cn.md)

raft 的实现

* [DLedger](https://github.com/openmessaging/openmessaging-storage-dledger): A raft-based java library for building high-available, high-durable, strong-consistent commitlog, which could act as the persistent layer for distributed storage system, i.e. messaging, streaming, kv, db, etc.
* [https://github.com/hashicorp/raft](https://github.com/hashicorp/raft)
* [etcd 中 raft 的实现](https://github.com/etcd-io/etcd/tree/master/raft)
* 蚂蚁金服的 Raft 实现：[sofa-jraft](https://github.com/sofastack/sofa-jraft)， 以及[ sofa-jraft 的介绍](https://www.sofastack.tech/projects/sofa-jraft/overview/)
* 百度 raft 的实现：[braft](https://github.com/baidu/braft)， [复制模型](https://github.com/baidu/braft/blob/master/docs/cn/replication.md)

#### Paxos

* [Paxos 协议简介](https://github.com/baidu/braft/blob/master/docs/cn/paxos_protocol.md)

#### ZAB

ZAB 协议

#### QJM

#### Gossip

### 资源

* [MIT 6.824: Distributed Systems](http://nil.csail.mit.edu/6.824/2018/)
* [如何的才能更好的学习MIT6.824分布式系统课程？](https://www.zhihu.com/question/29597104)
* [分布式总结论文](https://www.jianshu.com/u/38eb16b24cb9)
* [https://github.com/WangYangA9/MIT-6.824-2018](https://github.com/WangYangA9/MIT-6.824-2018)
* [https://www.bilibili.com/video/av45207204/](https://www.bilibili.com/video/av45207204/)
* [https://zhuanlan.zhihu.com/p/34680235](https://zhuanlan.zhihu.com/p/34680235)

