# 优化

### 查询

#### count 优化

![count &#x7684;&#x5DE5;&#x4F5C;&#x8FC7;&#x7A0B;&#xFF08;&#x7B80;&#x7248;&#xFF09;](../../.gitbook/assets/image%20%2846%29.png)

在使用 count 需要注意的是：一个优化点是在数据量大的时候，可以找一个字段长度相对较小的列做一个二级索引，这样 mysql 在统计的时候加载的数据量更小，IO需要的时间就更少；count 不计算 NULL 值

更进一步的计数优化，需要在架构上做更多的设计



![](../../.gitbook/assets/image%20%2840%29.png)

参考：

1. [select count 底层做了什么？](https://zhuanlan.zhihu.com/p/71333492)
2. [https://www.zhihu.com/question/34781415](https://www.zhihu.com/question/34781415)



#### Order By 优化

```sql
CREATE TABLE `user_profile` (
  `id` int(11) NOT NULL,
  `city` varchar(16) NOT NULL,
  `name` varchar(16) NOT NULL,
  `age` int(11) NOT NULL,
  `addr` varchar(128) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `city` (`city`)
) ENGINE=InnoDB;

-- 查询某个城市按姓名排名的前1000个人的城市、姓名和年龄
select city, name, age from user_profile where city = '上海' order by name limit 1000;

explain select city, name, age from user_profile where city = '上海' order by name limit 1000;
+----+-------------+--------------+------------+------+---------------+------+---------+-------+------+----------+---------------------------------------+
| id | select_type | table        | partitions | type | possible_keys | key  | key_len | ref   | rows | filtered | Extra                                 |
+----+-------------+--------------+------------+------+---------------+------+---------+-------+------+----------+---------------------------------------+
|  1 | SIMPLE      | user_profile | NULL       | ref  | city          | city | 66      | const |    1 |   100.00 | Using index condition; Using filesort |
+----+-------------+--------------+------------+------+---------------+------+---------+-------+------+----------+---------------------------------------+
```

* 简单条件的 order by

`select city, name, age from user_profile where city = '上海' order by name limit 1000` 是如何工作的？

![Order by](../../.gitbook/assets/image%20%2851%29.png)

![&#x4F18;&#x5316;&#x65B9;&#x5411;](../../.gitbook/assets/image%20%2838%29.png)

要点：**max\_length\_for\_sort\_data / sort\_buffer\_size / number\_of\_tmp\_files / examined rows / using filesort / 归并排序 / 快速排序 / 堆排序 / sort mode**

如果内存够，就要多利用内存，尽量减少磁盘访问。在 sort\_buffer\_size 足够的情况下，会避免使用外部排序（外部排序是借助多个临时的磁盘文件进行排序，把排好序的数据放入多个临时的小文件，然后将有序的小文件依次按序放入内存，直到满足条件为止）

* 稍微复杂一点

 在 city, name 上建立一个联合索引，`select city, name, age from user_profile where city in ('杭州','上海') order by name limit 100` 该如何工作？又该怎么优化呢？ 

这个语句是需要排序的，也就是 using filesort，工作方式和上图是类似的。如何避免呢？需要把这个SQL拆成两部分：

`select city, name, age from user_profile where city = '杭州' order by name limit 100` 和

`select city, name, age from user_profile where city = '上海' order by name limit 100` 

然后使用归并排序，取出前 100。

或者

```sql
select * from (
  select * from t where city = '杭州' limit 100
  union all
  select * from t where city = '苏州' limit 100
) as tt order by name limit 100
```

