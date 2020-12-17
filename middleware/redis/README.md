# Redis

### Redis 全景图

![](../../.gitbook/assets/image%20%2859%29.png)

学习 Redis 要从整体上有个宏观的理解，知道 Redis 都包含哪些东西，有哪些部分组成，出问题时可能是那部分影响的等

从系统维度理解，Redis 有很多知识需要掌握，比如 epoll 网络模型、run-to-complete 模型、简洁高效的线程模型等。

从应用维度上，场景驱动和案例驱动能更好的理解和掌握 Redis，如缓存和集群应用是 Redis 的两大广泛应用，每个应用背后都有一连串问题。

![](../../.gitbook/assets/image%20%2858%29.png)

根据这张全局的图，在遇到问题时就可以按图索骥进行分析，问题 -&gt; 主线 -&gt; 技术点，可以继续丰富这张图。

### Todos

* [ ] [Redis 的 epoll 模型](https://xie.infoq.cn/article/628ae27da9ccb37d2900e8ef4)
* [ ] [Redis6 的多线程](https://xie.infoq.cn/article/91ab6a27e9bca957cab2d1819)
* [ ] [美团针对 Redis Rehash 机制的探索和实践](https://www.cnblogs.com/meituantech/p/9376472.html)
* [ ] [Redis6 的多线程IO处理过程详解](https://zhuanlan.zhihu.com/p/144805500)
* [ ] [微博9年Redis优化之路](https://www.infoq.cn/article/bpdipUY0QM4ilMZbjbS1)
* [ ] 缓存究竟如何运作，才能接住百亿级明星热点的访问？[https://zhuanlan.zhihu.com/p/76394074](https://zhuanlan.zhihu.com/p/76394074)

### Redis Resource

#### 环境

* [http://redis.cn/](http://redis.cn/)
* [Redis Cluster 搭建流程](https://github.com/shniu/cloud-native-infrastructure/tree/master/middleware/redis-cluster)
* [Redis Cluster on Kubernetes 构建](https://github.com/shniu/cloud-native-infrastructure/tree/master/minikube-dev-env/resources/redis-cluster)
* [Redis 集群规范](http://redis.cn/topics/cluster-spec.html)

#### Command

* [Redis 命令备忘录](https://cheatography.com/tasjaevan/cheat-sheets/redis/)

#### In Action

* [数据库攻略](https://time.geekbang.org/column/article/10301)
* \*\*\*\*[How Twitter Uses Redis To Scale - 105TB RAM, 39MM QPS, 10,000+ Instances ](http://highscalability.com/blog/2014/9/8/how-twitter-uses-redis-to-scale-105tb-ram-39mm-qps-10000-ins.html)
* [Awesome redis](https://github.com/JamzyWang/awesome-redis)

#### 博客和专栏

* [Redis: under the hood](https://www.pauladamsmith.com/articles/redis-under-the-hood.html#redis-under-the-hood)
* [Redis 核心技术与实践](https://time.geekbang.org/column/article/268247)
* [Redis 深度历险](https://juejin.cn/book/6844733724618129422/section/6844733724660072461)

#### 源码阅读

* [https://github.com/shniu/redis/tree/5.0](https://github.com/shniu/redis/tree/5.0) by myself, 调试、验证等，一些自己验证某些问题写的 C 代码
* [Redis 源码分析](http://bbs.redis.cn/forum.php?mod=viewthread&tid=545)
* [oss-study/redis](https://github.com/oss-study/redis)
* [Redis 数据结构与实现](https://wingsxdu.com/post/database/redis/struct/#gsc.tab=0)

#### 案例

* [Redis 响应变慢，如何解决](https://time.geekbang.org/column/article/78984)
* [用 Redis 构建集群的最佳实践](https://time.geekbang.org/column/article/217590)
* [缓存的最佳实践系列](https://time.geekbang.org/column/article/149899)
* [https://github.com/JamzyWang/awesome-redis\#use-cases](https://github.com/JamzyWang/awesome-redis#use-cases)

#### Book

* [Redis 使用手册](https://weread.qq.com/web/reader/75732070719551157574079)
* [Redis 5 设计与源码分析](https://weread.qq.com/web/reader/d36322207190b923d368a9akc81322c012c81e728d9d180)   \(WIP\)
* [Redis 设计与实现](https://weread.qq.com/web/reader/d35323e0597db0d35bd957bkc81322c012c81e728d9d180)
* [Redis 开发与运维](https://weread.qq.com/web/reader/439327a0811e1aa5dg0166fd)



