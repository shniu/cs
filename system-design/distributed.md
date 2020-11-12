# 分布式系统

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

## 资源

* [MIT 6.824: Distributed Systems](http://nil.csail.mit.edu/6.824/2018/)
* [如何的才能更好的学习MIT6.824分布式系统课程？](https://www.zhihu.com/question/29597104)
* [分布式总结论文](https://www.jianshu.com/u/38eb16b24cb9)
* [https://github.com/WangYangA9/MIT-6.824-2018](https://github.com/WangYangA9/MIT-6.824-2018)
* [https://www.bilibili.com/video/av45207204/](https://www.bilibili.com/video/av45207204/)
* [https://zhuanlan.zhihu.com/p/34680235](https://zhuanlan.zhihu.com/p/34680235)

