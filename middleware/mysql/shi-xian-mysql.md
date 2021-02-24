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
   2. 服务端断开连接
5. 四次握手断开 tcp 连接

MySQL 的客户端和服务端通信使用了 CHAP \(Challenge Handshake Authentication Protocol\) 挑战握手协议，流程如下：

![MySQL &#x5BA2;&#x6237;&#x7AEF;&#x548C;&#x670D;&#x52A1;&#x7AEF;&#x5EFA;&#x7ACB;&#x8FDE;&#x63A5;&#x4E0E;&#x8BA4;&#x8BC1;&#x8FC7;&#x7A0B;](../../.gitbook/assets/image%20%2868%29.png)

什么是挑战握手协议？CHAP 是在网络物理连接后进行连接安全性验证的协议。基本步骤如下：

1. 链路建立阶段结束之后，认证者向对端点发送“challenge”消息。
2. 对端点用经过单向哈希函数计算出来的值做应答。
3. 认证者根据它自己计算的哈希值来检查应答，如果值匹配，认证得到承认；否则，连接应该终止。
4. 经过一定的随机间隔，认证者发送一个新的 challenge 给端点，重复步骤 1 到 3 。



```text
// MySQL 的通信协议：4 bytes header + n bytes payload(body)
|--- 3 bytes ---|--- 1 byte ---|--- n bytes ---|
  length of msg   sequence id       payload
```

![HandShake &#x5305;](../../.gitbook/assets/image%20%2867%29.png)

协议包中的 capability flags 有特殊的用途，The capability flags are used by the client and server to indicate which features they support and want to use. MySQL 的 capability : 在认证握手过程中，客户端和服务器交换了关于对方能够或愿意做什么的信息。这使他们能够调整对同行的期望，而不是以某种不支持的格式发送数据。信息的交换是通过包含协议能力的位掩码的字段来完成的。

server status flag: [https://dev.mysql.com/doc/internals/en/status-flags.html](https://dev.mysql.com/doc/internals/en/status-flags.html)

可见，MySQL 的通信包是使用长度前缀法来定位每个包的边界，处理粘包问题时使用长度截取，粘包的本质是在tcp层发送数据时，大于一个包大小的会拆成多个包，小于一个包大小的会组合在一起。

mysql 的包和 OS 层的 TCP 包的关系：MySQL Packet 属于应用层包，会被封装在 TCP 的数据包中，MySQL Packet 有可能被放在一个 TCP 数据包中，也有可能跨多个；有时甚至几个 MySQL Packet 在同一个 TCP 数据包中

参考：

* [MySQL 通信协议](https://jin-yang.github.io/post/mysql-protocol.html)
* [Client / Server Communication](https://www.oreilly.com/library/view/understanding-mysql-internals/0596009577/ch04.html)
* [MySQL 协议 HandShake 握手篇](https://cloud.tencent.com/developer/article/1184391)
* [https://github.com/chaintechinfo/MySQL-Protocol](https://github.com/chaintechinfo/MySQL-Protocol) 动手实现 MySQL 协议

#### MySQL 客户端

MySQL 客户端实现和服务端的通信，将客户端需要执行的各种操作发给服务端，比较重要的是：

1. 实现 MySQL 的通信协议
2. 高性能的网络模型，如使用类似于 Netty 的 Reactor 网络模型等

#### MySQL 服务端实现

* 连接管理器处理网络连接，解析命令，命令路由
* SQL 解析器，语法分析
* 查询优化器
* 执行器
* 存储引擎抽象和默认引擎实现

参考：

* [自己动手写 SQL 执行引擎](https://github.com/chaintechinfo/Freedom)
* [实现自己的数据库驱动](https://github.com/CallMeJiaGu/MySQL-Protocol) 
* [MySQL Protocol](https://github.com/sea-boat/mysql-protocol)
* [https://github.com/radondb/radon](https://github.com/radondb/radon)
* MySQL 协议 GO 实现：[https://github.com/pubnative/mysqlproto-go](https://github.com/pubnative/mysqlproto-go)
* GO MySQL Driver：[https://github.com/go-sql-driver/mysql](https://github.com/go-sql-driver/mysql)
* MySQL protocol library implementing in Go：[https://github.com/xelabs/go-mysqlstack](https://github.com/xelabs/go-mysqlstack)

