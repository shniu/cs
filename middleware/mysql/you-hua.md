# 优化

### 查询

#### count 优化

![count &#x7684;&#x5DE5;&#x4F5C;&#x8FC7;&#x7A0B;&#xFF08;&#x7B80;&#x7248;&#xFF09;](../../.gitbook/assets/image%20%2841%29.png)

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

`select city, name, age from user_profile where city = '上海' order by name limit 1000` 是如何工作的？

![Order by](../../.gitbook/assets/image%20%2842%29.png)

![&#x4F18;&#x5316;&#x65B9;&#x5411;](../../.gitbook/assets/image%20%2838%29.png)



