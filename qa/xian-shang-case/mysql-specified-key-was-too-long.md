# MySQL - Specified key was too long

## 现象描述

在更新 Prod 环境的数据库时，运行了一个 alter table 语句，MySQL 报错：`1071 - Specified key was too long; max key length is 767 bytes` , 经过排查数据里的数据、字段和索引，并没有发现太明显的异常。

从错误信息来看，和索引是有关系的；但是本地数据库、test 环境数据库、uat 环境数据库都是正常的，只有 Prod 是有问题的；进过查询资料，定位到一个 MySQL 的配置参数可能和这个问题有关系

## 排错

在每个环境中查询 MySQL 参数：`innodb_large_prefix`

```sql
-- Test
show variables like 'innodb_large_prefix';
-- result: ON

-- UAT
show variables like 'innodb_large_prefix';
-- result: ON

-- Prod
show variables like 'innodb_large_prefix';
-- Result: OFF
```

可见不同环境的配置是不一样，这个参数的作用是控制单列索引的最大长度的

* 对于 Innodb 存储引擎，innodb\_large\_prefix = OFF 时最大的单列索引长度是 767 Bytes，这里要注意是字节，我们在指定列的类型时一般是 char 或者 varchar，在 utf8 编码下，一个字符 3 Bytes，在 utf8mb4 编码下，一个字符是 4 bytes
* 对于 MyISAM 存储引擎，单列索引的最大长度是 1000 Bytes

而在我们自己的表定义中，有一个 `uniqueId varchar(200)` 的字段被用作了索引，而且编码是 utf8mb4，那么索引的理论长度是 800，超过了 767 Bytes。在 Prod 环境中发生这个问题，可能的原因：

1. innodb\_large\_prefix 以前是 ON 的，但后来被改成了 OFF，这样就会导致再次修改表结构时报错，增删改查数据不受影响
2. 其他原因暂时想不到了

## 原理分析

已知结论：

1. 对于 Innodb 存储引擎，当 innodb\_large\_prefix = OFF 时，单列索引的最大长度时 767 Bytes；当 innodb\_large\_prefix = ON 时，单列索引的最当长度可以到 3072 Bytes （还有地方说开启 innodb\_large\_prefix 后的索引长度增大，只针对 row format 是 DYNAMIC 和 COMPRESSED
2. 对于 Innodb 存储引擎，联合索引的最大长度都是 3072 Bytes
3. 由于最大长度限制的是字节数，所以不同编码下，索引列的最大长度是不一样的，比如 utf8 编码下，innodb\_large\_prefix = OFF 时，最大的 varchar 长度是 255，而 utf8mb4 最大的 varchar 长度就是 191
4. innodb\_large\_prefix 是 MySQL 5.5 以后引入的，如果想要在开启 innodb\_large\_prefix 后索引长度能支持到 3072 Bytes，还必须保证 MySQL 的 file format 设置（使用 Barracuda）和 row format 设置 （使用 DYNAMIC 和 COMPRESSED）
5. 小结一下，innodb\_large\_prefix=1 并且 innodb\_file\_format=BARRACUDA 时，对于 row\_format 为 dynamic 或 compressed 的表可以指定索引列长度大于 767 bytes。但是索引列总长度的不能大于 3072 bytes的限制仍然存在

MySQL 官方文档的描述：

> if `innodb_large_prefix` is enabled \(the default\), the index key prefix limit is 3072 bytes for `InnoDB` tables that use the [`DYNAMIC`](https://dev.mysql.com/doc/refman/5.7/en/glossary.html#glos_dynamic_row_format) or [`COMPRESSED`](https://dev.mysql.com/doc/refman/5.7/en/glossary.html#glos_compressed_row_format) row format. If `innodb_large_prefix` is disabled, the index key prefix limit is 767 bytes for tables of any row format.
>
> `innodb_large_prefix` is deprecated; expect it to be removed in a future MySQL release. `innodb_large_prefix` was introduced in MySQL 5.5 to disable large index key prefixes for compatibility with earlier versions of `InnoDB` that do not support large index key prefixes.
>
> The index key prefix length limit is 767 bytes for `InnoDB` tables that use the [`REDUNDANT`](https://dev.mysql.com/doc/refman/5.7/en/glossary.html#glos_redundant_row_format) or [`COMPACT`](https://dev.mysql.com/doc/refman/5.7/en/glossary.html#glos_compact_row_format) row format.
>
> For example, you might hit this limit with a [column prefix](https://dev.mysql.com/doc/refman/5.7/en/glossary.html#glos_column_prefix) index of more than 255 characters on a `TEXT` or `VARCHAR` column, assuming a `utf8mb3` character set and the maximum of 3 bytes for each character.Attempting to use an index key prefix length that exceeds the limit returns an error.
>
> To avoid such errors in replication configurations, avoid enabling `innodb_large_prefix` on the source if it cannot also be enabled on replicas.If you reduce the `InnoDB` [page size](https://dev.mysql.com/doc/refman/5.7/en/glossary.html#glos_page_size) to 8KB or 4KB by specifying the `innodb_page_size` option when creating the MySQL instance, the maximum length of the index key is lowered proportionally, based on the limit of 3072 bytes for a 16KB page size. That is, the maximum index key length is 1536 bytes when the page size is 8KB, and 768 bytes when the page size is 4KB.
>
> The limits that apply to index key prefixes also apply to full-column index keys.
>
> -- via: [**https://dev.mysql.com/doc/refman/5.7/en/innodb-limits.html**](https://dev.mysql.com/doc/refman/5.7/en/innodb-limits.html)
>
> -- via: [**https://dev.mysql.com/doc/refman/5.7/en/innodb-parameters.html\#sysvar\_innodb\_large\_prefix**](https://dev.mysql.com/doc/refman/5.7/en/innodb-parameters.html#sysvar_innodb_large_prefix)

```bash
# 查看数据库的默认编码配置
mysql> show variables like 'char%';

+--------------------------+---------------------------------------------------------------------------+
| Variable_name            | Value                                                                     |
+--------------------------+---------------------------------------------------------------------------+
| character_set_client     | utf8                                                                      |
| character_set_connection | utf8                                                                      |
| character_set_database   | utf8mb4                                                                   |
| character_set_filesystem | binary                                                                    |
| character_set_results    | utf8                                                                      |
| character_set_server     | utf8mb4                                                                   |
| character_set_system     | utf8                                                                      |
| character_sets_dir       | /Users/dfg/workspace/middleware/mysql/mysql-5.7.30-master/share/charsets/ |
+--------------------------+---------------------------------------------------------------------------+
8 rows in set (0.01 sec)

# 查看数据库 test 的编码
mysql> show create database test;
+----------+---------------------------------------------------------------------------------------------+
| Database | Create Database                                                                             |
+----------+---------------------------------------------------------------------------------------------+
| test     | CREATE DATABASE `test` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */ |
+----------+---------------------------------------------------------------------------------------------+
1 row in set (0.00 sec)


mysql> CREATE TABLE `events_2` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `uniqueId` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `type` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL,
  `key1` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

mysql> SET GLOBAL innodb_large_prefix = 0;
Query OK, 0 rows affected, 1 warning (0.00 sec)

mysql> show variables like 'innodb_large_prefix';
+---------------------+-------+
| Variable_name       | Value |
+---------------------+-------+
| innodb_large_prefix | OFF   |
+---------------------+-------+
1 row in set (0.01 sec)

mysql> alter table events_2 add unique key uni_unique_id (uniqueId);
ERROR 1071 (42000): Specified key was too long; max key length is 767 bytes

mysql> show create table events_3\G;
*************************** 1. row ***************************
       Table: events_3
Create Table: CREATE TABLE `events_3` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `uniqueId` varchar(200) COLLATE utf8_unicode_ci NOT NULL,
  `type` varchar(32) COLLATE utf8_unicode_ci NOT NULL,
  `key1` varchar(200) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci

mysql> alter table events_3 add unique key uni_unique_id (uniqueId);
Query OK, 0 rows affected (0.02 sec)

mysql> show index from events_3;
+----------+------------+---------------+--------------+-------------+-----------+-------------+----------+--------+------+------------+---------+---------------+
| Table    | Non_unique | Key_name      | Seq_in_index | Column_name | Collation | Cardinality | Sub_part | Packed | Null | Index_type | Comment | Index_comment |
+----------+------------+---------------+--------------+-------------+-----------+-------------+----------+--------+------+------------+---------+---------------+
| events_3 |          0 | PRIMARY       |            1 | id          | A         |           0 |     NULL | NULL   |      | BTREE      |         |               |
| events_3 |          0 | uni_unique_id |            1 | uniqueId    | A         |           0 |     NULL | NULL   |      | BTREE      |         |               |
+----------+------------+---------------+--------------+-------------+-----------+-------------+----------+--------+------+------------+---------+---------------+

mysql> alter table events_3 character set utf8mb4 collate utf8mb4_unicode_ci;
Query OK, 0 rows affected (0.02 sec)

mysql> show create table events_3\G;
*************************** 1. row ***************************
       Table: events_3
Create Table: CREATE TABLE `events_3` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `uniqueId` varchar(200) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `type` varchar(32) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `key1` varchar(200) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uni_unique_id` (`uniqueId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci

mysql> show variables like 'innodb_file_format';
+--------------------+-----------+
| Variable_name      | Value     |
+--------------------+-----------+
| innodb_file_format | Barracuda |
+--------------------+-----------+
1 row in set (0.16 sec)

mysql> show variables like '%row_format%';
+---------------------------+---------+
| Variable_name             | Value   |
+---------------------------+---------+
| innodb_default_row_format | dynamic |
+---------------------------+---------+

mysql> show variables like 'innodb_file_per_table';
+-----------------------+-------+
| Variable_name         | Value |
+-----------------------+-------+
| innodb_file_per_table | ON    |
+-----------------------+-------+
1 row in set (0.26 sec)
```

```text
CREATE TABLE `events_1` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `uniqueId` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `type` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL,
  `key1` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci

CREATE TABLE `events_2` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `uniqueId` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `type` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL,
  `key1` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci

CREATE TABLE `events_3` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `uniqueId` varchar(200) NOT NULL,
  `type` varchar(32) NOT NULL,
  `key1` varchar(200) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;


alter table events_1 add unique key uni_unique_id (uniqueId);

show variables like 'innodb_large_prefix';

SET GLOBAL innodb_large_prefix = 0;
```

## 参考链接

1. [**RDS 提示 1071 错误**](https://help.aliyun.com/knowledge_detail/41707.html)
2. [**https://stackoverflow.com/questions/1814532/1071-specified-key-was-too-long-max-key-length-is-767-bytes**](https://stackoverflow.com/questions/1814532/1071-specified-key-was-too-long-max-key-length-is-767-bytes)

