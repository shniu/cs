---
description: 简要介绍
---

# Introduction

{% hint style="info" %}
Engineers for the cloud native Era.

**计算机科学领域的任何问题都可以通过增加一个间接的中间层来解决。**
{% endhint %}

> 学习不仅是为了找到答案，更是为了找到方法。

CS 是计算机科学 \(Computer Science\) 的缩写，这个 Project 是一个总结，把自己认为和计算机相关的一些东西放进来（主要是和自己工作相关的一些知识），当然也不仅仅只包含计算机的东西。目前更多的关注在：

1. 计算机基础学科：操作系统、网络、数据结构与算法、编译原理、计算机构造、组成原理等
2. 架构和系统设计
3. 编程语言：Java、Golang、C/C++、Rust 等
4. 常用的组件和中间件，源码级别的探索
5. 前沿方向：云计算、云原生、大数据、区块链、AI 等
6. 其他一些杂谈，如技术管理、软技能、工具等

#### 项目

* [Toolbox](https://github.com/shniu/toolbox): Toolboxes often used for daily development, and build my own rapid development toolkit.

### 必看

* [jwasham](https://github.com/jwasham)/[**coding-interview-university**](https://github.com/jwasham/coding-interview-university) A complete computer science study plan to become a software engineer. 116K star
* [技术雷达第 22 期](https://assets.thoughtworks.com/assets/technology-radar-vol-22-cn.pdf)

#### [每个程序员都应该知道的延迟数](https://github.com/donnemartin/system-design-primer/blob/master/README-zh-Hans.md#%E6%AF%8F%E4%B8%AA%E7%A8%8B%E5%BA%8F%E5%91%98%E9%83%BD%E5%BA%94%E8%AF%A5%E7%9F%A5%E9%81%93%E7%9A%84%E5%BB%B6%E8%BF%9F%E6%95%B0)

```text
// 各种访问方式花费的时间
L1 cache reference 0.5 ns
Branch mispredict 5 ns
L2 cache reference 7 ns
Mutex lock/unlock 100 ns
Main memory reference 100 ns
Compress 1K bytes with Zippy 10,000 ns
Send 2K bytes over 1 Gbps network 20,000 ns
Read 1 MB sequentially from memory 250,000 ns
Round trip within same datacenter 500,000 ns
Disk seek 10,000,000 ns
Read 1 MB sequentially from network 10,000,000 ns
Read 1 MB sequentially from disk 30,000,000 ns
Send packet CA->Netherlands->CA 150,000,000 ns

===
Latency Comparison Numbers
--------------------------
L1 cache reference                           0.5 ns
Branch mispredict                            5   ns
L2 cache reference                           7   ns                      14x L1 cache
Mutex lock/unlock                           25   ns
Main memory reference                      100   ns                      20x L2 cache, 200x L1 cache
Compress 1K bytes with Zippy            10,000   ns       10 us
Send 1 KB bytes over 1 Gbps network     10,000   ns       10 us
Read 4 KB randomly from SSD*           150,000   ns      150 us          ~1GB/sec SSD
Read 1 MB sequentially from memory     250,000   ns      250 us
Round trip within same datacenter      500,000   ns      500 us
Read 1 MB sequentially from SSD*     1,000,000   ns    1,000 us    1 ms  ~1GB/sec SSD, 4X memory
Disk seek                           10,000,000   ns   10,000 us   10 ms  20x datacenter roundtrip
Read 1 MB sequentially from 1 Gbps  10,000,000   ns   10,000 us   10 ms  40x memory, 10X SSD
Read 1 MB sequentially from disk    30,000,000   ns   30,000 us   30 ms 120x memory, 30X SSD
Send packet CA->Netherlands->CA    150,000,000   ns  150,000 us  150 ms

Notes
-----
1 ns = 10^-9 seconds
1 us = 10^-6 seconds = 1,000 ns
1 ms = 10^-3 seconds = 1,000 us = 1,000,000 ns
```

## 关于 CS

计算机科学是系统性研究信息与计算的理论基础以及它们在计算机系统中如何实现与应用的实用技术的学科；涵盖的内容非常多，但是最核心最基础的学科是计算机科学的基石，包括：

1. 数据结构与算法
2. 计算机组成原理（计算机体系结构与计算机工程）
3. 操作系统
4. 计算机网络
5. 分布式系统
6. 计算机安全和密码学
7. 数据库
8. 并行计算和分布式计算
9. 编译原理
10. 软件设计
11. [应用领域]()

构建个人的知识体系，这些计算机科学的基石是重点，累起来的大厦才够稳固。非常喜欢某个老师的一个比喻，知识体系要建设的像一个倒立的八抓鱼，非常形象，下边的头部和身体是基石，这样就可以伸出很多的触手到不同应用领域。

## 目录说明

```text
cs/
 - algorithm (Data Structure & Algothrim 数据结构与算法)
 - ccp (Computer composition principle 计算机组成原理)
 - os (Operating System 操作系统)
 - network (Computer Network 计算机网络)
 - distributed (Distributed System 分布式系统)
 - security (Computer Security 计算机安全)
 - database (Database 数据库)
 - computing (Parallel Computing & Distributed Computing 并行计算&分布式计算)
 - compilation (Compilation Principle 编译原理)
 - design (Software Design 软件设计)
 - application

 - resource/  资源文件
   - img 图片文件
```

