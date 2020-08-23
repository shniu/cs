# MySQL Command

每个MySQL程序都有许多不同的选项。大多数程序提供了一个--help选项，你可以查看该程序支持的全部启动选项以及它们的默认值。例如，使用`mysql --help`可以看到`mysql`程序支持的启动选项，`mysqld_safe --help`可以看到`mysqld_safe`程序支持的启动选项。查看`mysqld`支持的启动选项有些特别，需要使用`mysqld --verbose --help`。

### 插入数据的存储过程

```sql
CREATE TABLE `words` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `word` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB;

delimiter ;;
create procedure idata()
begin
  declare i int;
  set i=0;
  while i<10000 do
    insert into words(word) values(concat(char(97+(i div 1000)), char(97+(i % 1000 div 100)), char(97+(i % 100 div 10)), char(97+(i % 10))));
    set i=i+1;
  end while;
end;;
delimiter ;

call idata();
```

### DDL and DML

```sql
// 建库语句
CREATE DATABASE mydatabase CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
```

### 表相关

```sql
-- 查看索引信息
show index from t;

-- 查看表
desc user;

-- 
show table status like 'user';

-- 更新表的索引统计信息
analyze table

-- 
show create table test;
```

### MySQL 命令行

```bash
### mysqld
# 初始化 mysql server 数据目录
$ bin/mysqld --defaults-file=./my.cnf --initialize --user=dfg
# 后台启动 mysql server
$ bin/mysqld --defaults-file=./my.cnf &

### mysql
# 连接 MySQL Server
$ bin/mysql -h 127.0.0.1 -P 3306 -u root -p

# 导入数据
$ bin/mysql -h 127.0.0.1 -P 3306 -u root -p < test.dump

### mysqladmin

### mysqldump
```

### 事务相关

```bash
# 登录MySQL后执行
### 事务通用

# 开启一个事务，在执行到第一个语句时才真正启动成功
#  一致性视图是在执行第一个快照读语句时创建的
mysql> start transaction;
mysql> begin;

# 开启一个事务
# 一致性视图是在执行 start transaction with consistent snapshot 时创建的
mysql> start transaction with consistent snapshot;

```

#### 隔离级别

```bash
# 查看隔离级别
mysql> show variables like 'transaction_isolation';

# 查询 session 级别的变量值
mysql> select @@tx_isolation;

# 查询 global 级别的变量值
mysql> select @@global.tx_isolation;

# 设置事务隔离级别 （会话级别）
mysql> SET session TRANSACTION ISOLATION LEVEL READ COMMITTED;
# or
mysql> set @@session.tx_isolation = 'READ-COMMITTED';
# or
mysql> set transaction isolation level read committed;

# 设置全局的隔离级别
mysql> set @@global.tx_isolation = 'READ-COMMITTED';
mysql> set global transaction isolation level read committed;

# 通过sql查询事务隔离级别
mysql> select * from information_schema.INNODB_TRX \G;
```

#### 锁相关

```bash
### 锁
# 查询时加锁

# 对当前记录加 X 锁，任何其他语句都需要阻塞等待
mysql> select * from user where uid = 101 for update;

# 对当前记录加 S 锁
mysql> select * from user where uid = 101 lock in share mode;

# 关闭 Gap lock 的方式
# 1. 事务隔离级别设置为 READ COMMITED
# 2. 参数 innodb_locks_unsafe_for_binlog 设置为 1，但是这种方式不推荐，在未来的版本这个参数被废弃了


```

### mysqld 相关

```bash
### 查看状态
# 查看表的详细信息
mysql> show table status like 'account' \G;

### 查看进程信息
mysql> show processlist;
```

### Innodb 相关

```bash
###
# 查看 Innodb 的状态
mysql> show engine innodb status \G;

# 查看 undo 信息
mysql> show varibales like 'innodb_undo%';

### innodb 的一些参数

# innodb 缓冲区大小，如 1G
innodb_buffer_pool_size

# 每个表都是使用1个独立的表空间，默认是 1
innodb_file_per_table

# redo log
innodb_log_file_size
innodb_log_files_in_group

# purge 相关
innodb_purge_batch_size

# 控制重做日志（redo log）的写盘和落盘策略, 推荐设置为 1
#  http://mysql.taobao.org/monthly/2014/08/02/
innodb_flush_log_at_trx_commit
innodb_use_global_flush_log_at_trx_commit

# 在准备刷一个脏页的时候，如果这个数据页旁边的数据页刚好是脏页，就会把这个“邻居”也带着一起刷掉；
# 而且这个把“邻居”拖下水的逻辑还可以继续蔓延，也就是对于每个邻居数据页，
# 如果跟它相邻的数据页也还是脏页的话，也会被放到一起刷。
# 当使用 IOPS 较高的 SSD 盘时，可以考虑关掉，设置为 0
innodb_flush_neighbors
```

### 主从相关

```bash
# 查看 Master 状态
mysql> show master status;

# 查看 Slave 状态
mysql> show slave status;

# 查看 Slave 主机
mysql> show slave hosts;

# 查看 binlog 文件
mysql> show binary logs;

# 查看第一个 binlog 中的内容
mysql> show binlog events;

# 查看指定文件的 binlog 内容
mysql> show binlog events in 'mysql-bin.000001';

# 启动复制
mysql> start slave;

# 停止复制
mysql> stop slave;
```

