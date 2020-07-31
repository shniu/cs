# MySQL

### 为什么会出现数据库？

也就是说数据库用来解决什么问题？针对任何领域，所面对的数据都在日益增多，怎么解决，唯一的办法就是做数据管理，平衡数据的增长和我们对数据本身的需求（如检索需求、更新需求等）。如何管理数据是一个比较难的问题

一些闲言碎语：

日常业务开发中会使用到数据库，作为一个可靠的持久化存储，在做存储架构的设计时，考虑的最多的是：怎么合理使用索引；怎么合理使用事务；会不会遇到锁的性能问题（比如会触发什么锁，表锁/行的读锁/行的写锁/间隙锁，会不会引入死锁，怎么提升并发更新的性能等）；应对什么样的并发量；应对什么样的数据量。后面两个问题更多的属于数据库架构层面的，需要综合业务发展的阶段和应对的业务量来判断

微观层面：弄清楚单个 MySQL 实例是如何管理数据、处理数据的，数据库启动后，可以接收库和表的创建、然后接受数据更新和数据检索，这个过程是怎么完成的？（引入了很多的设计方面的考虑）

宏观层面：数据持久层怎么应对高并发和海量数据存储，这是数据库架构层面的考量，可以使用读写分离的数据库架构，也可以使用分库分表的数据库架构，还可以使用缓存+数据库的架构，这个时候数据不再是单机处理了，就引入了分布式技术（也意味着引入了分布式的优点和缺点）

### MySQL 的基础架构与日志系统



![](../../.gitbook/assets/image%20%2832%29.png)



MySQL 的事务隔离级别如何实现的？

* 二进制日志 bin-log

对于**复制**来说，二进制日志在主复制服务器上用来记录要发送给从服务器的语句。二进制日志格式和处理的许多细节都是针对这个目的的。主服务器将其二进制日志中包含的事件发送给其从服务器，从服务器执行这些事件来进行与主服务器上相同的数据更改。从机将从主机上接收到的事件存储在其中继日志中，直到它们可以被执行。中继日志的格式与二进制日志相同。

某些**数据恢复**操作需要使用二进制日志。在恢复备份文件后，二进制日志中在备份后记录的事件将被重新执行。这些事件使数据库从备份点开始更新。

* Deep Into InnoDB Storage Engine

1. [https://dev.mysql.com/doc/internals/en/innodb.html](https://dev.mysql.com/doc/internals/en/innodb.html)
2. MySQL 技术内幕：InnoDB 存储引擎



### 高性能 MySQL

#### MVCC

大多数的存储引擎都不是简单的行级锁，基于提升并发性能的考虑，一般都同时实现了多版本并发控制MVCC，但是MVCC的实现并没有统一的标准；可以理解 MVCC 是行级锁的一个变种，但是在很多场景中它避免了加锁操作，因此开销要更低，大部分实现遵循：实现非阻塞的读，写操作只锁定必要的行。



表锁：S 锁 和 X 锁

### MySQL 资源列表

* [MySQL 技术内幕：InnoDB 存储引擎](https://weread.qq.com/web/reader/611329b059346e611427f1ckc81322c012c81e728d9d180)
* 数据库索引设计与优化 （网盘和本地）
* 高性能 MySQL
* [MySQL 官方手册](https://dev.mysql.com/doc/refman/5.7/en/)
* [How to analyze and tune MySQL query for better performance](https://www.mysql.com/cn/why-mysql/presentations/tune-mysql-queries-performance/)
* MySQL Internals Manual
* [MySQL 官方提供的示例数据 Employees](https://dev.mysql.com/doc/employee/en/employees-installation.html), 以及 [Employees 数据库的表结构关系](https://dev.mysql.com/doc/employee/en/sakila-structure.html)
* [数据库内核月报 - 淘宝](http://mysql.taobao.org/monthly/)
* 博客

  * [https://www.cnblogs.com/wxw16/p/6105624.html](https://www.cnblogs.com/wxw16/p/6105624.html) in 查询为什么慢
  * [https://juejin.im/post/5c2c53396fb9a04a053fc7fe](https://juejin.im/post/5c2c53396fb9a04a053fc7fe)
  * [https://tech.meituan.com/2014/06/30/mysql-index.html](https://tech.meituan.com/2014/06/30/mysql-index.html) 重点看

* 分析工具
  * [innodb\_ruby](https://github.com/jeremycole/innodb_ruby/wiki)  A parser for InnoDB file formats 



