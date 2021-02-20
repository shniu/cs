# Etcd

* [ ] \*\*\*\*[**分布式键值存储 etcd 原理与实现 · Analyze**](https://wingsxdu.com/post/database/etcd/#gsc.tab=0)
* [x] [raft 协议动画演示](http://thesecretlivesofdata.com/raft/)
* etcd: [https://etcd.io/](https://etcd.io/)
* [https://kubernetes.io/docs/tasks/administer-cluster/configure-upgrade-etcd/](https://kubernetes.io/docs/tasks/administer-cluster/configure-upgrade-etcd/)
* [etcd labs](http://play.etcd.io/install)

etcd 是一个高度一致的分布式键值存储，它提供了一种可靠的方式来存储需要由分布式系统或机器集群访问的数据。它可以优雅地处理网络分区期间的领导者选举，即使在领导者节点中也可以容忍机器故障。\(**etcd** is a strongly consistent, distributed key-value store that provides a reliable way to store data that needs to be accessed by a distributed system or cluster of machines. It gracefully handles leader elections during network partitions and can tolerate machine failure, even in the leader node.\)

1. 集群搭建和使用
2. Raft 协议
3. watch 机制
4. 应用场景：选主，服务发现，kv 存储

### raft 协议简单介绍

数据一致性问题是分布式系统的难题，在过去的 10 年里，Paxos 算法统治着一致性算法这一领域：绝大多数的实现都是基于 Paxos 或者受其影响。但尽管有很多工作都在尝试降低它的复杂性，但是 Paxos 算法依然十分难以理解。而 Raft 算法是一个为可理解性而设计的一致性算法。

当我们只有一个节点服务（比如是一个数据库服务）和一个客户端时，我们向服务发送一个数据变更，单节点上的数据很容易达成共识和一致性。但是当有多个节点，又该如何做到一致性共识呢？这个就是分布式共识问题，Raft 就是一个用来实现分布式共识的协议。

Raft 协议将节点分为三类：Leader, Candidate and Follower，而所有节点的初始状态都是 Follower，如果一个节点没有收到来自 Leader 的心跳，就会自动变成 Candidate，Candidate 会向其他节点请求投票，其他节点会返回它们的投票结果，如果 Candidate 获得了半数以上的票，就会成为 Leader，**这个过程就叫 Leader 选举**。此后所有的数据变更都会发给 Leader，Leader 收到的每个变更都会作为一个条目加入到节点的日志中，但是现在的条目处于未提交的状态，所以还不能更新节点的值；在提交之前，还需要把日志发给其他的 Followers，然后 Leader 等待半数以上的节点成功写入日志中，此时 Leader 会提交这个变更修改节点的值，Leader 然后将提交的通知发送给 Followers，这个时候，整个集群的状态就达成了共识。**整个过程可以叫 日志复制**。

可见，Raft 结合了 Leader 选举和日志复制来完成。先来详细看下 Leader 选举

1. Leader 选举有两个控制选举的超时配置：选举超时时间和心跳超时时间；选举超时时间是 Follwer 变为 Candidate 的等待时间，一般是 150ms 到 300ms 的一个随机值，一旦超时，Follower 就会变成 Candidate 进入一个新的选举周期，先投给自己一票，然后向其他节点发送一个投票请求，如果接收到投票请求的节点还没有投票，就会投票给请求的节点，该节点则会充置自己的超时时间，一旦 Candidate 获得了半数以上的票，就会变成 Leader；接着 Leader 就会发送追加消息给其他的 Follower；这些消息以心跳超时指定的时间间隔发送，Follwoer 会响应每一个追加的消息；此选举任期将持续到 Follower 停止接收心跳并成为 Candidate 为止，然后就会进入重新选举的流程。半数机制保证了每轮的选举都最多有一个 Leader 被选出。
2. 一旦我们有一个 Leader，需要把所有的系统变化复制到所有节点上，通过使用与心跳相同的“添加条目”消息来完成此操作。

Raft 的论文：[In Search of an Understandable Consensus Algorithm](https://raft.github.io/raft.pdf),  [raft 协议论文 - 中文版](https://github.com/maemual/raft-zh_cn/blob/master/raft-zh_cn.md)

Raft 的 Website: [https://raft.github.io/](https://raft.github.io/)

Raft 是一种基于消息传递通信模型、用来管理日志复制的一致性协议，它允许一组机器像一个整体一样工作，即使其中的一些机器出现错误也能正常提供服务。在 Raft 被提出来之前，Paxos 协议是第一个被证明的一致性算法，但是 Paxos 的原理理解与工程实现都很困难。Raft 是 Paxos 的一种实现方式，目标是提供更好理解的算法，并被证明可以提供与 Paxos 相同的容错性以及性能。

via: [分布式一致性协议 Raft 原理](https://wingsxdu.com/post/algorithms/raft/#gsc.tab=0)

Etcd使用的是raft一致性算法来实现的，是一款分布式的一致性KV存储，主要用于共享配置和服务发现。

via1:[ Etcd 架构和实现解析](http://jolestar.com/etcd-architecture/)

### Watch 机制

* [https://segmentfault.com/a/1190000021787055](https://segmentfault.com/a/1190000021787055)
* [https://xiaoz.co/2020/08/10/etcd-intro/](https://xiaoz.co/2020/08/10/etcd-intro/)

