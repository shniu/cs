# Kubernetes

### 如何学习 Kubernetes

* [Kubernetes 学习路径](https://www.infoq.cn/article/9DTX*1i1Z8hsxkdrPmhk)
* [如何学习 Kubernetes ](https://qiankunli.github.io/2020/03/03/learn_kubernetes.html#%E4%BB%8E%E6%8A%80%E6%9C%AF%E5%AE%9E%E7%8E%B0%E8%A7%92%E5%BA%A6%E7%9A%84%E6%95%B4%E4%BD%93%E8%AE%BE%E8%AE%A1)

### [容器技术及容器发展史](rong-qi-ji-shu.md#rong-qi-fa-zhan-shi)

### 

### Kubernetes 发展史

* [x] [从风口浪尖到十字路口，写在 Kubernetes 两周年之际](https://mp.weixin.qq.com/s/hrgXzt7YKVf6ZCFzJ-WTFA)

主要介绍了 Kubernetes 的发展史，Kubernetes 站在 Google Borg 项目的肩膀上，吸取了 Omega 项目的失败经验，随后发布了 Kubernetes 1.0 和 CNCF 的成立，Kubernetes 正式步入正轨。Kubernetes 在容器编排领域发力，逐渐在容器和容器云平台的大战中胜出。Google 的数据中心基础架构，则以 Cluster Management 生态体系为核心，通过**全容器化管理、分布式存储、软件定义网络**这“三驾马车”，为上层的生产业务（如 Google Search）和批处理任务（如大数据和深度学习）提供外界难以匹敌的功能、性能、和稳定性的保障。

PaaS 和 IaaS 之间的矛盾也是 Kubernetes 要解决的核心问题：Google 的 公有云 PaaS 产品 AppEngine 虽有很强的托管性（自动部署、运维），但灵活性较差（只支持固定语言和中间件）；而 IaaS 产品 Compute Engine 虽然灵活性高（近似裸机的可配置性），但管理性很低（用户自行进行应用安装、维护、配置）。

Kubernetes 的未来：

1. 从容器管理到集群管理，DCOS 或集群管理是一个更大的概念，容器管理只是其中的一部分，真正要实现如 Google 一样纯粹的容器化数据中心（没有且不需要 IaaS、PaaS 的分层），则需要围绕 Kubernetes 建立一个完整的集群管理体系。
2. 从自动化到智能化，Google 内部使用的 Borg 真正做到了“**调度一切任务**”，不论是无状态微服务应用，还是大数据、深度学习业务，都通过 Borg 平台统一的管理；而大数据和深度学习业务也正是利用了 Borg 提供的敏捷分布式计算，极大地提升了其自身的性能。

* [ ] [对平台的理解](https://qiankunli.github.io/2020/07/01/container_cloud.html)

### etcd

* [ ] \*\*\*\*[**分布式键值存储 etcd 原理与实现 · Analyze**](https://wingsxdu.com/post/database/etcd/#gsc.tab=0)

### 参考

* [Kubernetes 基本概念和应用](https://www.bilibili.com/video/BV1Ja4y1x748)
* [Kubernetes 编程基础知识](https://cloudnative.to/blog/kubernetes-programming-base/)
* [Kubernetes 源码精读](https://github.com/cloudnativeto/sig-k8s-source-code)

