# Kubernetes

> [https://github.com/shniu/cloud-native-infrastructure](https://github.com/shniu/cloud-native-infrastructure)

### 如何学习 Kubernetes

* [Kubernetes 学习路径](https://www.infoq.cn/article/9DTX*1i1Z8hsxkdrPmhk)
* [如何学习 Kubernetes ](https://qiankunli.github.io/2020/03/03/learn_kubernetes.html#%E4%BB%8E%E6%8A%80%E6%9C%AF%E5%AE%9E%E7%8E%B0%E8%A7%92%E5%BA%A6%E7%9A%84%E6%95%B4%E4%BD%93%E8%AE%BE%E8%AE%A1)

### [容器技术及容器发展史](rong-qi-ji-shu.md#rong-qi-fa-zhan-shi)

详细看：[容器技术发展](rong-qi-ji-shu.md#rong-qi-fa-zhan-shi)

### Kubernetes 发展史

* [x] [从风口浪尖到十字路口，写在 Kubernetes 两周年之际](https://mp.weixin.qq.com/s/hrgXzt7YKVf6ZCFzJ-WTFA)

主要介绍了 Kubernetes 的发展史，Kubernetes 站在 Google Borg 项目的肩膀上，吸取了 Omega 项目的失败经验，随后发布了 Kubernetes 1.0 和 CNCF 的成立，Kubernetes 正式步入正轨。Kubernetes 在容器编排领域发力，逐渐在容器和容器云平台的大战中胜出。Google 的数据中心基础架构，则以 Cluster Management 生态体系为核心，通过**全容器化管理、分布式存储、软件定义网络**这“三驾马车”，为上层的生产业务（如 Google Search）和批处理任务（如大数据和深度学习）提供外界难以匹敌的功能、性能、和稳定性的保障。

PaaS 和 IaaS 之间的矛盾也是 Kubernetes 要解决的核心问题：Google 的 公有云 PaaS 产品 AppEngine 虽有很强的托管性（自动部署、运维），但灵活性较差（只支持固定语言和中间件）；而 IaaS 产品 Compute Engine 虽然灵活性高（近似裸机的可配置性），但管理性很低（用户自行进行应用安装、维护、配置）。

Kubernetes 的未来：

1. 从容器管理到集群管理，DCOS 或集群管理是一个更大的概念，容器管理只是其中的一部分，真正要实现如 Google 一样纯粹的容器化数据中心（没有且不需要 IaaS、PaaS 的分层），则需要围绕 Kubernetes 建立一个完整的集群管理体系。
2. 从自动化到智能化，Google 内部使用的 Borg 真正做到了“**调度一切任务**”，不论是无状态微服务应用，还是大数据、深度学习业务，都通过 Borg 平台统一的管理；而大数据和深度学习业务也正是利用了 Borg 提供的敏捷分布式计算，极大地提升了其自身的性能。

* [x] [对平台的理解](https://qiankunli.github.io/2020/07/01/container_cloud.html)

如果你只是把自动化理解成实现一些几百行代码量的脚本，那么说明你并没有理解什么是真正的自动化，真正的自动化是给出一整套可以提升效率的平台，**这个平台可以集成各式各样的改善效率的工具集合**，而并非单独零散的脚本。当然这些工具也并非几天或几周就可以做完的，而是通过日常的工作中不断地发现问题、解决问题并总结而来的。以我们团队举例，将一些流程化比较固定的工作通过 RPA 来实现自动化，在出现故障时，为了能够第一时间快速响应故障，我们实现了故障自动拉群功能，这样可以减少故障过程中到处找人的问题。

### etcd

* [ ] \*\*\*\*[**分布式键值存储 etcd 原理与实现 · Analyze**](https://wingsxdu.com/post/database/etcd/#gsc.tab=0)

### 搭建环境

1. [Play with Kubernetes](https://labs.play-with-k8s.com/)
2. [Katacoda 提供的 Kubernetes 环境](https://katacoda.com/kubernetes)：免费的在线学习平台，无需注册，拥有 GitHub 账号即可登录
3. [Vagrant 搭建 Kubernetes cluster](https://github.com/shniu/kubernetes-vagrant-centos-cluster/blob/master/README-cn.md)，安装过程的记录[看这里](https://github.com/shniu/kubernetes-vagrant-centos-cluster/blob/master/installation-procedure-myself.md)
4. [minikube](https://github.com/kubernetes/minikube)
5. [kind](https://kind.sigs.k8s.io/)
6. [kubeadm](https://kubernetes.io/docs/setup/production-environment/tools/kubeadm/create-cluster-kubeadm/), [ha](https://kubernetes.io/docs/setup/production-environment/tools/kubeadm/high-availability/)

* [kube-ladder kubernetes 练级路径](https://github.com/caicloud/kube-ladder) （很不错）

### 容器国内外镜像

使用国内的镜像可以加速下载，一般使用 `registry.aliyuncs.com/google_containers` 来替代 `gcr.io/google_containers` 

```text
// Pause 容器镜像
gcr.io/google_containers/pause-amd64:3.0
registry.aliyuncs.com/google_containers/pause-amd64:3.0
```

### 基础知识

* 进程和线程\([Linux 线程实现机制分析](https://www.ibm.com/developerworks/cn/linux/kernel/l-thread/index.html)\)
* Linux 的 Namespace 和 Cgroups
* Linux 的 rootfs

### 基本概念

#### Pod

理解 Kubernetes 中 Pod 非常关键，它是 Kubernetes 创建和管理的最小可部署计算单元，Pod 是一组一个或多个容器，具有共享的存储/网络资源，以及有关如何运行这些容器的规范。它包含了一个或多个相对松耦合的容器，在非云环境中，在同一物理或虚拟机上执行的应用程序类似于在同一逻辑主机上执行的云应用程序。  
除应用程序容器外，Pod还可包含在Pod启动期间运行的init容器。如果集群提供此功能，则还可以注入临时容器进行调试。

Pod 的共享上下文是一组Linux Namespace，cgroups 和潜在的其他隔离方面-与隔离Docker容器相同。在Pod的上下文中，各个应用程序可能还会应用其他子隔离。

可见 Pod 在 Kubernetes 中非常重要，而且还和底层操作系统关系密切。

为什么需要 Pod ?

Pod 是 Kubernetes 中的原子调度单位。容器的本质是进程；容器，就是未来云计算系统中的进程；容器镜像就是这个系统里的“.exe”安装包。Kubernetes 就是未来云平台中的操作系统。

在真实的操作系统中，我们会发现进程可能并不是独自运行的，而是以进程组的方式运行，有机的组织在一起（进程组中的每个进程都松耦合的做一些自己的事情，彼此之间有关联, 使用 `pstree -g` 可以查看进程组与进程）, Kubernetes 项目所做的，其实就是将“进程组”的概念映射到了容器技术中，并使其成为了这个云计算“操作系统”里的“一等公民”。之所以这么做，是因为在实际的开发和实践过程中，应用之间一般都存在着类似于进程和进程组的关系，也就是这些应用之间有着密切的协作关系，使得它们必须部署在同一台机器上。

总结一下，需要 Pod 的第一个理由是处于资源调度/容器调度的考虑，将有超亲密关系的容器进行成组调度\(gang scheduling\)，超亲密关系的容器被放在一个 Pod 中，调度到同一台 Node 中，它们互相之间会发生直接的文件交换、使用 localhost 或者 Socket 文件进行本地通信、会发生非常频繁的远程调用、需要共享某些 Linux Namespace（比如，一个容器要加入另一个容器的 Network Namespace）等，可见 Kubernetes 项目的调度器，是统一按照 Pod 而非容器的资源需求进行计算的。

除此之外的一个重点是容器设计模式。Pod 实际上是一个逻辑概念，在真是的物理世界中，并不存在一个叫 Pod 的实体，也就是说，Kubernetes 真正处理的，还是宿主机操作系统上 Linux 容器的 Namespace 和 Cgroups，而并不存在一个所谓的 Pod 的边界或者隔离环境。Pod 其实是一组共享了某些资源的容器，Pod 中的所有容器共享的是同一个 Network Namespace, 并且可以声明共享同一个 Volume。

在 Kubernetes 中，Pod 的实现使用了一个中间容器，叫做 Infra 容器，在这个 Pod 中，Infra 容器永远都是第一个被创建的容器，而其他用户定义的容器，则通过 Join Network Namespace 的方式，与 Infra 容器关联在一起。如下图：

![](../../.gitbook/assets/image%20%2880%29.png)

在 Kubernetes 项目里，Infra 容器一定要占用极少的资源，所以它使用的是一个非常特殊的镜像，叫作：k8s.gcr.io/pause（[关于 Pause 容器的介绍](https://github.com/rootsongjc/kubernetes-handbook/blob/master/concepts/pause-container.md)）。这个镜像是一个用汇编语言编写的、永远处于“暂停”状态的容器，解压后的大小也只有 100~200 KB 左右。

对于 Pod 里的容器 A 和容器 B 来说：

1. 它们可以直接使用 localhost 进行通信；
2. 它们看到的网络设备跟 Infra 容器看到的完全一样；
3. 一个 Pod 只有一个 IP 地址，也就是这个 Pod 的 Network Namespace 对应的 IP 地址；
4. 当然，其他的所有网络资源，都是一个 Pod 一份，并且被该 Pod 中的所有容器共享；
5. Pod 的生命周期只跟 Infra 容器一致，而与容器 A 和 B 无关。

而对于同一个 Pod 里面的所有用户容器来说，它们的进出流量，也可以认为都是通过 Infra 容器完成的。这一点很重要，因为将来如果你要为 Kubernetes 开发一个网络插件时，应该重点考虑的是如何配置这个 Pod 的 Network Namespace，而不是每一个用户容器如何使用你的网络配置，这是没有意义的。

Pod 这种“超亲密关系”容器的设计思想，实际上就是希望，当用户想在一个容器里跑多个功能并不相关的应用时，应该优先考虑它们是不是更应该被描述成一个 Pod 里的多个容器。

> Pod 这个概念，提供的是一种编排思想，而不是具体的技术方案。所以，如果愿意的话，你完全可以使用虚拟机来作为 Pod 的实现，然后把用户容器都运行在这个虚拟机里。

  
via: [https://kubernetes.io/docs/concepts/workloads/pods/](https://kubernetes.io/docs/concepts/workloads/pods/)

via: [为什么我们需要 Pod ?](https://time.geekbang.org/column/article/40092), [基于容器的分布式系统设计模式](https://www.usenix.org/conference/hotcloud16/workshop-program/presentation/burns)

#### 什么是 Pause 容器，它有什么用处？

Pause 容器在启动后，永远处于暂停状态，每个 Pod 在启动时，Pause 容器是第一个被启动起来的。如何让一个 Pod 里的多个容器之间最高效的共享某些资源和数据。因为容器之间原本是被 Linux Namespace 和 cgroups 隔开的，所以现在实际要解决的是怎么去打破这个隔离，然后共享某些事情和某些信息。这就是 Pod 的设计要解决的核心问题所在：Pause 容器就是为解决 Pod 中的网络问题而生的。

官方镜像地址：`gcr.io/google_containers/pause-amd64:3.0` 

1. [Pause container](https://www.ianlewis.org/en/almighty-pause-container)
2. [Pause 容器 by Kubernetes handbook](https://github.com/rootsongjc/kubernetes-handbook/blob/master/concepts/pause-container.md)

#### Kubernetes 重要组件及命令

1. [Install and set up kubectl](https://kubernetes.io/docs/tasks/tools/install-kubectl/)
2. [kubectl Overview](https://kubernetes.io/docs/reference/kubectl/overview/) 

### Kubernetes 基本架构和容器调度流程

#### Node

[What is Node in Kubernetes? ](https://kubernetes.io/docs/concepts/architecture/nodes/)Kubernetes通过将容器放入 Pods 中以在 Node 上运行来运行您的工作负载。取决于群集，Node 可以是虚拟机或物理机。每个 Node 都包含运行 Pod 所需的服务，这些服务由控制平面管理。Node 上的组件包括 kubelet，容器运行时和kube-proxy。

有两种方式将 Node 加入到 Kubernetes Cluster \(api-server\):

1. Node 上的 kubelet 自注册到控制面板
2. 手动加入，人工执行加入的命令

在加入 Cluster 时，控制面板会检查 Node 的合法性，Kubernetes 在内部创建一个 Node 对象（表示）。 Kubernetes 会检查 kubelet 是否已注册到与 Node 的 metadata.name 字段匹配的 API 服务器。如果 Node 运行状况良好（如果所有必需的服务都在运行），则可以运行 Pod。否则，该 Node 的任何群集活动都将被忽略，直到它变得健康为止。

#### Kubernetes Components

Kubernetes集群由运行容器化应用程序的一组工作机（称为节点）组成。每个群集至少有一个工作节点。 工作节点托管 Pod，这些 Pod 是应用程序工作负载的组成部分。控制平面管理集群中的工作节点和 Pod。在生产环境中，控制平面通常在多台计算机上运行，​​而群集通常在多个节点上运行，从而提供了容错能力和高可用性。

![](../../.gitbook/assets/image%20%2881%29.png)

控制面板组件

* kube-apiserver

API 服务器是公开 Kubernetes API 的 Kubernetes 控制平面的组件。 API服务器是 Kubernetes 控制平面的前端。Kubernetes API 服务器的主要实现是 kube-apiserver。 kube-apiserver 旨在水平扩展，它通过部署更多实例进行扩展。您可以运行 kube-apiserver 的多个实例，并平衡这些实例之间的流量。

* kube-scheduler

控制平面组件，它监视没有分配节点的新创建的 Pod，并选择一个节点以使其运行。调度决策要考虑的因素包括：个人和集体资源需求，硬件/软件/策略约束，亲和力和反亲和力规范，数据局部性，工作间干扰以及截止日期。

* etcd

一致且高度可用的键值存储用作所有集群数据的Kubernetes的后备存储。如果您的Kubernetes集群使用etcd作为其后备存储，请确保您有针对这些数据的备份计划。

* kube-controller-manager

运行控制器进程的控制平面组件。 从逻辑上讲，每个控制器是一个单独的进程，但是为了降低复杂性，它们都被编译为单个二进制文件并在单个进程中运行。包括：

> * Node controller: Responsible for noticing and responding when nodes go down.
> * Replication controller: Responsible for maintaining the correct number of pods for every replication controller object in the system.
> * Endpoints controller: Populates the Endpoints object \(that is, joins Services & Pods\).
> * Service Account & Token controllers: Create default accounts and API access tokens for new namespaces.

* cloud-controller-manager

  
节点组件，每个节点都会有的

* kubelet

在集群中每个节点上运行的代理。确保容器在Pod中运行。kubelet包含通过各种机制提供的一组PodSpec，并确保这些PodSpec中描述的容器运行正常。 Kubelet不管理不是Kubernetes创建的容器。

* kube-proxy

kube-proxy是一个网络代理，它在集群中的每个节点上运行，实现了Kubernetes Service概念的一部分。kube-proxy维护节点上的网络规则。这些网络规则允许从群集内部或外部的网络会话与Pod进行网络通信。如果有kube-proxy可用，它将使用操作系统数据包过滤层。否则，kube-proxy会转发流量本身。

* 容器运行时

容器运行时是负责运行容器的软件。Kubernetes 支持多种容器运行时：docker, containerd, CRI-O 和任何实现 CRI 接口的容器引擎，CRI：[https://github.com/kubernetes/community/blob/master/contributors/devel/sig-node/container-runtime-interface.md](https://github.com/kubernetes/community/blob/master/contributors/devel/sig-node/container-runtime-interface.md)  
附加组件

* DNS
* Web UI \(dashboard\)
* 容器资源监控
* 集群级别的日志

via: [https://kubernetes.io/docs/concepts/overview/components/](https://kubernetes.io/docs/concepts/overview/components/)

### 参考

* [Kubernetes 基本概念和应用](https://www.bilibili.com/video/BV1Ja4y1x748)
* [Kubernetes 编程基础知识](https://cloudnative.to/blog/kubernetes-programming-base/)
* [Kubernetes 源码精读](https://github.com/cloudnativeto/sig-k8s-source-code)
* [Kubernetes Handbook](https://jimmysong.io/kubernetes-handbook/cloud-native/play-with-kubernetes.html)

环境

* [Play with Kubernetes](https://labs.play-with-k8s.com/)

