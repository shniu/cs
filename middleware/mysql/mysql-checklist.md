---
description: 可以用作开发过程中的数据库的 Checklist 的实践
---

# MySQL Checklist

#### **关于连接**

* 一个用户成功建立连接后，即使你用管理员账号对这个用户的权限做了修改，也不会影响已经存在连接的权限。修改完成后，只有再新建的连接才会使用新的权限设置；可以使用 `show processlist` 查看相关信息； `wait_timeout` 参数用来控制某个连接的最大空闲时间；

```text
mysql> show variables like 'wait_timeout';
+---------------+-------+
| Variable_name | Value |
+---------------+-------+
| wait_timeout  | 28800 |
+---------------+-------+
1 row in set (0.06 sec)


// 试图使用一个已经断开的连接发送请求，会收到 Lost connection to MySQL server during query.
```

* 数据库里面，长连接是指连接成功后，如果客户端持续有请求，则一直使用同一个连接。短连接则是指每次执行完很少的几次查询就断开连接，下次查询再重新建立一个。建立连接的过程通常是比较复杂的，所以在使用中要尽量减少建立连接的动作，也就是尽量使用长连接；一般在客户端会使用数据库连接池。但是有时候也需要注意，MySQL 临时使用的内存是管理在连接的内存对象中的，如果某个连接长期存在，而且做过一些比较大的操作，就会占用比较多的内存，如果这样的连接积累的越多，内存占用的就会越多，有时候还会引起连接异常中断，可以：每过一段时间连接断开后重连，尤其是做个大的操作；另外就是使用 `mysql_reset_connection`

#### **关于索引**

* 每个表都会有一个主键，当指定主键时就用指定的，没有指定就会找第一个出现的唯一索引，如果不存在唯一索引，会使用一个默认的6字节大小的 \_rowid 作为主键
* limit 优化

```sql
// limit 优化
select * from account limit 1000, 10;
```

上面的 SQL 在执行时，会丢掉扫描数据的前 10000 条记录，返回 10000 ~ 10010 的数据，这样的代价非常高；如何优化？

```sql
///// 优化1
select * from account limit 1000, 10;
// 首先是以主键进行排序的，那么每次返回都是有一个最大的id的，比如是 100

// SQL 改写为
select * from account where id > 100 limit 10;
// 最大的id返回 120，那么下次的查询为
select * from account where id > 120 limit 10;
```

#### 关于隔离级别

* 隔离级别的设置对当前连接有效，比如 JDBC，每个 Connection 都可以设置自己的事务隔离级别，而不会影响其他的连接；其实也是针对每个 Session 都可以独立设置自己的隔离级别；MySQL 提供了全局的隔离级别配置，在创建连接或者Session时不指定隔离级别，默认就是全局的隔离级别（MySQL 默认 REPEATABLE-READ
* MySQL 的每个事务都会有一个事务id，事务id是严格自增的，是在事务开启的时候向事务系统申请的；
* 可重复读的核心是一致性读\(consistent read\)，事务更新数据的时候，只能用当前读；如果当前的记录的行锁被其他事务占用的话，就需要进入锁等待；
* MySQL 在默认情况下，是使用一致性的非锁定读；只有指定使用一致性的锁定读时才会对记录加锁；然后在不同隔离级别下的一致性非锁定读，其结果是不一样的，对于 READ COMMITED 隔离级别，每次读到的都是当前能够看到的最新的快照（已提交的事务产生的）；对于 REPETABLE READ , 读取的是事务启动时创建的一致性视图

#### 关于并发

* [并发控制](http://mysql.taobao.org/monthly/2014/09/05/)

#### 关于其他

* 使用 InnoDB 存储引擎强烈推荐使用一个自增主键，如果不指定主键，会自动使用一个自增的整型 `_rowid` 作为主键\( 一般 6 字节，是所有的没有指定主键的表共用的\)，[参考这里](http://mysql.taobao.org/monthly/2014/09/06/)

`_rowid` 是全局共享的，并使用 `mutex` 锁进行保护，所以在高并发下会引发锁竞争，降低效率，故推荐使用 `auto_increment` 。`auto_increment` 是表级别的，不会产生数据库级别的竞争；由于`auto_increment`的顺序性，减少了随机读的可能，保证了写入的page的缓冲命中。（不可否认，写入的并发足够大时，会产生热点块的争用）

#### 关于 MySQL 使用

* [MySQL 的隐式类型转换引起的问题总结](https://blog.csdn.net/HaHa_Sir/article/details/93666147), [另外一篇](https://www.guitu18.com/post/2019/11/24/61.html)

```text
当操作符左右两边的数据类型不一致时，会发生隐式转换。
当where查询操作符左边为数值类型时发生了隐式转换，那么对效率影响不大，但还是不推荐这么做。
当where查询操作符左边为字符类型时发生了隐式转换，那么会导致索引失效，造成全表扫描效率极低。
字符串转换为数值类型时，非数字开头的字符串会转化为0，以数字开头的字符串会截取从第一个字符到第一个非数字内容为止的值为转化结果。
```

* rows\_examined就是“server层调用引擎取一行的时候”加1
* 引擎内部自己调用，读取行，不加1



