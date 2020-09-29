# 实现 MySQL

自己动手实现一个简版的 MySQL

### MVP

学习的最佳方式是理论结合实践，并输出给别人。这段时间学习了 MySQL 的一些内部工作机制和原理，有必要使用自己熟悉的编程语言实现一个简版的 MySQL，当然我们的目标不是做一个完全一摸一样的 MySQL，我们只是利用 MySQL 的核心实现原理，实现一个类似的数据库系统，可能的应用场景是将它作为内嵌数据库使用，比如在集成测试场景中。

一个 MVP 版本的 MySQL 应该包括哪些功能呢？

1. 需要支持基于 mysql 协议的客户端和服务端通信
2. 需要能够解析 SQL，然后根据命令进行操作，至少需要包括：create database, create table, drop database, drop table, select, insert, delete, update 等
3. 需要可靠的数据恢复机制
4. 需要支持事务特性

技术 Spike

* SQL Parser 引擎

参考 [阿里巴巴 druid SQL Parser 引擎](https://github.com/alibaba/druid/wiki/SQL-Parser)

* mysql 服务端实现

使用 Netty 作为网络通信库，实现 mysql 协议

* B+ Tree 实现



参考：

* [自己动手写 SQL 执行引擎](https://github.com/chaintechinfo/Freedom)
* [实现自己的数据库驱动](https://github.com/CallMeJiaGu/MySQL-Protocol) 
* [MySQL Protocol](https://github.com/sea-boat/mysql-protocol)

