# Table of contents

* [Introduction](README.md)

## CS 基础 <a id="cs"></a>

* [操作系统](cs/os/README.md)
  * [操作系统基础知识](cs/os/00-cao-zuo-xi-tong-ji-chu-zhi-shi.md)
  * [系统初始化](cs/os/01-xi-tong-chu-shi-hua.md)
  * [进程管理](cs/os/02-jin-cheng-guan-li.md)
* [计算机网络](cs/network.md)
* [数据结构与算法](cs/algorithm/README.md)
  * [字符串](cs/algorithm/02-zi-fu-chuan.md)
  * [位运算](cs/algorithm/wei-yun-suan.md)
  * [动态规划](cs/algorithm/01-dong-tai-gui-hua.md)
  * [树](cs/algorithm/03-shu.md)
  * [题目列表](cs/algorithm/99-chang-jian-ti-mu-lie-biao.md)
  * [一些总结](cs/algorithm/algotrain/README.md)
    * [01-关于复杂度分析和渐进式优化](cs/algorithm/algotrain/01-guan-yu-fu-za-du-fen-xi-he-jian-jin-shi-you-hua.md)
    * [02-由一般化到特殊化演变的树](cs/algorithm/algotrain/02-you-yi-ban-hua-dao-te-shu-hua-yan-bian-de-shu.md)
    * [03-第一次全方位认识贪心算法](cs/algorithm/algotrain/03-di-yi-ci-quan-fang-wei-ren-shi-tan-xin-suan-fa.md)
    * [05-分治、回溯、贪心和动态规划](cs/algorithm/algotrain/05-fen-zhi-hui-su-tan-xin-he-dong-tai-gui-hua.md)
    * [06-Trie与并查集与高级搜索](cs/algorithm/algotrain/06trie-yu-bing-cha-ji-yu-gao-ji-sou-suo.md)
    * [07-排序算法总结](cs/algorithm/algotrain/07-pai-xu-suan-fa-zong-jie.md)
    * [08-字符串算法总结](cs/algorithm/algotrain/08-zi-fu-chuan-suan-fa-zong-jie.md)
    * [毕业总结](cs/algorithm/algotrain/bi-ye-zong-jie.md)

## 系统设计 <a id="system-design"></a>

* [软件设计](system-design/design/README.md)
  * [软件架构](system-design/design/jia-gou.md)
  * [编程范式](system-design/design/01-bian-cheng-fan-shi.md)
  * [系统设计题](system-design/design/99-she-ji-ti.md)
  * [设计原则](system-design/design/02-she-ji-yuan-ze.md)
  * [计算机程序的构造和解释 SICP](system-design/design/98-ji-suan-ji-cheng-xu-de-gou-zao-he-jie-shi.md)
* [领域驱动设计](system-design/ddd/README.md)
  * [应用：在线请假考勤管理](system-design/ddd/leave.md)
  * [应用: library](system-design/ddd/ying-yong-library.md)
* [微服务](system-design/wei-fu-wu.md)
* [分布式系统](system-design/distributed.md)
* [设计最佳实践](system-design/design-best-practice.md)
* [综合](system-design/case-study/README.md)
  * [开发实践](system-design/case-study/kai-fa-shi-jian.md)
  * [分布式锁](system-design/case-study/distributed-lock.md)
  * [分布式ID生成算法](system-design/case-study/distribute-id.md)
  * [通用的幂等设计](system-design/case-study/idempotent.md)
  * [交易系统](system-design/case-study/exchange-engine.md)

## 编程语言 <a id="language"></a>

* [编程语言](language/language.md)
* [Java](language/java/README.md)
  * [Java 核心技术](language/java/core-tech.md)
  * [Java 8 新特性](language/java/java8.md)
  * [Java 集合框架](language/java/java-collection.md)
  * [Java NIO](language/java/java-nio.md)
  * [并发编程](language/java/concurrent/README.md)
    * [三个线程交替打印](language/java/concurrent/interview-2.md)
    * [两个线程交替打印奇偶](language/java/concurrent/interview-3.md)
    * [优雅终止线程](language/java/concurrent/interview-4.md)
    * [等待通知机制](language/java/concurrent/interview-5.md)
    * [万能钥匙：管程](language/java/concurrent/interview-6.md)
    * [限流器](language/java/concurrent/interview-7.md)
    * [无锁方案 CAS](language/java/concurrent/interview-8.md)
* [Golang](language/golang.md)

## 框架/组件/类库 <a id="framework"></a>

* [Guava](framework/guava.md)
* [RxJava](framework/rxjava.md)
* [Apache MINA](framework/apache-mina.md)
* [Netty](framework/netty.md)
* [Dubbo](framework/dubbo.md)
* [Apache Tomcat](framework/apache-tomcat.md)
* [MyBatis](framework/mybatis.md)
* [Spring Framework](framework/spring-framework.md)
* [Spring Boot](framework/spring-boot.md)
* [Spring Cloud](framework/spring-cloud-netflix/README.md)
  * [Feign & OpenFeign](framework/spring-cloud-netflix/feign-and-openfeign.md)
  * [Ribbon](framework/spring-cloud-netflix/ribbon.md)
  * [Eurake](framework/spring-cloud-netflix/eurake.md)
  * [Spring Cloud Config](framework/spring-cloud-netflix/spring-cloud-config.md)
  * [Spring Cloud Alibaba](framework/spring-cloud-netflix/spring-cloud-alibaba.md)
* [FixJ](framework/fixj.md)
* [Metrics](framework/metrics.md)

## 中间件 <a id="middleware"></a>

* [Redis](middleware/redis.md)
* [MQ](middleware/mq/README.md)
  * [Kafka](middleware/mq/kafka.md)
  * [Pulsar](middleware/mq/pulsar.md)
  * [RocketMQ](middleware/mq/rocketmq.md)

## 大数据 <a id="bigdata"></a>

* [流计算](bigdata/stream.md)
* [Flink](bigdata/flink.md)

## 区块链 <a id="blockchain"></a>

* [区块链](blockchain/intro.md)
* [数字货币钱包](blockchain/shu-zi-huo-bi-qian-bao.md)

## 其他 <a id="other"></a>

* [工具](other/tools.md)
* [读书](other/reading/README.md)
  * [设计数据密集型应用](other/reading/ddia.md)
  * [实现领域驱动设计](other/reading/ddd-impl.md)
  * [精通比特币](other/reading/jing-tong-bi-te-bi.md)
* [论文](other/papers.md)
* [工程博客](other/blog.md)
* [阅读源码](other/yue-du-yuan-ma.md)
* [面试](other/interview.md)
* [软技能](other/soft-skill.md)
* [application](other/application/README.md)
  * [服务端开发](other/application/server.md)
  * [Reactor 模型](other/application/reactor.md)

