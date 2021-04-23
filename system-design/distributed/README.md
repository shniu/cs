# 分布式系统

{% hint style="info" %}
分布式系统是一系列独立节点的集合，它们对外表现为一致的系统状态。
{% endhint %}

### 拜占庭将军问题

[拜占庭将军问题](https://zh.wikipedia.org/wiki/%E6%8B%9C%E5%8D%A0%E5%BA%AD%E5%B0%86%E5%86%9B%E9%97%AE%E9%A2%98)是由[莱斯利·兰波特](https://zh.wikipedia.org/wiki/%E8%8E%B1%E6%96%AF%E5%88%A9%C2%B7%E5%85%B0%E6%B3%A2%E7%89%B9)在其同名[论文](https://zh.wikipedia.org/wiki/%E8%AE%BA%E6%96%87)中提出的[分布式对等网络](https://zh.wikipedia.org/wiki/%E5%AF%B9%E7%AD%89%E7%BD%91%E7%BB%9C)通信容错问题。

在[分布式计算](https://zh.wikipedia.org/wiki/%E5%88%86%E5%B8%83%E5%BC%8F%E8%A8%88%E7%AE%97)中，不同的[计算机](https://zh.wikipedia.org/wiki/%E8%A8%88%E7%AE%97%E6%A9%9F)通过通讯交换信息达成共识而按照同一套协作策略行动。但有时候，系统中的成员计算机可能出错而发送错误的信息，用于传递信息的通讯网络也可能导致信息损坏，使得网络中不同的成员关于全体协作的策略得出不同结论[\[2\]](https://zh.wikipedia.org/wiki/%E6%8B%9C%E5%8D%A0%E5%BA%AD%E5%B0%86%E5%86%9B%E9%97%AE%E9%A2%98#cite_note-DriscollHall2004-2)，从而破坏系统一致性[\[3\]](https://zh.wikipedia.org/wiki/%E6%8B%9C%E5%8D%A0%E5%BA%AD%E5%B0%86%E5%86%9B%E9%97%AE%E9%A2%98#cite_note-DriscollHall2003-3)。拜占庭将军问题被认为是容错性问题中最难的问题类型之一。

> 一组[拜占庭](https://zh.wikipedia.org/wiki/%E6%8B%9C%E5%8D%A0%E5%BA%AD%E5%B8%9D%E5%9C%8B)将军分别各率领一支军队共同围困一座城市。为了简化问题，将各支军队的行动策略限定为进攻或撤离两种。因为部分军队进攻部分军队撤离可能会造成灾难性后果，因此各位将军必须通过投票来达成一致策略，即所有军队一起进攻或所有军队一起撤离。因为各位将军分处城市不同方向，他们只能通过信使互相联系。在投票过程中每位将军都将自己投票给进攻还是撤退的信息通过信使分别通知其他所有将军，这样一来每位将军根据自己的投票和其他所有将军送来的信息就可以知道共同的投票结果而决定行动策略。

1. [拜占庭将军问题论文英文版](https://web.archive.org/web/20170205142845/http://lamport.azurewebsites.net/pubs/byz.pdf), or [这里](http://www.microsoft.com/en-us/research/wp-content/uploads/2016/12/The-Byzantine-Generals-Problem.pdf)
2. [拜占庭将军问题论文中文版](https://blog.csdn.net/weixin_40098405/article/details/105566437)
3. [拜占庭容错算法 PBFT](http://yangzhe.me/2019/11/25/pbft/)
4. [从拜占庭将军问题到 PBFT](https://my.oschina.net/u/4379065/blog/4526898)  \[Todo\]

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

分布式一致性\(distributed consensus\) 是分布式系统中最基本的问题， 用来保证一个分布式系统的可靠性以及容灾能力。简单的来讲，就是如何在多个机器间对某一个值达成一致, 并且当达成一致之后，无论之后这些机器间发生怎样的故障，这个值能保持不变。

抽象定义上， 一个分布式系统里的所有进程要确定一个值v，如果这个系统满足如下几个性质， 就可以认为它解决了分布式一致性问题, 分别是:

* Termination: 所有正常的进程都会决定v具体的值，不会出现一直在循环的进程。
* Validity: 任何正常的进程确定的值v', 那么v'肯定是某个进程提交的。比如随机数生成器就不满足这个性质.
* Agreement: 所有正常的进程选择的值都是一样的。

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
* [漫谈分布式系统、拜占庭将军问题和区块链](http://zhangtielei.com/posts/blog-consensus-byzantine-and-blockchain.html) \[Todo\]

