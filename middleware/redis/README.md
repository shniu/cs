# Redis

* [ ] [Redis 的 epoll 模型](https://xie.infoq.cn/article/628ae27da9ccb37d2900e8ef4)
* [ ] [Redis6 的多线程](https://xie.infoq.cn/article/91ab6a27e9bca957cab2d1819)
* [ ] [美团针对 Redis Rehash 机制的探索和实践](https://www.cnblogs.com/meituantech/p/9376472.html)
* [ ] [Redis6 的多线程IO处理过程详解](https://zhuanlan.zhihu.com/p/144805500)

### Redis 全景图

![](../../.gitbook/assets/image%20%2859%29.png)

学习 Redis 要从整体上有个宏观的理解，知道 Redis 都包含哪些东西，有哪些部分组成，出问题时可能是那部分影响的等

从系统维度理解，Redis 有很多知识需要掌握，比如 epoll 网络模型、run-to-complete 模型、简洁高效的线程模型等。

从应用维度上，场景驱动和案例驱动能更好的理解和掌握 Redis，如缓存和集群应用是 Redis 的两大广泛应用，每个应用背后都有一连串问题。

![](../../.gitbook/assets/image%20%2858%29.png)

根据这张全局的图，在遇到问题时就可以按图索骥进行分析，问题 -&gt; 主线 -&gt; 技术点，可以继续丰富这张图。

### Redis Checklist

--

### Redis Resource

#### Command

* [Redis 命令备忘录](https://cheatography.com/tasjaevan/cheat-sheets/redis/)

#### In Action

* [数据库攻略](https://time.geekbang.org/column/article/10301)
* \*\*\*\*[How Twitter Uses Redis To Scale - 105TB RAM, 39MM QPS, 10,000+ Instances ](http://highscalability.com/blog/2014/9/8/how-twitter-uses-redis-to-scale-105tb-ram-39mm-qps-10000-ins.html)
* [Awesome redis](https://github.com/JamzyWang/awesome-redis)

#### 博客

* [Redis: under the hood](https://www.pauladamsmith.com/articles/redis-under-the-hood.html#redis-under-the-hood)

#### 案例

* [Redis 响应变慢，如何解决](https://time.geekbang.org/column/article/78984)

#### Book

* [Redis 使用手册](https://weread.qq.com/web/reader/75732070719551157574079)



