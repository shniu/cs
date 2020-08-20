---
description: 分析 MySQL 的 InnoDB 存储引擎的索引机制
---

# MySQL Index

索引是一种可以提升查询效率的数据结构\(**索引的本质是数据结构, 目的是提高查询效率**\)。

MySQL 的 InnoDB 存储引擎是使用 B+ Tree 来组织数据的，也就是说 B+ Tree 就是 InnoDB 使用的索引数据结构。

先来搞明白一个事情，为什么查询数据要借助数据库和数据库的索引？通常情况下，我们可以使用操作系统提供的 awk / grep 等来查询数据，顺序遍历文件中的每一行数据，直到找到为止；当数据量比较小时，比如几十兆、几百兆甚至上 GB，这种方式也许还可以应付，但是更多的数据量呢？我们会面临一些问题：

1. 计算机磁盘存在 IO 性能问题，如果把要搜索的数据都加载到内存，是需要时间的
2. 普通的做法需要对数据做全量扫描，全量意味着随着数据量的增长，查询速度会越来越慢
3. 一些常识：a、磁盘读取数据要寻址，要花费几ms级别，当然顺序读写就不需要额外的寻址时间；b、 磁盘的IO，也就是吞吐量，一般的磁盘吞吐在几百M每秒；c、 和内存相比，内存的操作时间是 100纳秒左右，差了 10万倍左右，当然ssd硬盘会更快一点，也会相差几万倍

总结一下，普通的原始做法是把数据顺序存储在文件中，等需要的时候，从头到尾全量遍历直到找到满足要求的数据，**缺点就是需要做全量扫描的IO操作，而且还不支持丰富多样的过滤查询，一个字“慢“，无法应对日益增长的数据量。**而数据库管理系统提供了一种解决方案。

> A database needs to do two things: when you give it some data, it should store the data, and when you ask it again later, it should give the data back to you.  
>
>  -- from &lt;&lt; Designing Data-Intensive Applications&gt;&gt;

怎么解决呢？应对海量数据的问题，就是利用**分治思想**，比如说操作系统管理文件系统的方式，磁盘被分成很多的数据块，每个数据块 4 KB 大小（有的是16 K），一个文件就可以被分成很多个 4 K 大小的数据页，实际的数据就被存放在这些数据页中；但是一般磁盘都非常大，比如说一个 1 TB 的磁盘就会有 2 亿个左右的数据页，**维护这么大的数据页就需要借助索引来完成**，建立数据页的索引，用来标识哪个数据存在哪个数据页里，索引就是来加速查找的数据结构，索引也是文件，叫索引文件，**我们可以建立两级索引，第一级索引在内存中，第二级索引在磁盘上，在查询时，首先获取二级索引，然后根据二级索引加载数据页，最后取出数据**。这样即避免了数据的全量IO（因为只加载部分数据），又提高了查询速度。

实质上，InnoDB 的 B+ Tree 索引实现利用了类似的思想：分治 + 多级索引 + 顺序磁盘扫描 + 少量的随机磁盘扫描。

### [B+ Tree](https://zh.wikipedia.org/wiki/B%2B%E6%A0%91) 

为什么选择使用 B+ Tree 来做数据库存储引擎的索引呢？涉及到：局部性原理，文件系统和磁盘的管理，数据结构的复杂度分析（能够应对高效查询的数据结构有哪些？avl / 红黑树 / hash 表 / 跳表），参考 [一般化到特殊化演变的树](../../cs/algorithm/algotrain/02-you-yi-ban-hua-dao-te-shu-hua-yan-bian-de-shu.md#wei-shen-me-hui-you-na-me-duo-te-shu-hua-de-shu)；B-Tree 和 B+Tree 是结合了磁盘访问特性和平衡二叉搜索树的满足特定需求的树结构，特别适合做大量数据的存储。

B+ Tree 的定义

> 1. 节点中的数据索引从左到右递增排列；
> 2. 非叶子节点不存储data，只存储索引\(冗余\)，可以放更多的索引 
> 3. 叶子节点包含所有索引字段 
> 4. 叶子节点用指针连接，提高区间访问的性能

InnoDB 如何使用 B+ Tree 来组织数据，又是如何结合磁盘的局部性原理和顺序读写呢？

一般来说，索引本身也很大，不可能全部存储在内存中，因此索引往往以索引文件的形式存储的磁盘上。这样的话，索引查找过程中就要产生磁盘I/O消耗，相对于内存存取，I/O存取的消耗要高几个数量级，所以评价一个数据结构作为索引的优劣最重要的指标就是在查找过程中磁盘I/O操作次数的渐进复杂度。换句话说，索引的结构组织要尽量减少查找过程中磁盘I/O的存取次数。

小问题：

* 为什么 InnoDB 的表必须有主键，并且推荐使用整型的自增主键？（**首先非聚簇索引需要引用聚簇索引的id，需要有个主键来被引用；其次，如果没有主键如何组织索引树呢，必须得需要一个载体来组织索引和数据。使用自增主键，是考虑到B+树的特点和局部性原理，叶子节点存放的是数据，如果是随机的值作为主键，在插入数据时有可能会频繁发生页分裂，或页利用率低的问题，即降低了性能也浪费了一定空间; 利用顺序读写效率高于随机读写；**）
* 为什么非主键索引结构叶子节点存储的是主键值？（**因为数据一致性的考虑，如果有多份数据拷贝，维护多个索引树之间的数据一致性就变得比较麻烦；还有节省存储空间的考虑**）

### 索引选择与优化

使用的表和数据是 MySQL 官方提供的 [employees 示例数据](https://github.com/datacharmer/test_db)



索引分析的两大利器

* explain 执行计划
* [trace 分析工具](http://mysql.taobao.org/monthly/2019/11/03/)



结论：

* InnoDB 以数据页来读取数据，默认 16KB
* change buffer，是在内存中的一块特殊的内存区域，用来缓存更新操作，目的是提高更新性能（因为change buffer 能够减少数据页的随机 IO，然后利用后台线程定期merge，merge时一般是会尽可能利用数据页的顺序 IO）；change buffer 在写多读少的业务效果会更好，比如账单类、日志类的系统；

1. 尽量使用普通索引，不是说不能使用唯一索引，关键是要思考真的需要唯一索引吗
2. 如果是写后立即读的场景比较频繁，应该关闭change buffer
3. 对于非常少出现写后立即读的场景，change buffer 可以提升性能，特别是数据量大的表；对于日志类的这类应用，可以把 change buffer 调大一些

* 重建索引

```sql
-- 重建普通索引
alter table T drop index k;
alter table T add index(k);

-- 重建主键索引
alter table T engine = InnoDB;
```

* 索引并不总是最好的工具，只有当索引帮助存储引擎快速查找记录带来的好处大于其带来的额外工作时，索引才有效。对于非常小的表，大部分情况下简单的全表扫描更高效。对于中到大型的表，索引就非常有效。但对于特大型的表，建立和使用索引的代价将随之增长。
* 尽量在InnoDB上采用自增字段做主键
* 关于索引最左前缀，我们会建立多列的联合索引

1. 全列匹配，就是精确查找联合索引中的所有列
2. 最左前缀匹配（和where条件中列出现的顺序无关），精确匹配联合索引中最左前缀
3. 查询条件没有指定索引第一列，不会命中索引
4. 匹配前缀字符串，比如索引中使用了 like 'abc%'，可以命中索引，但是 like '%abc', 无法命中索引
5. 范围查询，可以命中最左前缀的索引，范围后面的列无法命中索引
6. 查询条件中有函数或表达式无法命中索引，比如 `select * from sakila.actor where actor_id + 1 = 5` 无法命中索引；还有就是 `select * from t where to_days(current_date) - to_days(current_date) <= 10` 

* 索引选择性

> 既然索引可以加快查询速度，那么是不是只要是查询语句需要，就建上索引？答案是否定的。因为索引虽然加快了查询速度，但索引也是有代价的：索引文件本身要消耗存储空间，同时索引会加重插入、删除和修改记录时的负担，另外，MySQL在运行时也要消耗资源维护索引，因此索引并不是越多越好
>
> 索引选择性是指不重复的索引值和数据表总记录数之间的比值，越接近1，选择性越好

1. 记录数非常少的表可以考虑不建立索引，做全表扫描也没有多大问题，比如经验值 2000
2. 选择性比较低的列，可以考虑不建索引；选择性是指不重复的索引值，基数和表记录数的比值（Index Selectivity = Cardinality / \#T）
3. 一般情况下，使用前缀索引也是足以满足查询性能的。对于 BLOB、Text和很长的 Varchar 类型，必须使用前缀索引，

一个例子，`employees` 表

![employees&#x7684;&#x8868;&#x7ED3;&#x6784;](../../.gitbook/assets/image%20%2833%29.png)



![employees &#x7684;&#x7D22;&#x5F15;](../../.gitbook/assets/image%20%2837%29.png)



```text
mysql> EXPLAIN SELECT * FROM employees.employees WHERE first_name='Eric' AND last_name='Anido';
+----+-------------+-----------+------------+------+---------------+------+---------+------+--------+----------+-------------+
| id | select_type | table     | partitions | type | possible_keys | key  | key_len | ref  | rows   | filtered | Extra       |
+----+-------------+-----------+------------+------+---------------+------+---------+------+--------+----------+-------------+
|  1 | SIMPLE      | employees | NULL       | ALL  | NULL          | NULL | NULL    | NULL | 299556 |     1.00 | Using where |
+----+-------------+-----------+------------+------+---------------+------+---------+------+--------+----------+-------------+
1 row in set, 1 warning (0.00 sec)
```

![&#x4F7F;&#x7528; first\_name &#x548C; last\_name &#x67E5;&#x8BE2;](../../.gitbook/assets/image%20%2831%29.png)

使用选择性来决定怎么建索引

```text
mysql> select count(distinct(first_name)) / count(1) as Selectivity from employees;
+-------------+
| Selectivity |
+-------------+
|      0.0042 |
+-------------+
1 row in set (0.20 sec)
```

![first\_name &#x7684;&#x9009;&#x62E9;&#x6027;](../../.gitbook/assets/image%20%2834%29.png)



```text
mysql> select count(distinct(concat(first_name,last_name))) / count(1) as Selectivity from employees;
+-------------+
| Selectivity |
+-------------+
|      0.9313 |
+-------------+
1 row in set (0.60 sec)
```

![first\_name &#x548C; last\_name &#x7684;&#x9009;&#x62E9;&#x6027;](../../.gitbook/assets/image%20%2836%29.png)

first\_name 和 last\_name 的长度之和为 30，索引长度能不能小一点？

取 last\_name 的前缀

```text
mysql> select count(distinct(concat(first_name,left(last_name,3)))) / count(1) as Selectivity from employees;
+-------------+
| Selectivity |
+-------------+
|      0.7879 |
+-------------+
1 row in set (0.54 sec)
```

![](../../.gitbook/assets/image%20%2835%29.png)

```text
alter table employees add index `idx_first_last4` (`first_name`, last_name(4));

mysql> explain SELECT * FROM employees.employees WHERE first_name='Eric' AND last_name='Anido';
+----+-------------+-----------+------------+------+-----------------+-----------------+---------+-------------+------+----------+-------------+
| id | select_type | table     | partitions | type | possible_keys   | key             | key_len | ref         | rows | filtered | Extra       |
+----+-------------+-----------+------------+------+-----------------+-----------------+---------+-------------+------+----------+-------------+
|  1 | SIMPLE      | employees | NULL       | ref  | idx_first_last4 | idx_first_last4 | 76      | const,const |    1 |   100.00 | Using where |
+----+-------------+-----------+------------+------+-----------------+-----------------+---------+-------------+------+----------+-------------+
1 row in set, 1 warning (0.00 sec)

-- 加索引前
mysql> SELECT * FROM employees.employees WHERE first_name='Eric' AND last_name='Anido';
+--------+------------+------------+-----------+--------+------------+
| emp_no | birth_date | first_name | last_name | gender | hire_date  |
+--------+------------+------------+-----------+--------+------------+
|  18454 | 1955-02-28 | Eric       | Anido     | M      | 1988-07-18 |
+--------+------------+------------+-----------+--------+------------+
1 row in set (0.09 sec)

-- 加索引后
mysql> SELECT * FROM employees.employees WHERE first_name='Eric' AND last_name='Anido';
+--------+------------+------------+-----------+--------+------------+
| emp_no | birth_date | first_name | last_name | gender | hire_date  |
+--------+------------+------------+-----------+--------+------------+
|  18454 | 1955-02-28 | Eric       | Anido     | M      | 1988-07-18 |
+--------+------------+------------+-----------+--------+------------+
1 row in set (0.00 sec)
```

```sql
-- profiling 是否开启
mysql> show variables like 'profiling';
+---------------+-------+
| Variable_name | Value |
+---------------+-------+
| profiling     | OFF   |
+---------------+-------+
1 row in set (0.00 sec)

-- 开启
set profiling = 1;

-- 执行sql

-- 查看执行时间
mysql> show profiles;
+----------+------------+---------------------------------------------------------------------------------+
| Query_ID | Duration   | Query                                                                           |
+----------+------------+---------------------------------------------------------------------------------+
|        1 | 0.00047900 | SELECT * FROM employees.employees WHERE first_name='Eric' AND last_name='Anido' |
+----------+------------+---------------------------------------------------------------------------------+
1 row in set, 1 warning (0.00 sec)

-- 查看某个sql的 cpu/io/memory/swaps/context switch/source 等使用情况
mysql> show profile cpu, block io, memory,swaps,context switches,source for query 1;
+----------------------+----------+----------+------------+-------------------+---------------------+--------------+---------------+-------+-----------------------+----------------------+-------------+
| Status               | Duration | CPU_user | CPU_system | Context_voluntary | Context_involuntary | Block_ops_in | Block_ops_out | Swaps | Source_function       | Source_file          | Source_line |
+----------------------+----------+----------+------------+-------------------+---------------------+--------------+---------------+-------+-----------------------+----------------------+-------------+
| starting             | 0.000087 | 0.000055 |   0.000008 |                 0 |                   0 |            0 |             0 |     0 | NULL                  | NULL                 |        NULL |
| checking permissions | 0.000007 | 0.000004 |   0.000003 |                 0 |                   0 |            0 |             0 |     0 | check_access          | sql_authorization.cc |         809 |
| Opening tables       | 0.000014 | 0.000013 |   0.000001 |                 0 |                   0 |            0 |             0 |     0 | open_tables           | sql_base.cc          |        5781 |
| init                 | 0.000035 | 0.000030 |   0.000006 |                 0 |                   0 |            0 |             0 |     0 | handle_query          | sql_select.cc        |         128 |
| System lock          | 0.000015 | 0.000008 |   0.000005 |                 0 |                   0 |            0 |             0 |     0 | mysql_lock_tables     | lock.cc              |         330 |
| optimizing           | 0.000014 | 0.000011 |   0.000004 |                 0 |                   0 |            0 |             0 |     0 | optimize              | sql_optimizer.cc     |         158 |
| statistics           | 0.000128 | 0.000084 |   0.000036 |                 0 |                   2 |            0 |             0 |     0 | optimize              | sql_optimizer.cc     |         374 |
| preparing            | 0.000017 | 0.000013 |   0.000004 |                 0 |                   0 |            0 |             0 |     0 | optimize              | sql_optimizer.cc     |         482 |
| executing            | 0.000003 | 0.000002 |   0.000002 |                 0 |                   0 |            0 |             0 |     0 | exec                  | sql_executor.cc      |         126 |
| Sending data         | 0.000062 | 0.000048 |   0.000014 |                 0 |                   0 |            0 |             0 |     0 | exec                  | sql_executor.cc      |         202 |
| end                  | 0.000005 | 0.000003 |   0.000002 |                 0 |                   0 |            0 |             0 |     0 | handle_query          | sql_select.cc        |         206 |
| query end            | 0.000007 | 0.000006 |   0.000001 |                 0 |                   0 |            0 |             0 |     0 | mysql_execute_command | sql_parse.cc         |        4956 |
| closing tables       | 0.000007 | 0.000005 |   0.000002 |                 0 |                   0 |            0 |             0 |     0 | mysql_execute_command | sql_parse.cc         |        5009 |
| freeing items        | 0.000040 | 0.000023 |   0.000016 |                 0 |                   0 |            0 |             0 |     0 | mysql_parse           | sql_parse.cc         |        5622 |
| cleaning up          | 0.000038 | 0.000015 |   0.000020 |                 0 |                   1 |            0 |             0 |     0 | dispatch_command      | sql_parse.cc         |        1931 |
+----------------------+----------+----------+------------+-------------------+---------------------+--------------+---------------+-------+-----------------------+----------------------+-------------+
15 rows in set, 1 warning (0.00 sec)

mysql> show profile for query 1;
+----------------------+----------+
| Status               | Duration |
+----------------------+----------+
| starting             | 0.000087 |
| checking permissions | 0.000007 |
| Opening tables       | 0.000014 |
| init                 | 0.000035 |
| System lock          | 0.000015 |
| optimizing           | 0.000014 |
| statistics           | 0.000128 |
| preparing            | 0.000017 |
| executing            | 0.000003 |
| Sending data         | 0.000062 |
| end                  | 0.000005 |
| query end            | 0.000007 |
| closing tables       | 0.000007 |
| freeing items        | 0.000040 |
| cleaning up          | 0.000038 |
+----------------------+----------+
15 rows in set, 1 warning (0.00 sec)
```

#### 

#### 参考资料

* [MySQL 索引背后的数据结构和算法原理](http://blog.codinglabs.org/articles/theory-of-mysql-index.html)
* [MySQL 索引原理及查询优化](https://tech.meituan.com/2014/06/30/mysql-index.html)

