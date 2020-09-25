---
description: Todo list
---

# Todo

* [ ] @Transactional 的原理，[使用注意事项](https://blog.csdn.net/qq_20597727/article/details/84900994)，并由此探索一下 Spring 的事务管理
* [ ] spring 核心思想与源码阅读，spring boot （[https://fangjian0423.github.io/2017/06/05/springboot-source-analysis-summary/](https://fangjian0423.github.io/2017/06/05/springboot-source-analysis-summary/)）
* [ ] MyBatis 的[注解使用](https://blog.csdn.net/wfq784967698/article/details/78786001)，动态 SQL 等 
* [ ] 探索 MyBatis 的基本使用和源码分析，重点学习它的设计思想、模型、接口、实现等
* [ ] 学习常见的任务调度方式和设计，[重点搞定各种分布式任务框架](https://my.oschina.net/vivotech/blog/3190348#h3_16)（分解几步）
  * [ ] Quartz
  * [ ] xxl-job
* [ ] RestTemplate
* [ ] 通用幂等设计
* [ ] 消息队列设计
  * [ ] bigqueue 分析与实现
* [ ] 分布式限流设计
  * [ ] 实现分布式限流，[https://www.infoq.cn/article/Qg2tX8fyw5Vt-f3HH673](https://www.infoq.cn/article/Qg2tX8fyw5Vt-f3HH673)
  * [ ] 单机限流，Hyxtri , Sentinel, resillence4j, Redis + lua
* [ ] 网关设计
* [ ] 数字货币交易所设计和实现
  * [ ] 数字货币交易所设计
  * [ ] 数字货币钱包，对数字货币钱包的理解（看下hd中数字货币钱包怎么对接链）
* [ ] DDD 实践总结
* [ ] 后端存储设计
* [ ] 软件设计
  * [ ] 设计原则、模式
  * [ ] 软件设计
* [ ] 分布式系统理论和实践（分解）
  * [ ] 分布式一致性hash实现
  * [ ] 分布式案例分析
  * [ ] 分布式经典问题和资料
* [ ] MySQL
  * [ ] [https://juejin.im/post/5b82e0196fb9a019f47d1823\#heading-17](https://juejin.im/post/5b82e0196fb9a019f47d1823#heading-17)
  * [ ] [https://github.com/aneasystone/mysql-deadlocks](https://github.com/aneasystone/mysql-deadlocks) 死锁
  * [ ] [https://www.aneasystone.com/archives/2017/11/solving-dead-locks-two.html](https://www.aneasystone.com/archives/2017/11/solving-dead-locks-two.html)
  * [ ] [https://zhuanlan.zhihu.com/p/72855648](https://zhuanlan.zhihu.com/p/72855648)
  * [ ] [https://zhuanlan.zhihu.com/p/62025900](https://zhuanlan.zhihu.com/p/62025900)
  * [ ] [https://zhuanlan.zhihu.com/p/62251242](https://zhuanlan.zhihu.com/p/62251242)
* [ ] Java
  * [ ] 数据结构 - TreeMap [https://www.jianshu.com/p/2dcff3634326](https://www.jianshu.com/p/2dcff3634326)
* [ ] [Kubernetes 和微服务监控体系](https://space.bilibili.com/518029478?spm_id_from=333.788.b_765f7570696e666f.2) 杨波老师
* [ ] 阅读
  * [ ] [DDIA](https://github.com/Vonng/ddia/blob/master/ch1.md)
  * [ ] 精通比特币
  * [ ] 黄金时代
  * [ ] 区块链核心算法
  * [ ] DDD 与 实现 DDD
  * [ ] 技术的本质

#### Learning Plan

系统性的学习某个知识，需要理论和实践并行。

* 学习关系型数据库

计划：基于 MySQL 的核心思想，实现一个简版的 mysql-java / mysql-go

* 学习内存数据库

计划：基于 Redis 的核心思想，实现一个简版的 redis-java / redis-go

* 学习分布式存储和分布式数据库

计划：在原有的 mysql-java / redis-java 的基础之上增加分布式存储的支持

* 学习交易系统的开发

计划：实现一个 MVP 版本的撮合引擎，然后再迭代新的功能

