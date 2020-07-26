# MySQL 命令

* DDL and DML

```bash
// 建库语句
CREATE DATABASE mydatabase CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
```

* MySQL 命令行

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

* 事务相关

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

# 查看隔离级别
mysql> show variables like 'transaction_isolation';

mysql> select @@tx_isolation;

# 设置事务隔离级别
mysql> SET session TRANSACTION ISOLATION LEVEL READ COMMITTED;
```

* 锁相关

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

* mysqld 相关

```bash
### 查看状态
# 查看表的详细信息
mysql> show table status like 'account' \G;

### 查看进程信息
mysql> show processlist;
```

* Innodb 相关

```bash
###
# 查看 Innodb 的状态
mysql> show engine innodb status \G;

# 查看 undo 信息
mysql> show varibales like 'innodb_undo%';

### innodb 的一些参数

# purge 相关
innodb_purge_batch_size
```

* 主从相关

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

