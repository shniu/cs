# MySQL 内核

## 内核

* \*\*\*\*[**MySQL · 引擎特性 · InnoDB 崩溃恢复过程**](http://mysql.taobao.org/monthly/2015/06/01/)\*\*\*\*
* \*\*\*\*[**MySQL · 引擎特性 · InnoDB 事务子系统介绍**](http://mysql.taobao.org/monthly/2015/12/01/)\*\*\*\*
* \*\*\*\*[**MySQL · 引擎特性 · The design of mysql8.0 redolog**](http://mysql.taobao.org/monthly/2019/02/05/)\*\*\*\*
* \*\*\*\*[**MySQL · 引擎特性 · Innodb change buffer介绍**](http://mysql.taobao.org/monthly/2015/07/01/)\*\*\*\*
* \*\*\*\*[**MySQL · 引擎特性 · InnoDB 文件系统之文件物理结构**](http://mysql.taobao.org/monthly/2016/02/01/)\*\*\*\*
* \*\*\*\*[**MySQL · 引擎特性 · InnoDB 文件系统之IO系统和内存管理**](http://mysql.taobao.org/monthly/2016/02/02/)\*\*\*\*
* \*\*\*\*[**MySQL · 原理介绍 · 再议MySQL的故障恢复**](http://mysql.taobao.org/monthly/2018/12/04/) **要读**
* \*\*\*\*[**Database · 原理介绍 · 数据库的事务与复制**](http://mysql.taobao.org/monthly/2018/12/01/)\*\*\*\*
* \*\*\*\*[**Database · 原理介绍 · Snapshot Isolation 综述**](http://mysql.taobao.org/monthly/2019/02/03/)\*\*\*\*
* \*\*\*\*[**MySQL · 最佳实践 · MySQL中的IO共享操作**](http://mysql.taobao.org/monthly/2019/01/09/)\*\*\*\*
* \*\*\*\*[**MySQL · 最佳实践 · MySQL多队列线程池优化**](http://mysql.taobao.org/monthly/2019/02/09/)\*\*\*\*
* \*\*\*\*[**MtSQL Redo log**](http://mysql.taobao.org/monthly/2019/03/03/)\*\*\*\*
* \*\*\*\*[**MySQL 事务锁概述**](http://mysql.taobao.org/monthly/2016/01/01/)\*\*\*\*
* \*\*\*\*[**MySQL · 答疑解惑 · 物理备份死锁分析**](http://mysql.taobao.org/monthly/2016/01/05/)\*\*\*\*
* \*\*\*\*[**MySQL · 答疑解惑 · MySQL 锁问题最佳实践**](http://mysql.taobao.org/monthly/2016/03/10/)\*\*\*\*
* \*\*\*\*[**MySQL · 答疑解惑 · 备库Seconds\_Behind\_Master计算**](http://mysql.taobao.org/monthly/2016/03/09/)\*\*\*\*
* \*\*\*\*[**MySQL · TokuDB · Cachetable 的工作线程和线程池**](http://mysql.taobao.org/monthly/2016/01/06/)\*\*\*\*
* \*\*\*\*[**MySQL · 特性分析 · 线程池**](http://mysql.taobao.org/monthly/2016/02/09/)\*\*\*\*
* \*\*\*\*

## InnoDB

### InnoDB 基本概念

#### 表空间

InnoDB 的存储模型使用的是表空间（也被称做文件空间）；空间由实际的操作系统层级的多个文件组成，比如 ibdata1, ibdata2 etc. 这些文件组成了一个独立的逻辑文件；每一个表空间被分配一个 32-bit 的整型 space Id，用来指向不同的表空间；InnoDB 默认存在一个 space id 为 0 的默认表空间，也叫系统表空间，系统表空间用来满足 InnoDB 的各种需求，比如 undo log, double write buffer etc.，此外 InnoDB 支持每个表的表空间，也就是一个表的数据可以放在一个单独的表空间中，多个表之间不相互影响；

#### 页

每个表空间被切分成若干页，每个页的大小通常是 16 KB（也可以修改，做了空间压缩会有变化等）；每个页会被分配一个 32-bit 的页码，通常称做 offset （偏移量），比如 page0 的偏移量是 0，page1 的偏移量是 16384。表空间大小的限制是 64TB，\(2^32 \* 16 KB = 64TB\)

页的大致组成：

![Page Overview](../../.gitbook/assets/image%20%2852%29.png)

Page Type: 页被分配给文件空间管理、范围管理、事务系统、数据字典、撤销日志、blobs，当然还有索引（表数据）

#### 空间文件

1个空间文件是许多页的连接。为了更有效的管理，页被分组成为 1MB 的块（64个连续页面，默认页大小16KB），并称为 extent \(区\), 很多结构就只引用extents来分配空间内的页面；InnoDB需要做一些记账工作来跟踪所有的页面、extents和空间本身，所以一个空间文件有一些强制性的上层结构

#### 系统表空间



### InnoDB 相关资料

* [The basics of InnoDB space file layout](https://blog.jcole.us/2013/01/03/the-basics-of-innodb-space-file-layout/)
* MySQL 文档：[InnoDB 存储引擎](https://dev.mysql.com/doc/refman/5.7/en/innodb-storage-engine.html)

