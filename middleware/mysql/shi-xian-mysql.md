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

### 实现自己的 MySQL

#### MySQL 通信协议

MySQL 基本的通信流程

1. 客户端发起连接，进行 tcp 三次握手
2. 认证阶段
   1. 服务端向客户端发送 handshake packet
   2. 客户端处理 handshake，向服务端发送 auth packet
   3. 认证结果，OK packet or ERR packet
3. 命令执行阶段
   1. 客户端 -&gt; 服务端：发送命令包 \(Command Packet\)
   2. 服务端 -&gt; 客户端：发送回应包 \(OK Packet, or Error Packet, or Result Set Packet\)
4. 断开连接
   1. 客户端 -&gt; 服务端: 发送退出包
5. 四次握手断开 tcp 连接

#### MySQL 服务端实现

参考：

* [自己动手写 SQL 执行引擎](https://github.com/chaintechinfo/Freedom)
* [实现自己的数据库驱动](https://github.com/CallMeJiaGu/MySQL-Protocol) 
* [MySQL Protocol](https://github.com/sea-boat/mysql-protocol)
* [https://github.com/radondb/radon](https://github.com/radondb/radon)
* MySQL 协议 GO 实现：[https://github.com/pubnative/mysqlproto-go](https://github.com/pubnative/mysqlproto-go)
* GO MySQL Driver：[https://github.com/go-sql-driver/mysql](https://github.com/go-sql-driver/mysql)
* MySQL protocol library implementing in Go：[https://github.com/xelabs/go-mysqlstack](https://github.com/xelabs/go-mysqlstack)

