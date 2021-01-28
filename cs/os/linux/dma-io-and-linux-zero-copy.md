---
description: Zero Copy 的实现需要依赖于 DMA 技术
---

# DMA IO and Linux Zero Copy

目前绝大部分的应用服务都是 IO 密集型的，几乎都是基于 C/S 模型构建的，这也决定了现代网络应用的性能瓶颈：IO。

**传统的 Linux 的标准 IO 方式是基于数据拷贝的，也就是 IO 操作会导致数据在操作系统的内核空间缓冲区与用户空间缓冲区之间来回拷贝，而且需要经过 CPU**（CPU 读取数据放到自己的寄存器，然后执行指令把数据从寄存器写到对应的地址，真个过程就是：内核缓冲区数据 -&gt; CPU寄存器 -&gt; 用户空间缓冲区），缓冲区的最大好处是减少磁盘 IO 的操作，如果要访问的数据在操作系统的高速缓冲区中，我们直接从缓冲区获取而不需要访问实际的物理设备。**但是这种方式的弊端是每次数据拷贝都需要经过 CPU，这种不需要运算的无意义重复劳动占用了大量的 CPU 时间，导致 CPU 的利用率降低，我们应该让 CPU 干更多的计算的事情，这个也是 CPU 的优势，而且 CPU 干拷贝数据的事情效率着实很低。**

**零拷贝技术就是一个为了解决在有大量数据拷贝的场景中提升效率的方案**，从整体上提高的系统效率和吞吐量。

目前的存储器构成是分层的：

![&#x5B58;&#x50A8;&#x5668;&#x5C42;&#x6B21;&#x7ED3;&#x6784;](../../../.gitbook/assets/image%20%2899%29.png)



---

思路：

1. IO 控制方式有哪些
2. OS 中的传统 IO 和 DMA 方式的 IO

### IO 控制 - DMA

要搞明白零拷贝是什么，就得先了解操作系统的 IO 是怎么做的。如果需要从计算机组成原理层面了解 IO，看看 [Computer Organization/IO](../../computer-organization/io.md)。

### 零拷贝



### 扩展阅读

* [DMA：为什么 Kafka 这么快](https://time.geekbang.org/column/article/118657)
* [Efficient data transfer through zero copy](https://developer.ibm.com/articles/j-zerocopy/)
* [搞懂零拷贝](https://www.cnblogs.com/xiaolincoding/p/13719610.html)

