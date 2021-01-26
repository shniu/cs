---
description: 一个异步的基于事件驱动的高性能网络应用框架。
---

# Netty

| 必须掌握的知识 |
| :--- |
| Netty 的设计思想，设计哲学，它是如何理解这个网络世界的 |
| Netty 的线程模型和网络处理模型 |
| Netty 对 ByteBuffer 的改进和优化 |
| Netty 开发应用的一般流程 |

### IO 模型



### Netty 线程模型和运行架构 \(Reactor 模型实现\)

![Netty Reactor](../../.gitbook/assets/image%20%2893%29.png)

### Netty 源码分析



### Netty 高并发高性能架构设计精髓

* 主从 Reactor 线程模型
* IO 多路复用非阻塞
* 无锁串行化设计思想
* 支持高性能序列化协议
* 零拷贝技术\(使用 Java 提供的直接内存和 OS 的 DMA 能力\)

扩展阅读：

1. [搞懂 零拷贝技术](https://www.cnblogs.com/xiaolincoding/p/13719610.html)

* ByteBuf 内存池设计
* 灵活的 TCP 参数配置能力
* 并发优化



